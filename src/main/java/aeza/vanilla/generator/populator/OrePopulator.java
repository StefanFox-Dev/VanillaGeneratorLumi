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
                // 1.18+ / 1.20+ Modern Ore Distribution
                new OreType(BlockID.COAL_ORE, 30, 17, 0, 256),
                new OreType(BlockID.IRON_ORE, 24, 9, -64, 256),
                new OreType(BlockID.GOLD_ORE, 6, 9, -64, 32),
                new OreType(BlockID.REDSTONE_ORE, 14, 8, -64, 15),
                new OreType(BlockID.DIAMOND_ORE, 8, 8, -64, 16),
                new OreType(BlockID.LAPIS_ORE, 4, 7, -64, 64),
                new OreType(BlockID.GRAVEL, 8, 33, -64, 128),
                new OreType(BlockID.DIRT, 10, 33, -64, 128)
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
