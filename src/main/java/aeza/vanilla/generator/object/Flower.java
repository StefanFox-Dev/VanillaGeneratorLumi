package aeza.vanilla.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;

import java.util.SplittableRandom;

public class Flower extends TerrainObject {
    private final int flowerId;
    private final int flowerMeta;

    public Flower(int flowerId, int flowerMeta) {
        this.flowerId = flowerId;
        this.flowerMeta = flowerMeta;
    }

    public Flower(int flowerId) {
        this(flowerId, 0);
    }

    @Override
    public boolean generate(ChunkManager world, SplittableRandom random, int x, int y, int z) {
        int groundId = world.getBlockIdAt(x, y - 1, z);
        if ((groundId == BlockID.GRASS || groundId == BlockID.DIRT) && world.getBlockIdAt(x, y, z) == BlockID.AIR) {
            world.setBlockAt(x, y, z, flowerId, flowerMeta);
            return true;
        }
        return false;
    }
}
