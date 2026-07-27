package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import aeza.vanilla.generator.object.tree.GenericTree;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class MountainBiomePopulator extends Populator {

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int biome = chunk.getBiomeId(x, z);
                int worldX = baseX + x;
                int worldZ = baseZ + z;
                int y = chunk.getHighestBlockAt(x, z);

                // Prevent spawning land bushes/flowers underwater
                if (y <= 62) continue;
                int topBlock = chunk.getBlockId(x, y, z);
                if (topBlock == BlockID.WATER || topBlock == BlockID.STILL_WATER) continue;

                int groundBlock = chunk.getBlockId(x, y - 1, z);

                // 1. Frozen Peaks & Jagged Peaks (Ice & Snow Spires)
                if (biome == BiomeIds.FROZEN_PEAKS || biome == BiomeIds.JAGGED_PEAKS) {
                    if (groundBlock == BlockID.STONE || groundBlock == BlockID.SNOW_BLOCK || groundBlock == BlockID.PACKED_ICE) {
                        chunk.setBlockId(x, y, z, BlockID.SNOW_LAYER);
                        if (y > 100) {
                            for (int sy = y - 1; sy >= y - 4 && sy > 0; sy--) {
                                int current = chunk.getBlockId(x, sy, z);
                                if (current == BlockID.STONE || current == BlockID.DIRT) {
                                    chunk.setBlockId(x, sy, z, (biome == BiomeIds.FROZEN_PEAKS && random.nextBoolean()) ? BlockID.PACKED_ICE : BlockID.SNOW_BLOCK);
                                }
                            }
                        }
                    }
                }
                // 2. Snowy Slopes & Mountain Grove (Powder Snow & Dense Spruce Trees)
                else if (biome == BiomeIds.SNOWY_SLOPES || biome == BiomeIds.MOUNTAIN_GROVE) {
                    if (groundBlock == BlockID.GRASS || groundBlock == BlockID.DIRT || groundBlock == BlockID.SNOW_BLOCK) {
                        chunk.setBlockId(x, y, z, BlockID.SNOW_LAYER);
                        if (biome == BiomeIds.SNOWY_SLOPES && random.nextDouble() < 0.15) {
                            chunk.setBlockId(x, y - 1, z, BlockID.POWDER_SNOW);
                        }
                        if (biome == BiomeIds.MOUNTAIN_GROVE && random.nextDouble() < 0.08) {
                            new GenericTree(random).generate(world, random, worldX, y, worldZ);
                        }
                    }
                }
                // 3. Stony Peaks (Calcite, Stone, Gravel)
                else if (biome == BiomeIds.STONY_PEAKS) {
                    if (random.nextDouble() < 0.35) {
                        chunk.setBlockId(x, y - 1, z, BlockID.CALCITE);
                    } else if (random.nextDouble() < 0.25) {
                        chunk.setBlockId(x, y - 1, z, BlockID.GRAVEL);
                    }
                }
                // 4. Mountain Meadow (Flowers & Sweet Berry Bushes on Grass)
                else if (biome == BiomeIds.MOUNTAIN_MEADOW) {
                    if (groundBlock == BlockID.GRASS || groundBlock == BlockID.DIRT) {
                        if (random.nextDouble() < 0.20) {
                            int flowerType = random.nextInt(8);
                            chunk.setBlockId(x, y, z, BlockID.RED_FLOWER);
                            chunk.setBlockData(x, y, z, flowerType);
                        } else if (random.nextDouble() < 0.05) {
                            chunk.setBlockId(x, y, z, BlockID.SWEET_BERRY_BUSH);
                            chunk.setBlockData(x, y, z, 3);
                        }
                    }
                }
                // 5. Windswept Hills & Forest
                else if (biome == BiomeIds.WINDSWEPT_HILLS || biome == BiomeIds.EXTREME_HILLS) {
                    if (groundBlock == BlockID.GRASS && random.nextDouble() < 0.04) {
                        new GenericTree(random).generate(world, random, worldX, y, worldZ);
                    }
                }
            }
        }
    }
}
