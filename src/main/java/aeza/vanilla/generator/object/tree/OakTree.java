package aeza.vanilla.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.math.NukkitRandom;

public class OakTree extends GenericTree {
    public OakTree(NukkitRandom random) {
        super(random);
        this.logId = BlockID.OAK_LOG;
        this.logMeta = 0; // Oak
        this.leavesId = BlockID.LEAVES;
        this.leavesMeta = 0; // Oak leaves
    }
}
