package aeza.vanilla.generator.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class CherryTree {

    public static void grow(ChunkManager level, SplittableRandom random, int x, int y, int z) {
        FullChunk chunk = level.getChunk(x >> 4, z >> 4);
        if (chunk == null) return;

        int height = 5 + random.nextInt(3);

        // Build Cherry Log trunk (ID 791)
        for (int dy = 0; dy < height; dy++) {
            level.setBlockAt(x, y + dy, z, BlockID.CHERRY_LOG, 0);
        }

        // Branch outward near top
        int topY = y + height - 1;

        // Build broad pink Cherry Leaves canopy (ID 803)
        for (int dy = -2; dy <= 1; dy++) {
            int radius = dy == 1 ? 2 : (dy == 0 ? 3 : 2);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && random.nextInt(3) == 0) {
                        continue; // Organic rounded canopy corners
                    }
                    int px = x + dx;
                    int py = topY + dy;
                    int pz = z + dz;
                    int curId = level.getBlockIdAt(px, py, pz);
                    if (curId == BlockID.AIR || curId == BlockID.SNOW_LAYER) {
                        level.setBlockAt(px, py, pz, BlockID.CHERRY_LEAVES, 0);
                    }
                }
            }
        }

        // Occasional Bee Hive / Nest attached to trunk (20% chance)
        if (random.nextInt(5) == 0) {
            level.setBlockAt(x + 1, y + 2, z, BlockID.BEE_NEST, 0);
        }
    }
}
