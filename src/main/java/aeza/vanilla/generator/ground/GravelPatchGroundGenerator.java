package aeza.vanilla.generator.ground;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class GravelPatchGroundGenerator extends GroundGenerator {
    @Override
    public void generateTerrainColumn(FullChunk chunk, SplittableRandom random, int blockX, int blockZ, int biome, double surfaceNoise) {
        if (surfaceNoise < -1.0 || surfaceNoise > 2.0) {
            topBlockId = BlockID.GRAVEL;
            topBlockMeta = 0;
            groundBlockId = BlockID.GRAVEL;
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
