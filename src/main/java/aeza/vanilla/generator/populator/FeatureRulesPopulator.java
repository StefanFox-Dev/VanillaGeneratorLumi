package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class FeatureRulesPopulator extends Populator {

    private static final int[] FLOWERS = {
        BlockID.DANDELION, BlockID.POPPY, BlockID.BLUE_ORCHID, BlockID.ALLIUM,
        BlockID.AZURE_BLUET, BlockID.RED_TULIP, BlockID.ORANGE_TULIP, BlockID.WHITE_TULIP,
        BlockID.PINK_TULIP, BlockID.OXEYE_DAISY
    };

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int i = 0; i < 4; i++) {
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int biome = chunk.getBiomeId(x, z);

            if (biome == BiomeIds.OCEAN || biome == BiomeIds.DEEP_OCEAN || biome == BiomeIds.RIVER || biome == BiomeIds.FROZEN_OCEAN) {
                continue;
            }

            int topY = chunk.getHighestBlockAt(x, z);
            if (topY >= 63 && topY < 250) {
                int groundId = chunk.getBlockId(x, topY, z);
                int aboveId = chunk.getBlockId(x, topY + 1, z);

                if (groundId == BlockID.GRASS && aboveId == BlockID.AIR) {
                    double roll = random.nextDouble();

                    // Flowers feature_rules
                    if (roll < 0.15) {
                        int flower = FLOWERS[random.nextInt(FLOWERS.length)];
                        world.setBlockAt(baseX + x, topY + 1, baseZ + z, flower, 0);
                    }
                    // Double plant / Tall grass feature_rules
                    else if (roll < 0.5) {
                        world.setBlockAt(baseX + x, topY + 1, baseZ + z, BlockID.TALL_GRASS, 1);
                    }
                }
            }
        }
    }
}
