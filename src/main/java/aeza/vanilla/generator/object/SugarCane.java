package aeza.vanilla.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

public class SugarCane extends TerrainObject {
    @Override
    public boolean generate(ChunkManager world, NukkitRandom random, int x, int y, int z) {
        if (world.getBlockIdAt(x, y, z) == BlockID.AIR) {
            int height = 1 + random.nextBoundedInt(3);
            for (int i = 0; i < height; ++i) {
                int bId = world.getBlockIdAt(x, y + i, z);
                if (bId == BlockID.AIR) {
                    world.setBlockAt(x, y + i, z, BlockID.SUGARCANE_BLOCK, 0);
                }
            }
            return true;
        }
        return false;
    }
}
