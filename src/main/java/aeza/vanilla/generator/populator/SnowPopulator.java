package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class SnowPopulator extends Populator {
    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int biome = chunk.getBiomeId(x, z);
                if (biome != BiomeIds.ICE_PLAINS && biome != BiomeIds.ICE_MOUNTAINS 
                        && biome != BiomeIds.COLD_TAIGA && biome != BiomeIds.COLD_TAIGA_HILLS 
                        && biome != BiomeIds.FROZEN_OCEAN && biome != BiomeIds.FROZEN_RIVER) {
                    continue;
                }

                int worldX = baseX + x;
                int worldZ = baseZ + z;
                int topY = chunk.getHighestBlockAt(x, z);

                if (topY > 0) {
                    int topId = world.getBlockIdAt(worldX, topY, worldZ);
                    if (topId == BlockID.WATER || topId == BlockID.STILL_WATER) {
                        if (topY == 63) {
                            world.setBlockAt(worldX, topY, worldZ, BlockID.ICE, 0);
                        }
                    } else if (world.getBlockIdAt(worldX, topY + 1, worldZ) == BlockID.AIR) {
                        if (topId != BlockID.ICE && topId != BlockID.PACKED_ICE && topId != BlockID.AIR) {
                            world.setBlockAt(worldX, topY + 1, worldZ, BlockID.SNOW_LAYER, 0);
                        }
                    }
                }
            }
        }
    }
}
