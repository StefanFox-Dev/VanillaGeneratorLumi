package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import aeza.vanilla.generator.object.Cactus;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class WarmBiomePopulator extends Populator {

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

                if (y <= 62) continue; // Prevent spawning land flora underwater
                int topBlock = chunk.getBlockId(x, y, z);
                if (topBlock == BlockID.WATER || topBlock == BlockID.STILL_WATER) continue;
                int groundBlock = chunk.getBlockId(x, y - 1, z);

                // 1. Desert Biomes (Cacti & Dead Bushes on Sand)
                if (biome == BiomeIds.DESERT || biome == BiomeIds.DESERT_HILLS || biome == BiomeIds.DESERT_MUTATED) {
                    if (groundBlock == BlockID.SAND) {
                        double roll = random.nextDouble();
                        if (roll < 0.015) {
                            new Cactus().generate(world, random, worldX, y, worldZ);
                        } else if (roll < 0.04) {
                            chunk.setBlockId(x, y, z, BlockID.DEAD_BUSH);
                        }
                    }
                }
                // 2. Badlands, Wooded Badlands & Eroded Badlands (Terracotta Strata & High Gold Ore)
                else if (biome == BiomeIds.MESA || biome == BiomeIds.MESA_BRYCE || biome == BiomeIds.MESA_PLATEAU || biome == BiomeIds.MESA_PLATEAU_STONE) {
                    for (int sy = y - 1; sy >= y - 16 && sy > 0; sy--) {
                        int cur = chunk.getBlockId(x, sy, z);
                        if (cur == BlockID.STONE || cur == BlockID.DIRT || cur == BlockID.GRASS) {
                            int bandColor = getTerracottaBandColor(sy);
                            if (bandColor == 0) {
                                chunk.setBlockId(x, sy, z, BlockID.TERRACOTTA);
                            } else {
                                chunk.setBlockId(x, sy, z, BlockID.STAINED_TERRACOTTA);
                                chunk.setBlockData(x, sy, z, bandColor);
                            }

                            // Extra Gold Ore in Badlands
                            if (random.nextDouble() < 0.06) {
                                chunk.setBlockId(x, sy, z, BlockID.GOLD_ORE);
                            }
                        }
                    }
                }
                // 3. Savanna & Windswept Savanna (Acacia Trees & Tall Grass on Grass)
                else if (biome == BiomeIds.SAVANNA || biome == BiomeIds.SAVANNA_PLATEAU || biome == BiomeIds.WINDSWEPT_SAVANNA) {
                    if (groundBlock == BlockID.GRASS && random.nextDouble() < 0.03) {
                        chunk.setBlockId(x, y, z, BlockID.TALL_GRASS);
                        chunk.setBlockData(x, y, z, 1);
                    }
                }
            }
        }
    }

    private int getTerracottaBandColor(int y) {
        int band = (y % 14);
        return switch (band) {
            case 1, 2 -> 14; // Red
            case 4 -> 1;     // Orange
            case 6, 7 -> 4;  // Yellow
            case 9 -> 0;     // White
            case 11 -> 12;   // Brown
            case 13 -> 8;    // Light Gray
            default -> 0;    // Hardened Terracotta
        };
    }
}
