package aeza.vanilla.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

public class TallGrass extends TerrainObject {
    private final int meta;

    public TallGrass(int meta) {
        this.meta = meta;
    }

    public TallGrass() {
        this(1); // Fern or tall grass
    }

    @Override
    public boolean generate(ChunkManager world, NukkitRandom random, int x, int y, int z) {
        int groundId = world.getBlockIdAt(x, y - 1, z);
        if ((groundId == BlockID.GRASS || groundId == BlockID.DIRT) && world.getBlockIdAt(x, y, z) == BlockID.AIR) {
            world.setBlockAt(x, y, z, BlockID.TALL_GRASS, meta);
            return true;
        }
        return false;
    }
}
