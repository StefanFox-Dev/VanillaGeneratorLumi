package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class CaveBiomePopulator extends Populator {

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int biome = chunk.getBiomeId(x, z);

                // Populate underground cave biomes from Y = 5 to 55
                for (int y = 5; y <= 55; y++) {
                    int cur = chunk.getBlockId(x, y, z);

                    if (cur == BlockID.AIR) {
                        int below = chunk.getBlockId(x, y - 1, z);
                        int above = chunk.getBlockId(x, y + 1, z);

                        // 1. Lush Caves (Moss, Azalea, Glow Berries, Vines)
                        if (biome == BiomeIds.LUSH_CAVES) {
                            if (below == BlockID.STONE || below == BlockID.DEEPSLATE || below == BlockID.DIRT) {
                                chunk.setBlockId(x, y - 1, z, BlockID.MOSS_BLOCK);
                                if (random.nextDouble() < 0.12) {
                                    chunk.setBlockId(x, y, z, BlockID.MOSS_CARPET);
                                } else if (random.nextDouble() < 0.03) {
                                    chunk.setBlockId(x, y, z, BlockID.AZALEA);
                                }
                            }
                            if (above == BlockID.STONE || above == BlockID.DEEPSLATE) {
                                if (random.nextDouble() < 0.15) {
                                    int vineLength = 2 + random.nextInt(4);
                                    for (int v = 0; v < vineLength && (y - v) > 2; v++) {
                                        if (chunk.getBlockId(x, y - v, z) == BlockID.AIR) {
                                            chunk.setBlockId(x, y - v, z, BlockID.CAVE_VINES);
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // 2. Dripstone Caves (Stalactites hanging down & Stalagmites rising up)
                        else if (biome == BiomeIds.DRIPSTONE_CAVES) {
                            // Stalactite hanging down MUST attach to solid ceiling block
                            if (above == BlockID.STONE || above == BlockID.DEEPSLATE || above == BlockID.DRIPSTONE_BLOCK) {
                                if (random.nextDouble() < 0.20) {
                                    chunk.setBlockId(x, y + 1, z, BlockID.DRIPSTONE_BLOCK);
                                    int len = 1 + random.nextInt(3);
                                    for (int d = 0; d < len && (y - d) > 2; d++) {
                                        if (chunk.getBlockId(x, y - d, z) == BlockID.AIR) {
                                            chunk.setBlockId(x, y - d, z, BlockID.POINTED_DRIPSTONE);
                                            chunk.setBlockData(x, y - d, z, 0); // Pointing Down
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                            // Stalagmite rising up MUST attach to solid floor block
                            if (below == BlockID.STONE || below == BlockID.DEEPSLATE || below == BlockID.DRIPSTONE_BLOCK) {
                                if (random.nextDouble() < 0.15) {
                                    chunk.setBlockId(x, y - 1, z, BlockID.DRIPSTONE_BLOCK);
                                    chunk.setBlockId(x, y, z, BlockID.POINTED_DRIPSTONE);
                                    chunk.setBlockData(x, y, z, 1); // Pointing Up
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
