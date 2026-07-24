package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class OceanPopulator extends Populator {

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int biome = chunk.getBiomeId(x, z);
                if (biome != BiomeIds.OCEAN && biome != BiomeIds.DEEP_OCEAN && biome != BiomeIds.WARM_OCEAN && biome != BiomeIds.LUKEWARM_OCEAN) {
                    continue;
                }

                int worldX = baseX + x;
                int worldZ = baseZ + z;
                int highestY = chunk.getHighestBlockAt(x, z);

                if (highestY > 30 && highestY < 62) {
                    int topBlock = chunk.getBlockId(x, highestY, z);
                    
                    // Underwater floor decoration (Gravel / Sand)
                    if (topBlock == BlockID.DIRT || topBlock == BlockID.GRASS) {
                        if (random.nextDouble() < 0.6) {
                            chunk.setBlockId(x, highestY, z, BlockID.GRAVEL);
                            topBlock = BlockID.GRAVEL;
                        } else {
                            chunk.setBlockId(x, highestY, z, BlockID.SAND);
                            topBlock = BlockID.SAND;
                        }
                    }

                    int aboveY = highestY + 1;
                    int aboveBlock = chunk.getBlockId(x, aboveY, z);

                    if (aboveBlock == BlockID.WATER || aboveBlock == BlockID.STILL_WATER) {
                        double roll = random.nextDouble();

                        // Populate Kelp (BlockID.BLOCK_KELP = 393)
                        if (roll < 0.12 && (topBlock == BlockID.SAND || topBlock == BlockID.GRAVEL || topBlock == BlockID.DIRT)) {
                            int kelpHeight = 3 + random.nextInt(15);
                            for (int h = 0; h < kelpHeight; h++) {
                                int ky = aboveY + h;
                                if (ky < 62 && (chunk.getBlockId(x, ky, z) == BlockID.STILL_WATER || chunk.getBlockId(x, ky, z) == BlockID.WATER)) {
                                    chunk.setBlock(x, ky, z, BlockID.BLOCK_KELP, 0);
                                } else {
                                    break;
                                }
                            }
                        }
                        // Populate Seagrass (BlockID.SEAGRASS = 385)
                        else if (roll < 0.35 && (topBlock == BlockID.SAND || topBlock == BlockID.DIRT || topBlock == BlockID.GRAVEL)) {
                            if (aboveY < 61 && random.nextDouble() < 0.25 && (chunk.getBlockId(x, aboveY + 1, z) == BlockID.STILL_WATER || chunk.getBlockId(x, aboveY + 1, z) == BlockID.WATER)) {
                                chunk.setBlock(x, aboveY, z, BlockID.SEAGRASS, 2); // Tall seagrass bottom
                                chunk.setBlock(x, aboveY + 1, z, BlockID.SEAGRASS, 1); // Tall seagrass top
                            } else {
                                chunk.setBlock(x, aboveY, z, BlockID.SEAGRASS, 0); // Short seagrass
                            }
                        }
                        // Populate Coral / Sea Pickles in warm oceans
                        else if (biome == BiomeIds.WARM_OCEAN && roll < 0.5) {
                            if (random.nextDouble() < 0.3) {
                                chunk.setBlock(x, aboveY, z, BlockID.SEA_PICKLE, random.nextInt(4));
                            } else {
                                chunk.setBlock(x, aboveY, z, BlockID.CORAL, random.nextInt(5));
                            }
                        }
                    }
                }
            }
        }
    }
}
