package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.generator.object.tree.ObjectBigSpruceTree;
import cn.nukkit.level.generator.object.tree.ObjectCherryTree;
import cn.nukkit.level.generator.object.tree.ObjectDarkOakTree;
import cn.nukkit.level.generator.object.tree.ObjectJungleBigTree;
import cn.nukkit.level.generator.object.tree.ObjectSavannaTree;
import cn.nukkit.level.generator.object.tree.ObjectSwampTree;
import cn.nukkit.level.generator.object.tree.ObjectTree;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.SplittableRandom;

public class OverworldBiomeDecorator extends Populator {

    private final ObjectDarkOakTree darkOakTree = new ObjectDarkOakTree();
    private final ObjectSavannaTree savannaTree = new ObjectSavannaTree();
    private final ObjectSwampTree swampTree = new ObjectSwampTree();
    private final ObjectCherryTree cherryTree = new ObjectCherryTree();
    private final ObjectBigSpruceTree bigSpruceTree = new ObjectBigSpruceTree(0.6f, 3, true);
    private final ObjectJungleBigTree bigJungleTree = new ObjectJungleBigTree(12, 14, Block.get(BlockID.JUNGLE_LOG), Block.get(BlockID.LEAVES, 3));
    private final BigMushroom brownMushroom = new BigMushroom(BigMushroom.BROWN);
    private final BigMushroom redMushroom = new BigMushroom(BigMushroom.RED);

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        int biomeId = chunk.getBiomeId(8, 8);
        NukkitRandom nRand = new NukkitRandom(random.nextLong());

        // 1. Trees & Woody Plants
        int treeCount = getTreeCountForBiome(biomeId, random);
        for (int i = 0; i < treeCount; i++) {
            int localX = random.nextInt(16);
            int localZ = random.nextInt(16);
            int x = baseX + localX;
            int z = baseZ + localZ;

            int y = findSurfaceY(chunk, localX, localZ);
            if (y >= 62 && y <= 250) {
                int ground = chunk.getBlockId(localX, y, localZ);
                if (ground == BlockID.GRASS || ground == BlockID.DIRT || ground == BlockID.PODZOL) {
                    Vector3 pos = new Vector3(x, y + 1, z);
                    growBiomeTree(world, nRand, random, pos, x, y + 1, z, biomeId);
                }
            }
        }

        // 2. Flowers, Grass & Foliage
        int flowerCount = getFoliageCountForBiome(biomeId, random);
        for (int i = 0; i < flowerCount; i++) {
            int localX = random.nextInt(16);
            int localZ = random.nextInt(16);
            int x = baseX + localX;
            int z = baseZ + localZ;

            int y = findSurfaceY(chunk, localX, localZ);
            if (y >= 62 && y <= 250) {
                int ground = chunk.getBlockId(localX, y, localZ);
                int above = chunk.getBlockId(localX, y + 1, localZ);

                if (above == BlockID.AIR && (ground == BlockID.GRASS || ground == BlockID.DIRT)) {
                    placeBiomeFlora(world, random, x, y + 1, z, biomeId);
                }
            }
        }

        // 3. Cacti & Dead Bushes for Deserts / Badlands
        if (biomeId == BiomeIds.DESERT || biomeId == BiomeIds.DESERT_HILLS || biomeId == BiomeIds.MESA || biomeId == BiomeIds.MESA_BRYCE) {
            for (int i = 0; i < 4; i++) {
                int localX = random.nextInt(16);
                int localZ = random.nextInt(16);
                int x = baseX + localX;
                int z = baseZ + localZ;

                int y = findSurfaceY(chunk, localX, localZ);
                if (y >= 62) {
                    int ground = chunk.getBlockId(localX, y, localZ);
                    if (ground == BlockID.SAND) {
                        if (random.nextInt(3) == 0) {
                            int height = 1 + random.nextInt(3);
                            for (int h = 1; h <= height; h++) {
                                world.setBlockAt(x, y + h, z, BlockID.CACTUS, 0);
                            }
                        } else {
                            world.setBlockAt(x, y + 1, z, BlockID.DEAD_BUSH, 0);
                        }
                    }
                }
            }
        }
    }

    private int findSurfaceY(FullChunk chunk, int localX, int localZ) {
        for (int y = 250; y >= 60; y--) {
            int id = chunk.getBlockId(localX, y, localZ);
            if (id != BlockID.AIR && id != BlockID.LEAVES && id != BlockID.LEAVES2 && id != BlockID.OAK_LOG && id != BlockID.ACACIA_LOG && id != BlockID.TALL_GRASS) {
                return y;
            }
        }
        return 0;
    }

    private int getTreeCountForBiome(int biomeId, SplittableRandom random) {
        return switch (biomeId) {
            case BiomeIds.FOREST, BiomeIds.FLOWER_FOREST -> 4 + random.nextInt(3);
            case BiomeIds.BIRCH_FOREST -> 5 + random.nextInt(3);
            case BiomeIds.ROOFED_FOREST -> 6 + random.nextInt(4);
            case BiomeIds.TAIGA, BiomeIds.COLD_TAIGA -> 5 + random.nextInt(4);
            case BiomeIds.MEGA_TAIGA -> 6 + random.nextInt(4);
            case BiomeIds.JUNGLE, BiomeIds.JUNGLE_HILLS -> 7 + random.nextInt(5);
            case BiomeIds.SAVANNA, BiomeIds.SAVANNA_PLATEAU -> random.nextInt(4) == 0 ? 1 : 0;
            case BiomeIds.PLAINS, BiomeIds.SUNFLOWER_PLAINS -> random.nextInt(12) == 0 ? 1 : 0;
            case BiomeIds.SWAMPLAND -> 2 + random.nextInt(3);
            case BiomeIds.CHERRY_GROVE -> 4 + random.nextInt(3);
            default -> 0;
        };
    }

    private int getFoliageCountForBiome(int biomeId, SplittableRandom random) {
        return switch (biomeId) {
            case BiomeIds.PLAINS, BiomeIds.SUNFLOWER_PLAINS -> 16 + random.nextInt(8);
            case BiomeIds.FLOWER_FOREST, BiomeIds.MOUNTAIN_MEADOW -> 24 + random.nextInt(12);
            case BiomeIds.FOREST, BiomeIds.BIRCH_FOREST -> 12 + random.nextInt(6);
            case BiomeIds.TAIGA, BiomeIds.COLD_TAIGA -> 8 + random.nextInt(4);
            case BiomeIds.JUNGLE -> 16 + random.nextInt(8);
            case BiomeIds.SAVANNA -> 8 + random.nextInt(6);
            case BiomeIds.SWAMPLAND -> 8 + random.nextInt(6);
            default -> 2;
        };
    }

    private void growBiomeTree(ChunkManager world, NukkitRandom nRand, SplittableRandom random, Vector3 pos, int x, int y, int z, int biomeId) {
        switch (biomeId) {
            case BiomeIds.BIRCH_FOREST -> {
                if (random.nextInt(3) == 0) {
                    ObjectTree.growTree(world, x, y, z, nRand, BlockSapling.BIRCH_TALL);
                } else {
                    ObjectTree.growTree(world, x, y, z, nRand, BlockSapling.BIRCH);
                }
            }
            case BiomeIds.ROOFED_FOREST -> {
                if (random.nextInt(5) == 0) {
                    if (random.nextBoolean()) {
                        redMushroom.generate(world, nRand, pos);
                    } else {
                        brownMushroom.generate(world, nRand, pos);
                    }
                } else {
                    darkOakTree.generate(world, nRand, pos);
                }
            }
            case BiomeIds.TAIGA, BiomeIds.COLD_TAIGA -> ObjectTree.growTree(world, x, y, z, nRand, BlockSapling.SPRUCE);
            case BiomeIds.MEGA_TAIGA -> {
                if (random.nextInt(3) == 0) {
                    bigSpruceTree.placeObject(world, x, y, z, nRand);
                } else {
                    ObjectTree.growTree(world, x, y, z, nRand, BlockSapling.SPRUCE);
                }
            }
            case BiomeIds.JUNGLE, BiomeIds.JUNGLE_HILLS -> {
                if (random.nextInt(3) == 0) {
                    bigJungleTree.generate(world, nRand, pos);
                } else {
                    ObjectTree.growTree(world, x, y, z, nRand, BlockSapling.JUNGLE);
                }
            }
            case BiomeIds.SAVANNA, BiomeIds.SAVANNA_PLATEAU -> savannaTree.generate(world, nRand, pos);
            case BiomeIds.SWAMPLAND -> swampTree.generate(world, nRand, pos);
            case BiomeIds.CHERRY_GROVE -> cherryTree.generate(world, nRand, pos);
            default -> ObjectTree.growTree(world, x, y, z, nRand, BlockSapling.OAK);
        }
    }

    private void placeBiomeFlora(ChunkManager world, SplittableRandom random, int x, int y, int z, int biomeId) {
        if (biomeId == BiomeIds.FLOWER_FOREST || biomeId == BiomeIds.MOUNTAIN_MEADOW) {
            int flowerMeta = random.nextInt(9);
            world.setBlockAt(x, y, z, BlockID.POPPY, flowerMeta);
        } else if (biomeId == BiomeIds.SUNFLOWER_PLAINS && random.nextInt(3) == 0) {
            world.setBlockAt(x, y, z, BlockID.DOUBLE_PLANT, 0);
            world.setBlockAt(x, y + 1, z, BlockID.DOUBLE_PLANT, 8);
        } else {
            if (random.nextInt(5) == 0) {
                world.setBlockAt(x, y, z, random.nextBoolean() ? BlockID.DANDELION : BlockID.POPPY, 0);
            } else {
                world.setBlockAt(x, y, z, BlockID.TALL_GRASS, 1);
            }
        }
    }
}
