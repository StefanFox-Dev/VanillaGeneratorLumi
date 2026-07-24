package aeza.vanilla.generator.object;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

public class OreVein extends TerrainObject {
    private final OreType oreType;

    public OreVein(OreType oreType) {
        this.oreType = oreType;
    }

    @Override
    public boolean generate(ChunkManager world, NukkitRandom random, int x, int y, int z) {
        float f = random.nextFloat() * (float) Math.PI;
        double d1 = (x + 8) + Math.sin(f) * oreType.clusterSize / 8.0F;
        double d2 = (x + 8) - Math.sin(f) * oreType.clusterSize / 8.0F;
        double d3 = (z + 8) + Math.cos(f) * oreType.clusterSize / 8.0F;
        double d4 = (z + 8) - Math.cos(f) * oreType.clusterSize / 8.0F;
        double d5 = y + random.nextBoundedInt(3) - 2;
        double d6 = y + random.nextBoundedInt(3) - 2;

        for (int i = 0; i <= oreType.clusterSize; ++i) {
            double d7 = d1 + (d2 - d1) * i / oreType.clusterSize;
            double d8 = d5 + (d6 - d5) * i / oreType.clusterSize;
            double d9 = d3 + (d4 - d3) * i / oreType.clusterSize;
            double d10 = random.nextDouble() * oreType.clusterSize / 16.0D;
            double d11 = (Math.sin(i * (float) Math.PI / oreType.clusterSize) + 1.0F) * d10 + 1.0D;
            double d12 = (Math.sin(i * (float) Math.PI / oreType.clusterSize) + 1.0F) * d10 + 1.0D;

            int minX = (int) Math.floor(d7 - d11 / 2.0D);
            int maxX = (int) Math.floor(d7 + d11 / 2.0D);
            int minY = (int) Math.floor(d8 - d12 / 2.0D);
            int maxY = (int) Math.floor(d8 + d12 / 2.0D);
            int minZ = (int) Math.floor(d9 - d11 / 2.0D);
            int maxZ = (int) Math.floor(d9 + d11 / 2.0D);

            for (int px = minX; px <= maxX; ++px) {
                double d13 = (px + 0.5D - d7) / (d11 / 2.0D);
                if (d13 * d13 < 1.0D) {
                    for (int py = minY; py <= maxY; ++py) {
                        double d14 = (py + 0.5D - d8) / (d12 / 2.0D);
                        if (d13 * d13 + d14 * d14 < 1.0D) {
                            for (int pz = minZ; pz <= maxZ; ++pz) {
                                double d15 = (pz + 0.5D - d9) / (d11 / 2.0D);
                                if (d13 * d13 + d14 * d14 + d15 * d15 < 1.0D) {
                                    if (world.getBlockIdAt(px, py, pz) == oreType.targetId) {
                                        world.setBlockAt(px, py, pz, oreType.blockId, oreType.blockMeta);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
