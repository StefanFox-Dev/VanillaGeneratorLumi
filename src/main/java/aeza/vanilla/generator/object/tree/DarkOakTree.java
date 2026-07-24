package aeza.vanilla.generator.object.tree;

import cn.nukkit.block.BlockID;

import java.util.SplittableRandom;

public class DarkOakTree extends GenericTree {
    public DarkOakTree(SplittableRandom random) {
        super(random);
        this.logId = BlockID.ACACIA_LOG;
        this.logMeta = 1; // Dark Oak
        this.leavesId = BlockID.LEAVES2;
        this.leavesMeta = 1; // Dark Oak leaves
    }
}
