package aeza.vanilla.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.math.NukkitRandom;

public class AcaciaTree extends GenericTree {
    public AcaciaTree(NukkitRandom random) {
        super(random);
        this.logId = BlockID.ACACIA_LOG;
        this.logMeta = 0; // Acacia
        this.leavesId = BlockID.LEAVES2;
        this.leavesMeta = 0; // Acacia leaves
    }
}
