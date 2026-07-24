package aeza.vanilla.generator.object;

import cn.nukkit.level.ChunkManager;

import java.util.SplittableRandom;

public abstract class TerrainObject {

    public static void killTree(ChunkManager world, int x, int y, int z) {
        // Helper if needed
    }

    public abstract boolean generate(ChunkManager world, SplittableRandom random, int x, int y, int z);
}
