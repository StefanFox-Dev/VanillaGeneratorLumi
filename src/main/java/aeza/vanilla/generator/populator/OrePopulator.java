package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.object.OreType;
import aeza.vanilla.generator.object.OreVein;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class OrePopulator extends Populator {

    private final OreType[] ores;

    public OrePopulator(OreType[] ores) {
        this.ores = ores;
    }

    public OrePopulator() {
        this(new OreType[]{
                new OreType(BlockID.COAL_ORE, 20, 17, 0, 128),
                new OreType(BlockID.IRON_ORE, 20, 9, 0, 64),
                new OreType(BlockID.GOLD_ORE, 2, 9, 0, 32),
                new OreType(BlockID.REDSTONE_ORE, 8, 8, 0, 16),
                new OreType(BlockID.DIAMOND_ORE, 1, 8, 0, 16),
                new OreType(BlockID.LAPIS_ORE, 1, 7, 0, 32),
                new OreType(BlockID.GRAVEL, 8, 33, 0, 128),
                new OreType(BlockID.DIRT, 10, 33, 0, 128)
        });
    }

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (OreType ore : ores) {
            for (int i = 0; i < ore.clusterCount; ++i) {
                int x = baseX + random.nextInt(16);
                int y = ore.minY + random.nextInt(Math.max(1, ore.maxY - ore.minY));
                int z = baseZ + random.nextInt(16);
                new OreVein(ore).generate(world, random, x, y, z);
            }
        }
    }
}
