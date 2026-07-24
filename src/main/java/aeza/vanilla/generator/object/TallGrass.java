package aeza.vanilla.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;

import java.util.SplittableRandom;

public class TallGrass extends TerrainObject {
    private final int meta;

    public TallGrass(int meta) {
        this.meta = meta;
    }

    public TallGrass() {
        this(1);
    }

    @Override
    public boolean generate(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        int groundId = world.getBlockIdAt(x, y - 1, z);
        if (groundId == BlockID.GRASS && world.getBlockIdAt(x, y, z) == BlockID.AIR) {
            world.setBlockAt(x, y, z, BlockID.TALL_GRASS, meta);
            return true;
        }
        return false;
    }
}
