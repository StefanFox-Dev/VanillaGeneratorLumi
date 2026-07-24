package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class BambooPopulator extends Populator {

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int biome = chunk.getBiomeId(x, z);

                if (biome != BiomeIds.JUNGLE && biome != BiomeIds.JUNGLE_HILLS && biome != BiomeIds.JUNGLE_MUTATED) {
                    continue;
                }

                int worldX = baseX + x;
                int worldZ = baseZ + z;
                int highestY = chunk.getHighestBlockAt(x, z);

                if (highestY < 62) continue;

                int groundId = chunk.getBlockId(x, highestY, z);
                int aboveId = chunk.getBlockId(x, highestY + 1, z);

                if ((groundId == BlockID.GRASS || groundId == BlockID.DIRT || groundId == BlockID.PODZOL) && aboveId == BlockID.AIR) {

                    // Natural, tasteful Bamboo stalk density (~6% chance per block in Jungles)
                    if (random.nextInt(100) < 6) {
                        int stalkHeight = 8 + random.nextInt(6); // 8-14 blocks high
                        for (int h = 1; h <= stalkHeight; h++) {
                            int curId = world.getBlockIdAt(worldX, highestY + h, worldZ);
                            if (curId == BlockID.AIR || curId == BlockID.LEAVES || curId == BlockID.VINES) {
                                world.setBlockAt(worldX, highestY + h, worldZ, BlockID.BAMBOO, 0);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
