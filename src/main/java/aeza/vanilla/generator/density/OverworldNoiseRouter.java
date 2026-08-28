package aeza.vanilla.generator.density;

import aeza.vanilla.generator.climate.MultiNoiseBiomeSource;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.NukkitRandom;

import java.util.SplittableRandom;

public class OverworldNoiseRouter {

    public static final int SEA_LEVEL = 63;
    public static final int MIN_Y = -64;
    public static final int MAX_Y = 320;

    private final MultiNoiseBiomeSource biomeSource;
    private final SimplexF terrainNoise3D;
    private final SimplexF detailNoise3D;
    private final SimplexF deepslateNoise;

    public OverworldNoiseRouter(long seed, MultiNoiseBiomeSource biomeSource) {
        this.biomeSource = biomeSource;
        NukkitRandom rand = new NukkitRandom(seed + 101);
        this.terrainNoise3D = new SimplexF(rand, 4F, 2 / 4F, 1F / 160f);
        this.detailNoise3D = new SimplexF(rand, 2F, 2 / 4F, 1F / 48f);
        this.deepslateNoise = new SimplexF(rand, 2F, 2 / 4F, 1F / 16f);
    }

    public void generateTerrain(FullChunk chunk, SplittableRandom random, int chunkX, int chunkZ) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            int worldX = baseX + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = baseZ + z;

                float cont = biomeSource.getContinentalness(worldX, worldZ);
                float eros = biomeSource.getErosion(worldX, worldZ);
                float weird = biomeSource.getWeirdness(worldX, worldZ);

                // Compute base surface target height based on 1.18+ climate splines
                double baseHeight = computeBaseHeight(cont, eros, weird);

                // Bedrock roughness at Y = -64..-60
                int bedrockHeight = MIN_Y + random.nextInt(5);

                for (int y = MAX_Y; y >= MIN_Y; y--) {
                    if (y <= bedrockHeight) {
                        chunk.setBlockId(x, y, z, BlockID.BEDROCK);
                        continue;
                    }

                    double density = computeDensity(worldX, y, worldZ, baseHeight, eros);

                    if (density > 0) {
                        // Solid Terrain (Stone or Deepslate)
                        if (y < 0) {
                            // Deepslate transition between Y = -8 and Y = 0
                            if (y >= -8) {
                                float n = deepslateNoise.noise2D(worldX, worldZ, true);
                                if (y < -4 + (int) (n * 4)) {
                                    chunk.setBlockId(x, y, z, BlockID.DEEPSLATE);
                                } else {
                                    chunk.setBlockId(x, y, z, BlockID.STONE);
                                }
                            } else {
                                chunk.setBlockId(x, y, z, BlockID.DEEPSLATE);
                            }
                        } else {
                            chunk.setBlockId(x, y, z, BlockID.STONE);
                        }
                    } else if (y <= SEA_LEVEL) {
                        // Water Level (Ocean / Sea)
                        chunk.setBlockId(x, y, z, BlockID.STILL_WATER);
                    } else {
                        chunk.setBlockId(x, y, z, BlockID.AIR);
                    }
                }
            }
        }
    }

    private double computeBaseHeight(float cont, float eros, float weird) {
        // Oceans vs Inland
        double h;
        if (cont < -0.2f) {
            // Deep Ocean to Ocean (-0.7 to -0.2 -> Y = 30 to 60)
            h = 42.0 + (cont + 0.7) * 40.0;
        } else if (cont < 0.1f) {
            // Coast / Lowlands (-0.2 to 0.1 -> Y = 60 to 72)
            h = 62.0 + (cont + 0.2) * 33.3;
        } else {
            // Inland continents (0.1 to 1.0 -> Y = 72 to 140 base)
            h = 72.0 + (cont - 0.1) * 60.0;

            // Mountain peaks from low erosion & high weirdness (Peaks up to Y = 240..270)
            if (eros < -0.2f) {
                double mountainFactor = Math.abs(eros) * (0.5 + Math.abs(weird) * 0.8);
                h += mountainFactor * 130.0;
            }
        }
        return h;
    }

    private double computeDensity(int x, int y, int z, double baseHeight, float eros) {
        double heightOffset = (baseHeight - y) / (eros < -0.2f ? 40.0 : 20.0);

        // 3D Noise variation for natural slopes and overhangs
        float n3d = terrainNoise3D.noise3D(x, y, z, true);
        float detail = detailNoise3D.noise3D(x, y, z, true) * 0.25f;

        return heightOffset + (n3d * 0.8) + detail;
    }
}
