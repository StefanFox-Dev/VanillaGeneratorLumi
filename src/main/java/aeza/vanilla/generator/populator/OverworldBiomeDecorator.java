package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class OverworldBiomeDecorator extends Populator {

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        int biomeId = chunk.getBiomeId(8, 8);

        // 1. Trees & Woody Plants
        int treeCount = getTreeCountForBiome(biomeId, random);
        for (int i = 0; i < treeCount; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            int localX = x & 0x0f;
            int localZ = z & 0x0f;

            int y = findSurfaceY(chunk, localX, localZ);
            if (y >= 62 && y <= 250) {
                int ground = chunk.getBlockId(localX, y, localZ);
                if (ground == BlockID.GRASS || ground == BlockID.DIRT || ground == BlockID.PODZOL) {
                    growBiomeTree(world, random, x, y + 1, z, biomeId);
                }
            }
        }

        // 2. Flowers, Grass & Foliage
        int flowerCount = getFoliageCountForBiome(biomeId, random);
        for (int i = 0; i < flowerCount; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            int localX = x & 0x0f;
            int localZ = z & 0x0f;

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
                int x = baseX + random.nextInt(16);
                int z = baseZ + random.nextInt(16);
                int localX = x & 0x0f;
                int localZ = z & 0x0f;

                int y = findSurfaceY(chunk, localX, localZ);
                if (y >= 62) {
                    int ground = chunk.getBlockId(localX, y, localZ);
                    if (ground == BlockID.SAND) {
                        if (random.nextInt(3) == 0) {
                            // Cactus
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
            case BiomeIds.FOREST, BiomeIds.FLOWER_FOREST -> 6 + random.nextInt(4);
            case BiomeIds.BIRCH_FOREST -> 7 + random.nextInt(3);
            case BiomeIds.ROOFED_FOREST -> 12 + random.nextInt(6);
            case BiomeIds.TAIGA, BiomeIds.COLD_TAIGA, BiomeIds.MEGA_TAIGA -> 8 + random.nextInt(5);
            case BiomeIds.JUNGLE, BiomeIds.JUNGLE_HILLS -> 10 + random.nextInt(6);
            case BiomeIds.SAVANNA, BiomeIds.SAVANNA_PLATEAU -> 1 + (random.nextInt(3) == 0 ? 1 : 0);
            case BiomeIds.PLAINS, BiomeIds.SUNFLOWER_PLAINS -> random.nextInt(10) == 0 ? 1 : 0;
            case BiomeIds.SWAMPLAND -> 3 + random.nextInt(3);
            case BiomeIds.CHERRY_GROVE -> 6 + random.nextInt(4);
            default -> 0;
        };
    }

    private int getFoliageCountForBiome(int biomeId, SplittableRandom random) {
        return switch (biomeId) {
            case BiomeIds.PLAINS, BiomeIds.SUNFLOWER_PLAINS -> 16 + random.nextInt(10);
            case BiomeIds.FLOWER_FOREST, BiomeIds.MOUNTAIN_MEADOW -> 24 + random.nextInt(12);
            case BiomeIds.FOREST, BiomeIds.BIRCH_FOREST -> 12 + random.nextInt(8);
            case BiomeIds.TAIGA, BiomeIds.COLD_TAIGA -> 8 + random.nextInt(6);
            case BiomeIds.JUNGLE -> 18 + random.nextInt(8);
            case BiomeIds.SAVANNA -> 10 + random.nextInt(6);
            case BiomeIds.SWAMPLAND -> 8 + random.nextInt(6);
            default -> 2;
        };
    }

    private void growBiomeTree(ChunkManager world, SplittableRandom random, int x, int y, int z, int biomeId) {
        switch (biomeId) {
            case BiomeIds.BIRCH_FOREST -> growBirchTree(world, random, x, y, z);
            case BiomeIds.ROOFED_FOREST -> {
                if (random.nextInt(4) == 0) {
                    growHugeMushroom(world, random, x, y, z);
                } else {
                    growDarkOakTree(world, random, x, y, z);
                }
            }
            case BiomeIds.TAIGA, BiomeIds.COLD_TAIGA, BiomeIds.MEGA_TAIGA -> growSpruceTree(world, random, x, y, z);
            case BiomeIds.JUNGLE, BiomeIds.JUNGLE_HILLS -> growJungleTree(world, random, x, y, z);
            case BiomeIds.SAVANNA, BiomeIds.SAVANNA_PLATEAU -> growAcaciaTree(world, random, x, y, z);
            case BiomeIds.SWAMPLAND -> growSwampTree(world, random, x, y, z);
            default -> growOakTree(world, random, x, y, z);
        }
    }

    private void growOakTree(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        int height = 4 + random.nextInt(3);
        for (int dy = 0; dy < height; dy++) {
            world.setBlockAt(x, y + dy, z, BlockID.OAK_LOG, 0); // Oak log
        }

        // Canopy
        for (int dy = height - 2; dy <= height + 1; dy++) {
            int radius = (dy == height + 1) ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && (dy == height + 1 || random.nextBoolean())) {
                        continue;
                    }
                    int cur = world.getBlockIdAt(x + dx, y + dy, z + dz);
                    if (cur == BlockID.AIR) {
                        world.setBlockAt(x + dx, y + dy, z + dz, BlockID.LEAVES, 0);
                    }
                }
            }
        }
    }

    private void growBirchTree(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        int height = 5 + random.nextInt(3);
        for (int dy = 0; dy < height; dy++) {
            world.setBlockAt(x, y + dy, z, BlockID.OAK_LOG, 2); // Birch log (meta 2)
        }

        for (int dy = height - 2; dy <= height + 1; dy++) {
            int radius = (dy == height + 1) ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && dy == height + 1) continue;
                    int cur = world.getBlockIdAt(x + dx, y + dy, z + dz);
                    if (cur == BlockID.AIR) {
                        world.setBlockAt(x + dx, y + dy, z + dz, BlockID.LEAVES, 2); // Birch leaves
                    }
                }
            }
        }
    }

    private void growDarkOakTree(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        int height = 6 + random.nextInt(3);
        // 2x2 Trunk
        for (int dy = 0; dy < height; dy++) {
            world.setBlockAt(x, y + dy, z, BlockID.ACACIA_LOG, 1); // Dark oak is Acacia_log meta 1 in Bedrock BlockID
            world.setBlockAt(x + 1, y + dy, z, BlockID.ACACIA_LOG, 1);
            world.setBlockAt(x, y + dy, z + 1, BlockID.ACACIA_LOG, 1);
            world.setBlockAt(x + 1, y + dy, z + 1, BlockID.ACACIA_LOG, 1);
        }

        // Broad dark oak canopy
        for (int dy = height - 2; dy <= height + 1; dy++) {
            int radius = 3;
            for (int dx = -radius; dx <= radius + 1; dx++) {
                for (int dz = -radius; dz <= radius + 1; dz++) {
                    int cur = world.getBlockIdAt(x + dx, y + dy, z + dz);
                    if (cur == BlockID.AIR) {
                        world.setBlockAt(x + dx, y + dy, z + dz, BlockID.LEAVES2, 1); // Dark oak leaves
                    }
                }
            }
        }
    }

    private void growSpruceTree(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        int height = 6 + random.nextInt(4);
        for (int dy = 0; dy < height; dy++) {
            world.setBlockAt(x, y + dy, z, BlockID.OAK_LOG, 1); // Spruce log (meta 1)
        }

        int radius = 2;
        for (int dy = height; dy >= 2; dy--) {
            int currentRadius = (dy == height) ? 0 : ((dy % 2 == 0) ? radius : 1);
            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                    if (Math.abs(dx) == currentRadius && Math.abs(dz) == currentRadius && currentRadius > 1) continue;
                    int cur = world.getBlockIdAt(x + dx, y + dy, z + dz);
                    if (cur == BlockID.AIR) {
                        world.setBlockAt(x + dx, y + dy, z + dz, BlockID.LEAVES, 1); // Spruce leaves
                    }
                }
            }
        }
        world.setBlockAt(x, y + height, z, BlockID.LEAVES, 1);
    }

    private void growAcaciaTree(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        int height = 5 + random.nextInt(3);
        int dx = 0;
        int dz = 0;
        for (int dy = 0; dy < height; dy++) {
            if (dy > 2 && dy == height - 2) {
                dx += (random.nextBoolean() ? 1 : -1);
                dz += (random.nextBoolean() ? 1 : -1);
            }
            world.setBlockAt(x + dx, y + dy, z + dz, BlockID.ACACIA_LOG, 0); // Acacia log (meta 0)
        }

        // Flat acacia canopy on top
        int topX = x + dx;
        int topY = y + height;
        int topZ = z + dz;
        for (int cx = -2; cx <= 2; cx++) {
            for (int cz = -2; cz <= 2; cz++) {
                if (Math.abs(cx) == 2 && Math.abs(cz) == 2) continue;
                int cur = world.getBlockIdAt(topX + cx, topY, topZ + cz);
                if (cur == BlockID.AIR) {
                    world.setBlockAt(topX + cx, topY, topZ + cz, BlockID.LEAVES2, 0);
                }
            }
        }
        world.setBlockAt(topX, topY + 1, topZ, BlockID.LEAVES2, 0);
    }

    private void growJungleTree(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        int height = 6 + random.nextInt(5);
        for (int dy = 0; dy < height; dy++) {
            world.setBlockAt(x, y + dy, z, BlockID.OAK_LOG, 3); // Jungle log (meta 3)
        }

        for (int dy = height - 2; dy <= height + 1; dy++) {
            int radius = (dy == height + 1) ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && dy == height + 1) continue;
                    int cur = world.getBlockIdAt(x + dx, y + dy, z + dz);
                    if (cur == BlockID.AIR) {
                        world.setBlockAt(x + dx, y + dy, z + dz, BlockID.LEAVES, 3); // Jungle leaves
                    }
                }
            }
        }
    }

    private void growSwampTree(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        growOakTree(world, random, x, y, z);
        // Add hanging vines
        for (int dy = y + 2; dy <= y + 4; dy++) {
            world.setBlockAt(x + 2, dy, z, BlockID.VINE, 8);
            world.setBlockAt(x - 2, dy, z, BlockID.VINE, 2);
            world.setBlockAt(x, dy, z + 2, BlockID.VINE, 4);
            world.setBlockAt(x, dy, z - 2, BlockID.VINE, 1);
        }
    }

    private void growHugeMushroom(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        int height = 5 + random.nextInt(3);
        int type = random.nextBoolean() ? BlockID.BROWN_MUSHROOM_BLOCK : BlockID.RED_MUSHROOM_BLOCK;

        for (int dy = 0; dy < height; dy++) {
            world.setBlockAt(x, y + dy, z, BlockID.BROWN_MUSHROOM_BLOCK, 10); // Stem
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockAt(x + dx, y + height, z + dz, type, 14);
            }
        }
    }

    private void placeBiomeFlora(ChunkManager world, SplittableRandom random, int x, int y, int z, int biomeId) {
        if (biomeId == BiomeIds.FLOWER_FOREST || biomeId == BiomeIds.MOUNTAIN_MEADOW) {
            int flowerMeta = random.nextInt(9);
            world.setBlockAt(x, y, z, BlockID.POPPY, flowerMeta);
        } else if (biomeId == BiomeIds.SUNFLOWER_PLAINS && random.nextInt(3) == 0) {
            world.setBlockAt(x, y, z, BlockID.DOUBLE_PLANT, 0); // Sunflower bottom
            world.setBlockAt(x, y + 1, z, BlockID.DOUBLE_PLANT, 8); // Sunflower top
        } else {
            if (random.nextInt(5) == 0) {
                world.setBlockAt(x, y, z, random.nextBoolean() ? BlockID.DANDELION : BlockID.POPPY, 0);
            } else {
                world.setBlockAt(x, y, z, BlockID.TALL_GRASS, 1); // Tall grass
            }
        }
    }
}
