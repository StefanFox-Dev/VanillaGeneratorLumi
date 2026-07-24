package aeza.vanilla.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.math.NukkitRandom;

public class BirchTree extends GenericTree {
    public BirchTree(NukkitRandom random) {
        super(random);
        this.logId = BlockID.OAK_LOG;
        this.logMeta = 2; // Birch
        this.leavesId = BlockID.LEAVES;
        this.leavesMeta = 2; // Birch leaves
    }
}
