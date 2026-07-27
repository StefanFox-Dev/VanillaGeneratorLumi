package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class EndStructurePopulator extends Populator {

    public EndStructurePopulator() {
        StructureManager.init();
    }

    private int findEndGroundY(FullChunk chunk, int x, int z) {
        for (int y = 200; y >= 30; y--) {
            int id = chunk.getBlockId(x, y, z);
            if (id == BlockID.END_STONE) {
                return y + 1;
            }
        }
        return 60;
    }

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        String worldName = chunk.getProvider() != null && chunk.getProvider().getLevel() != null ? chunk.getProvider().getLevel().getName() : "the_end";

        // End City (~every 8 chunks on outer End islands)
        if (((chunkX & 7) == 4) && ((chunkZ & 7) == 4)) {
            int surfaceY = findEndGroundY(chunk, 8, 8);
            if (surfaceY > 30) {
                EndCityPopulator.generateEndCity(world, random, baseX, surfaceY, baseZ, worldName);
            }
        }
    }
}
