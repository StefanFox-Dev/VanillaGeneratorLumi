package aeza.vanilla.generator.ground;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class StonePatchGroundGenerator extends GroundGenerator {
    @Override
    public void generateTerrainColumn(FullChunk chunk, SplittableRandom random, int blockX, int blockZ, int biome, double surfaceNoise) {
        if (surfaceNoise > 1.0) {
            topBlockId = BlockID.STONE;
            topBlockMeta = 0;
            groundBlockId = BlockID.STONE;
            groundBlockMeta = 0;
        } else {
            topBlockId = BlockID.GRASS;
            topBlockMeta = 0;
            groundBlockId = BlockID.DIRT;
            groundBlockMeta = 0;
        }

        super.generateTerrainColumn(chunk, random, blockX, blockZ, biome, surfaceNoise);
    }
}
