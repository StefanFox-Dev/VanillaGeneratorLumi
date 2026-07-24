package aeza.vanilla.generator.object.tree;

import cn.nukkit.block.BlockID;

import java.util.SplittableRandom;

public class AcaciaTree extends GenericTree {
    public AcaciaTree(SplittableRandom random) {
        super(random);
        this.logId = BlockID.ACACIA_LOG;
        this.logMeta = 0; // Acacia
        this.leavesId = BlockID.LEAVES2;
        this.leavesMeta = 0; // Acacia leaves
    }
}
