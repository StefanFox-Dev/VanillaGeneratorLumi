package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.Random;
import java.util.SplittableRandom;

public class NetherStructurePopulator extends Populator {

    public NetherStructurePopulator() {
        StructureManager.init();
    }

    private int findNetherGroundY(FullChunk chunk, int x, int z) {
        for (int y = 100; y >= 30; y--) {
            int id = chunk.getBlockId(x, y, z);
            if (id == BlockID.NETHERRACK || id == BlockID.SOUL_SAND || id == BlockID.BASALT || id == BlockID.BLACKSTONE) {
                return y + 1;
            }
        }
        return 40;
    }

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        Random javaRand = new Random(random.nextLong());
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        int surfaceY = findNetherGroundY(chunk, 8, 8);
        String worldName = chunk.getProvider() != null && chunk.getProvider().getLevel() != null ? chunk.getProvider().getLevel().getName() : "nether";

        // Bastion Remnants (~every 8 chunks)
        if (((chunkX & 7) == 3) && ((chunkZ & 7) == 3)) {
            NBTStructure bastion = StructureManager.getRandomStructure("bastion", javaRand);
            if (bastion != null) {
                bastion.place(world, baseX, surfaceY, baseZ);
                StructureManager.registerGeneratedStructure(worldName, "bastion", baseX, surfaceY, baseZ);
                return;
            }
        }

        // Nether Fossils (~every 8 chunks)
        if (((chunkX & 7) == 1) && ((chunkZ & 7) == 5)) {
            NBTStructure fossil = StructureManager.getRandomStructure("nether_fossils", javaRand);
            if (fossil == null) {
                fossil = StructureManager.getRandomStructure("fossils", javaRand);
            }
            if (fossil != null) {
                fossil.place(world, baseX + 4, surfaceY, baseZ + 4);
                StructureManager.registerGeneratedStructure(worldName, "nether_fossils", baseX + 4, surfaceY, baseZ + 4);
                return;
            }
        }

        // Ruined Nether Portal (~every 10 chunks)
        if (((chunkX & 15) == 6) && ((chunkZ & 15) == 2)) {
            NBTStructure portal = StructureManager.getRandomStructure("ruined_portal", javaRand);
            if (portal != null) {
                portal.place(world, baseX + 4, surfaceY, baseZ + 4);
                StructureManager.registerGeneratedStructure(worldName, "ruined_portal", baseX + 4, surfaceY, baseZ + 4);
            }
        }
    }
}
