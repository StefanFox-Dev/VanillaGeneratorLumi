package aeza.vanilla.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;

import java.util.SplittableRandom;

public class Cactus extends TerrainObject {

    @Override
    public boolean generate(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        if (world.getBlockIdAt(x, y, z) != BlockID.AIR) return false;
        int groundId = world.getBlockIdAt(x, y - 1, z);
        if (groundId != BlockID.SAND) return false;

        int height = 1 + random.nextInt(3);
        for (int i = 0; i < height; i++) {
            if (world.getBlockIdAt(x, y + i, z) == BlockID.AIR) {
                world.setBlockAt(x, y + i, z, BlockID.CACTUS, 0);
            }
        }
        return true;
    }
}
