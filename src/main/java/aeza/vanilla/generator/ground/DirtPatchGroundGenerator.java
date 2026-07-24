package aeza.vanilla.generator.ground;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class DirtPatchGroundGenerator extends GroundGenerator {
    @Override
    public void generateTerrainColumn(FullChunk chunk, SplittableRandom random, int blockX, int blockZ, int biome, double surfaceNoise) {
        if (surfaceNoise > 1.75) {
            topBlockId = BlockID.DIRT;
            topBlockMeta = 1; // Coarse dirt
        } else if (surfaceNoise > -0.95) {
            topBlockId = BlockID.DIRT;
            topBlockMeta = 2; // Podzol
        } else {
            topBlockId = BlockID.GRASS;
            topBlockMeta = 0;
        }
        groundBlockId = BlockID.DIRT;
        groundBlockMeta = 0;

        super.generateTerrainColumn(chunk, random, blockX, blockZ, biome, surfaceNoise);
    }
}
