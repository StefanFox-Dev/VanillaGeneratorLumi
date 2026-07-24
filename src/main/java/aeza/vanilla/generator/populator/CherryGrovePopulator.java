package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biome.CherryGroveBiome;
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
                if (biome != CherryGroveBiome.CHERRY_GROVE_ID) continue;

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

                    // 2. Scatter Pink Petals carpet (ID 804)
                    else if (random.nextInt(3) == 0) {
                        world.setBlockAt(worldX, highestY + 1, worldZ, BlockID.PINK_PETALS, random.nextInt(4));
                    }

                    // 3. Scatter Flowers (Dandelions & Poppies)
                    else if (random.nextInt(16) == 0) {
                        int flowerId = random.nextBoolean() ? BlockID.DANDELION : BlockID.POPPY;
                        world.setBlockAt(worldX, highestY + 1, worldZ, flowerId, 0);
                    }

                    // 4. Scatter Grass
                    else if (random.nextInt(5) == 0) {
                        world.setBlockAt(worldX, highestY + 1, worldZ, BlockID.TALL_GRASS, 1);
                    }
                }
            }
        }

        // 5. Spawn Cherry Grove Animals (SIGNIFICANTLY REDUCED: 8% chance per chunk, only 1-2 animals)
        if (cherryCount > 0 && random.nextInt(100) < 8 && chunk.getProvider() != null && chunk.getProvider().getLevel() != null) {
            var lvl = chunk.getProvider().getLevel();
            int surfaceY = chunk.getHighestBlockAt(8, 8);
            if (surfaceY >= 63) {
                Location mobLoc = new Location(baseX + 8, surfaceY + 1, baseZ + 8, lvl);

                try {
                    if (random.nextBoolean()) {
                        Entity s = Entity.createEntity("Sheep", mobLoc);
                        if (s != null) s.spawnToAll();
                    } else {
                        Entity p = Entity.createEntity("Pig", mobLoc);
                        if (p != null) p.spawnToAll();
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}
