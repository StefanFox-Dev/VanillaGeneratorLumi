package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import aeza.vanilla.generator.tree.CherryTree;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class CherryGrovePopulator extends Populator {

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        int cherryCount = 0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int biome = chunk.getBiomeId(x, z);
                if (biome != BiomeIds.CHERRY_GROVE) continue;

                int worldX = baseX + x;
                int worldZ = baseZ + z;
                int highestY = chunk.getHighestBlockAt(x, z);

                if (highestY < 62) continue;

                int groundId = chunk.getBlockId(x, highestY, z);
                int aboveId = chunk.getBlockId(x, highestY + 1, z);

                if (groundId == BlockID.GRASS && aboveId == BlockID.AIR) {

                    // 1. Plant Cherry Blossom Trees (~2-4 per chunk)
                    if (x % 6 == 1 && z % 6 == 1 && cherryCount < 4 && random.nextInt(3) == 0) {
                        CherryTree.grow(world, random, worldX, highestY + 1, worldZ);
                        cherryCount++;
                    }

                    // 2. Pink Petal Carpet on Grass
                    if (random.nextInt(4) == 0) {
                        chunk.setBlockId(x, highestY + 1, z, BlockID.PINK_PETALS);
                        chunk.setBlockData(x, highestY + 1, z, random.nextInt(4));
                    }
                }
            }
        }

        // 3. Spawn Bees & Piggies in Cherry Groves
        if (cherryCount > 0 && random.nextInt(6) == 0 && chunk.getProvider() != null && chunk.getProvider().getLevel() != null) {
            int spawnX = baseX + 8;
            int spawnZ = baseZ + 8;
            int spawnY = chunk.getHighestBlockAt(8, 8) + 1;

            if (spawnY > 62) {
                Location loc = new Location(spawnX, spawnY, spawnZ, chunk.getProvider().getLevel());
                if (random.nextBoolean()) {
                    Entity.createEntity("Bee", loc);
                } else {
                    Entity.createEntity("Pig", loc);
                }
            }
        }
    }
}
