package aeza.vanilla.generator.cave;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.NukkitRandom;

public class CaveGenerator {

    private static final int SEA_LEVEL = 63;
    private static final int MIN_CAVE_Y = -58;
    private static final int MAX_CAVE_Y = 180;

    private final SimplexF spaghettiNoiseA;
    private final SimplexF spaghettiNoiseB;
    private final SimplexF cheeseNoise;
    private final SimplexF lavaNoise;

    public CaveGenerator(long seed) {
        NukkitRandom randA = new NukkitRandom(seed + 7711);
        NukkitRandom randB = new NukkitRandom(seed + 9933);
        NukkitRandom randC = new NukkitRandom(seed + 12345);
        NukkitRandom randD = new NukkitRandom(seed + 54321);

        this.spaghettiNoiseA = new SimplexF(randA, 3F, 1F / 2F, 1F / 48f);
        this.spaghettiNoiseB = new SimplexF(randB, 3F, 1F / 2F, 1F / 48f);
        this.cheeseNoise = new SimplexF(randC, 2F, 1F / 2F, 1F / 32f);
        this.lavaNoise = new SimplexF(randD, 2F, 1F / 2F, 1F / 16f);
    }

    public void carveDirectly(FullChunk chunk, int chunkX, int chunkZ) {
        if (chunk == null) return;

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            int worldX = baseX + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = baseZ + z;

                for (int y = MAX_CAVE_Y; y >= MIN_CAVE_Y; y--) {
                    int block = chunk.getBlockId(x, y, z);
                    if (block != BlockID.STONE && block != BlockID.DEEPSLATE && block != BlockID.DIRT && block != BlockID.GRAVEL && block != BlockID.TUFF) {
                        continue;
                    }

                    // Water Protection: Never carve into water or break the sea/river bed!
                    if (y <= SEA_LEVEL + 2) {
                        if (isNearWater(chunk, x, y, z)) {
                            continue;
                        }
                    }

                    // 1. Spaghetti Cave Tunnels (Intersection of 2 3D Noise Fields)
                    float nA = spaghettiNoiseA.noise3D(worldX, y, worldZ, true);
                    float nB = spaghettiNoiseB.noise3D(worldX, y, worldZ, true);

                    double tunnelThreshold = 0.055;
                    // Tunnels widen slightly deep underground
                    if (y < 0) tunnelThreshold = 0.075;

                    boolean isSpaghetti = Math.abs(nA) < tunnelThreshold && Math.abs(nB) < tunnelThreshold;

                    // 2. Cheese Caverns (Large underground rooms in deep layers)
                    boolean isCheese = false;
                    if (y < 40 && y > -55) {
                        float cheese = cheeseNoise.noise3D(worldX, y, worldZ, true);
                        if (cheese < -0.42f) {
                            isCheese = true;
                        }
                    }

                    if (isSpaghetti || isCheese) {
                        // Lava lake floors deep in the world (-54 to -58)
                        if (y <= -54) {
                            chunk.setBlockId(x, y, z, BlockID.STILL_LAVA);
                        } else {
                            chunk.setBlockId(x, y, z, BlockID.AIR);
                        }
                    }
                }
            }
        }
    }

    private boolean isNearWater(FullChunk chunk, int x, int y, int z) {
        for (int dy = 0; dy <= 4; dy++) {
            int checkY = y + dy;
            if (checkY >= 320) break;
            int id = chunk.getBlockId(x, checkY, z);
            if (id == BlockID.WATER || id == BlockID.STILL_WATER) {
                return true;
            }
        }
        return false;
    }
}
