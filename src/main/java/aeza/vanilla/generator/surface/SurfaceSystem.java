package aeza.vanilla.generator.surface;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import aeza.vanilla.generator.climate.MultiNoiseBiomeSource;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class SurfaceSystem {

    private final MultiNoiseBiomeSource biomeSource;

    public SurfaceSystem(MultiNoiseBiomeSource biomeSource) {
        this.biomeSource = biomeSource;
    }

    public void applySurface(FullChunk chunk, SplittableRandom random, int chunkX, int chunkZ) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            int worldX = baseX + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = baseZ + z;
                int biome = chunk.getBiomeId(x, z);

                int depth = -1;

                for (int y = 319; y >= -64; y--) {
                    int mat = chunk.getBlockId(x, y, z);

                    if (mat == BlockID.AIR || mat == BlockID.WATER || mat == BlockID.STILL_WATER) {
                        depth = -1;
                    } else if (mat == BlockID.STONE || mat == BlockID.DEEPSLATE) {
                        if (depth == -1) {
                            depth = 4 + random.nextInt(2);

                            // Apply Top Surface Block
                            applyTopBlock(chunk, random, x, y, z, biome);
                        } else if (depth > 0) {
                            depth--;
                            applyUnderBlock(chunk, random, x, y, z, biome, depth, y);
                        }
                    }
                }
            }
        }
    }

    private void applyTopBlock(FullChunk chunk, SplittableRandom random, int x, int y, int z, int biome) {
        boolean isUnderwater = y < 63;

        if (isUnderwater) {
            // Underwater Seabed Surface
            if (biome == BiomeIds.WARM_OCEAN || biome == BiomeIds.LUKEWARM_OCEAN || biome == BiomeIds.DESERT) {
                chunk.setBlockId(x, y, z, BlockID.SAND);
            } else if (biome == BiomeIds.DEEP_OCEAN || biome == BiomeIds.COLD_DEEP_OCEAN || biome == BiomeIds.WARM_DEEP_OCEAN) {
                chunk.setBlockId(x, y, z, random.nextDouble() < 0.7 ? BlockID.GRAVEL : BlockID.SAND);
            } else {
                chunk.setBlockId(x, y, z, random.nextBoolean() ? BlockID.GRAVEL : BlockID.DIRT);
            }
            return;
        }

        // 1. Badlands (Mesa / Eroded / Wooded)
        if (isBadlands(biome)) {
            if (biome == BiomeIds.MESA_PLATEAU) {
                chunk.setBlockId(x, y, z, BlockID.GRASS);
            } else {
                chunk.setBlock(x, y, z, BlockID.SAND, 1); // Red Sand (meta 1)
            }
            return;
        }

        // 2. Desert Biomes
        if (biome == BiomeIds.DESERT || biome == BiomeIds.DESERT_HILLS || biome == BiomeIds.DESERT_MUTATED) {
            chunk.setBlockId(x, y, z, BlockID.SAND);
            return;
        }

        // 3. Stony Peaks
        if (biome == BiomeIds.STONY_PEAKS) {
            if (random.nextDouble() < 0.35) {
                chunk.setBlockId(x, y, z, BlockID.CALCITE);
            } else if (random.nextDouble() < 0.25) {
                chunk.setBlockId(x, y, z, BlockID.GRAVEL);
            } else {
                chunk.setBlockId(x, y, z, BlockID.STONE);
            }
            return;
        }

        // 4. Frozen Peaks & Jagged Peaks
        if (biome == BiomeIds.FROZEN_PEAKS) {
            chunk.setBlockId(x, y, z, (y > 110 && random.nextBoolean()) ? BlockID.PACKED_ICE : BlockID.SNOW_BLOCK);
            return;
        }
        if (biome == BiomeIds.JAGGED_PEAKS || biome == BiomeIds.SNOWY_SLOPES) {
            chunk.setBlockId(x, y, z, BlockID.SNOW_BLOCK);
            return;
        }
        if (biome == BiomeIds.ICE_PLAINS || biome == BiomeIds.COLD_TAIGA) {
            chunk.setBlockId(x, y, z, BlockID.GRASS);
            if (y + 1 < 320 && chunk.getBlockId(x, y + 1, z) == BlockID.AIR) {
                chunk.setBlockId(x, y + 1, z, BlockID.SNOW_LAYER);
            }
            return;
        }

        // 5. Mushroom Island
        if (biome == BiomeIds.MUSHROOM_ISLAND || biome == BiomeIds.MUSHROOM_ISLAND_SHORE) {
            chunk.setBlockId(x, y, z, BlockID.MYCELIUM);
            return;
        }

        // 6. Beaches
        if (biome == BiomeIds.BEACH || biome == BiomeIds.COLD_BEACH) {
            chunk.setBlockId(x, y, z, BlockID.SAND);
            return;
        }

        // 7. Normal Biomes (Grass Block)
        chunk.setBlockId(x, y, z, BlockID.GRASS);
    }

    private void applyUnderBlock(FullChunk chunk, SplittableRandom random, int x, int y, int z, int biome, int depthRemaining, int currentY) {
        if (currentY < 63) {
            // Underwater sub-layers: Gravel, Sand, or Sandstone
            if (biome == BiomeIds.WARM_OCEAN || biome == BiomeIds.LUKEWARM_OCEAN || biome == BiomeIds.DESERT) {
                chunk.setBlockId(x, y, z, depthRemaining > 1 ? BlockID.SAND : BlockID.SANDSTONE);
            } else {
                chunk.setBlockId(x, y, z, random.nextDouble() < 0.6 ? BlockID.GRAVEL : BlockID.STONE);
            }
            return;
        }

        if (isBadlands(biome)) {
            int bandColor = getTerracottaBandColor(currentY);
            if (bandColor == 0) {
                chunk.setBlockId(x, y, z, BlockID.TERRACOTTA);
            } else {
                chunk.setBlock(x, y, z, BlockID.STAINED_TERRACOTTA, bandColor);
            }
            return;
        }

        if (biome == BiomeIds.DESERT || biome == BiomeIds.DESERT_HILLS || biome == BiomeIds.DESERT_MUTATED) {
            if (depthRemaining > 1) {
                chunk.setBlockId(x, y, z, BlockID.SAND);
            } else {
                chunk.setBlockId(x, y, z, BlockID.SANDSTONE);
            }
            return;
        }

        if (biome == BiomeIds.FROZEN_PEAKS || biome == BiomeIds.JAGGED_PEAKS) {
            chunk.setBlockId(x, y, z, (currentY > 100 && random.nextDouble() < 0.4) ? BlockID.PACKED_ICE : BlockID.STONE);
            return;
        }

        if (biome == BiomeIds.BEACH || biome == BiomeIds.COLD_BEACH) {
            chunk.setBlockId(x, y, z, BlockID.SANDSTONE);
            return;
        }

        chunk.setBlockId(x, y, z, BlockID.DIRT);
    }

    private boolean isBadlands(int biome) {
        return biome == BiomeIds.MESA || biome == BiomeIds.MESA_BRYCE || biome == BiomeIds.MESA_PLATEAU || biome == BiomeIds.MESA_PLATEAU_STONE;
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
            default -> 0;    // Natural Hardened Terracotta
        };
    }
}
