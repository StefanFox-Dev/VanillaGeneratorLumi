package aeza.vanilla.generator.object.tree;

import aeza.vanilla.generator.object.TerrainObject;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

public class GenericTree extends TerrainObject {
    protected int height;
    protected int logId = BlockID.OAK_LOG;
    protected int logMeta = 0;
    protected int leavesId = BlockID.LEAVES;
    protected int leavesMeta = 0;

    public GenericTree(NukkitRandom random) {
        this.height = random.nextBoundedInt(3) + 4;
    }

    public boolean canFit(ChunkManager world, int x, int y, int z) {
        if (y < 1 || y + height + 1 >= 256) return false;
        int targetId = world.getBlockIdAt(x, y - 1, z);
        return targetId == BlockID.GRASS || targetId == BlockID.DIRT;
    }

    @Override
    public boolean generate(ChunkManager world, NukkitRandom random, int x, int y, int z) {
        if (!canFit(world, x, y, z)) return false;

        for (int yy = y - 3 + height; yy <= y + height; ++yy) {
            int sub = yy - (y + height);
            int radius = 1 - sub / 2;
            for (int xx = x - radius; xx <= x + radius; ++xx) {
                int offX = xx - x;
                for (int zz = z - radius; zz <= z + radius; ++zz) {
                    int offZ = zz - z;
                    if (Math.abs(offX) != radius || Math.abs(offZ) != radius || (random.nextBoundedInt(2) != 0 && sub != 0)) {
                        int bId = world.getBlockIdAt(xx, yy, zz);
                        if (bId == BlockID.AIR || bId == BlockID.LEAVES || bId == BlockID.LEAVES2) {
                            world.setBlockAt(xx, yy, zz, leavesId, leavesMeta);
                        }
                    }
                }
            }
        }

        for (int yy = 0; yy < height; ++yy) {
            int bId = world.getBlockIdAt(x, y + yy, z);
            if (bId == BlockID.AIR || bId == BlockID.LEAVES || bId == BlockID.LEAVES2) {
                world.setBlockAt(x, y + yy, z, logId, logMeta);
            }
        }

        return true;
    }
}
