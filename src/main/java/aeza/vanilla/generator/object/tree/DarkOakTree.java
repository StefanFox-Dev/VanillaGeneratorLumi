package aeza.vanilla.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.math.NukkitRandom;

public class DarkOakTree extends GenericTree {
    public DarkOakTree(NukkitRandom random) {
        super(random);
        this.logId = BlockID.ACACIA_LOG;
        this.logMeta = 1; // Dark Oak
        this.leavesId = BlockID.LEAVES2;
        this.leavesMeta = 1; // Dark Oak leaves
    }
}
