package aeza.vanilla.generator.populator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public abstract class Populator {
    public abstract void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk);
}
