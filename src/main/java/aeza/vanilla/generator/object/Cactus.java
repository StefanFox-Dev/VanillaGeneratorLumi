package aeza.vanilla.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

public class Cactus extends TerrainObject {
    @Override
    public boolean generate(ChunkManager world, NukkitRandom random, int x, int y, int z) {
        if (world.getBlockIdAt(x, y, z) == BlockID.AIR) {
            int height = 1 + random.nextBoundedInt(3);
            for (int i = 0; i < height; ++i) {
                int bId = world.getBlockIdAt(x, y + i, z);
                int belowId = world.getBlockIdAt(x, y + i - 1, z);
                if ((bId == BlockID.AIR || bId == BlockID.CACTUS) && (belowId == BlockID.SAND || belowId == BlockID.CACTUS)) {
                    world.setBlockAt(x, y + i, z, BlockID.CACTUS, 0);
                }
            }
            return true;
        }
        return false;
    }
}
