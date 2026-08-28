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
    public static final int MAX_Y = 319;

    private final MultiNoiseBiomeSource biomeSource;
    private final SimplexF terrainNoise3D;
    private final SimplexF detailNoise3D;
    private final SimplexF hillNoise2D;
    private final SimplexF deepslateNoise;

    public OverworldNoiseRouter(long seed, MultiNoiseBiomeSource biomeSource) {
        this.biomeSource = biomeSource;
        NukkitRandom rand = new NukkitRandom(seed + 101);
        this.terrainNoise3D = new SimplexF(rand, 3F, 1F / 2F, 1F / 80f);
        this.detailNoise3D = new SimplexF(rand, 2F, 1F / 2F, 1F / 32f);
        this.hillNoise2D = new SimplexF(rand, 4F, 1F / 2F, 1F / 160f);
        this.deepslateNoise = new SimplexF(rand, 2F, 1F / 2F, 1F / 16f);
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

                // Compute smooth natural target height with continuous Hermite spline
                double baseHeight = computeSmoothBaseHeight(worldX, worldZ, cont, eros, weird);

                int bedrockHeight = MIN_Y + random.nextInt(4);

                for (int y = MAX_Y; y >= MIN_Y; y--) {
                    if (y <= bedrockHeight) {
                        chunk.setBlockId(x, y, z, BlockID.BEDROCK);
                        continue;
                    }

                    double density = computeDensity(worldX, y, worldZ, baseHeight);

                    if (density > 0) {
                        // Solid Terrain
                        if (y < 0) {
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
                        chunk.setBlockId(x, y, z, BlockID.STILL_WATER);
                    } else {
                        chunk.setBlockId(x, y, z, BlockID.AIR);
                    }
                }
            }
        }
    }

    private double computeSmoothBaseHeight(int x, int z, float cont, float eros, float weird) {
        // Continuous Spline Continentalness curve (C in [-1.0, 1.0])
        double continentalHeight;
        if (cont < -0.45f) {
            double t = smoothStep(-1.0, -0.45, cont);
            continentalHeight = 34.0 + t * 14.0; // Deep Ocean: 34 -> 48
        } else if (cont < -0.15f) {
            double t = smoothStep(-0.45, -0.15, cont);
            continentalHeight = 48.0 + t * 10.0; // Ocean: 48 -> 58
        } else if (cont < -0.04f) {
            double t = smoothStep(-0.15, -0.04, cont);
            continentalHeight = 58.0 + t * 5.0; // Coast: 58 -> 63
        } else if (cont < 0.05f) {
            double t = smoothStep(-0.04, 0.05, cont);
            continentalHeight = 63.0 + t * 3.0; // Beach: 63 -> 66
        } else if (cont < 0.35f) {
            double t = smoothStep(0.05, 0.35, cont);
            continentalHeight = 66.0 + t * 10.0; // Plains / Forests: 66 -> 76
        } else if (cont < 0.65f) {
            double t = smoothStep(0.35, 0.65, cont);
            continentalHeight = 76.0 + t * 20.0; // Plateaus: 76 -> 96
        } else {
            double t = smoothStep(0.65, 1.0, cont);
            continentalHeight = 96.0 + t * 24.0; // High Elevation: 96 -> 120
        }

        // Multi-octave rolling hills smoothly scaled by land factor
        double landFactor = smoothStep(-0.15, 0.05, cont);
        float hill = hillNoise2D.noise2D(x, z, true) * 9.0f * (float) landFactor;

        // Smooth continuous mountain boost (Erosion + Continentalness smoothly combined)
        // Strictly continuous everywhere: 0 at cont <= -0.1 or eros >= 0.2
        double mountainContFactor = smoothStep(-0.1, 0.4, cont);
        double mountainErosFactor = smoothStep(0.2, -0.6, eros);
        double peakBonus = (0.4 + 0.6 * Math.abs(weird)) * 120.0;
        double mountainElevation = mountainContFactor * mountainErosFactor * peakBonus;

        return continentalHeight + hill + mountainElevation;
    }

    private double computeDensity(int x, int y, int z, double baseHeight) {
        double heightOffset = (baseHeight - y) * 0.065;

        float n3d = terrainNoise3D.noise3D(x, y, z, true);
        float detail = detailNoise3D.noise3D(x, y, z, true) * 0.35f;

        return heightOffset + (n3d * 0.9) + detail;
    }

    private static double smoothStep(double edge0, double edge1, double x) {
        if (edge0 < edge1) {
            double t = Math.max(0.0, Math.min(1.0, (x - edge0) / (edge1 - edge0)));
            return t * t * (3.0 - 2.0 * t);
        } else {
            double t = Math.max(0.0, Math.min(1.0, (edge0 - x) / (edge0 - edge1)));
            return t * t * (3.0 - 2.0 * t);
        }
    }
}
