package aeza.vanilla.generator.object;

import cn.nukkit.block.BlockID;

public class OreType {
    public final int blockId;
    public final int blockMeta;
    public final int targetId;
    public final int clusterCount;
    public final int clusterSize;
    public final int minY;
    public final int maxY;

    public OreType(int blockId, int blockMeta, int targetId, int clusterCount, int clusterSize, int minY, int maxY) {
        this.blockId = blockId;
        this.blockMeta = blockMeta;
        this.targetId = targetId;
        this.clusterCount = clusterCount;
        this.clusterSize = clusterSize;
        this.minY = minY;
        this.maxY = maxY;
    }

    public OreType(int blockId, int targetId, int clusterCount, int clusterSize, int minY, int maxY) {
        this(blockId, 0, targetId, clusterCount, clusterSize, minY, maxY);
    }

    public OreType(int blockId, int clusterCount, int clusterSize, int minY, int maxY) {
        this(blockId, 0, BlockID.STONE, clusterCount, clusterSize, minY, maxY);
    }
}
