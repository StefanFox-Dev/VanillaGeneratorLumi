package aeza.vanilla.generator.object.tree;

import cn.nukkit.block.BlockID;

import java.util.SplittableRandom;

public class BirchTree extends GenericTree {
    public BirchTree(SplittableRandom random) {
        super(random);
        this.logId = BlockID.BIRCH_LOG;
        this.logMeta = 2; // Birch
        this.leavesId = BlockID.LEAVES;
        this.leavesMeta = 2; // Birch leaves
    }
}
