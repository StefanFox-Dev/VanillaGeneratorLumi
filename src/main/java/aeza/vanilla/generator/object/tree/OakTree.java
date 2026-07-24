package aeza.vanilla.generator.object.tree;

import cn.nukkit.block.BlockID;

import java.util.SplittableRandom;

public class OakTree extends GenericTree {
    public OakTree(SplittableRandom random) {
        super(random);
        this.logId = BlockID.OAK_LOG;
        this.logMeta = 0; // Oak
        this.leavesId = BlockID.LEAVES;
        this.leavesMeta = 0; // Oak leaves
    }
}
