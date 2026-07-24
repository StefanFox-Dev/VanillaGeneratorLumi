package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.object.tree.GenericTree;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class OverworldPopulator extends Populator {

    private final OrePopulator orePopulator = new OrePopulator();

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        orePopulator.populate(world, random, chunkX, chunkZ, chunk);

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        int trees = random.nextInt(3) + 2;
        for (int i = 0; i < trees; i++) {
            int rx = random.nextInt(16);
            int rz = random.nextInt(16);
            int x = baseX + rx;
            int z = baseZ + rz;
            int y = chunk.getHighestBlockAt(rx, rz);
            if (y > 0) {
                new GenericTree(random).generate(world, random, x, y, z);
            }
        }

        int grass = random.nextInt(10) + 5;
        for (int i = 0; i < grass; i++) {
            int rx = random.nextInt(16);
            int rz = random.nextInt(16);
            int x = baseX + rx;
            int z = baseZ + rz;
            int y = chunk.getHighestBlockAt(rx, rz);
            if (y > 0 && world.getBlockIdAt(x, y - 1, z) == BlockID.GRASS) {
                world.setBlockAt(x, y, z, BlockID.TALL_GRASS, 1);
            }
        }
    }
}
