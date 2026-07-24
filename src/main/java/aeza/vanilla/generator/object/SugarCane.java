package aeza.vanilla.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;

import java.util.SplittableRandom;

public class SugarCane extends TerrainObject {

    @Override
    public boolean generate(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        if (world.getBlockIdAt(x, y, z) != BlockID.AIR) return false;
        int groundId = world.getBlockIdAt(x, y - 1, z);
        if (groundId != BlockID.GRASS && groundId != BlockID.DIRT && groundId != BlockID.SAND) return false;

        boolean hasWater = world.getBlockIdAt(x - 1, y - 1, z) == BlockID.STILL_WATER ||
                           world.getBlockIdAt(x + 1, y - 1, z) == BlockID.STILL_WATER ||
                           world.getBlockIdAt(x, y - 1, z - 1) == BlockID.STILL_WATER ||
                           world.getBlockIdAt(x, y - 1, z + 1) == BlockID.STILL_WATER;

        if (!hasWater) return false;

        int height = 2 + random.nextInt(2);
        for (int i = 0; i < height; i++) {
            if (world.getBlockIdAt(x, y + i, z) == BlockID.AIR) {
                world.setBlockAt(x, y + i, z, BlockID.REEDS, 0);
            }
        }
        return true;
    }
}
