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
            // Deep Ocean: 36 -> 48
            double t = smoothStep(-1.0, -0.45, cont);
            continentalHeight = 36.0 + t * 12.0;
        } else if (cont < -0.15f) {
            // Ocean: 48 -> 58
            double t = smoothStep(-0.45, -0.15, cont);
            continentalHeight = 48.0 + t * 10.0;
        } else if (cont < -0.04f) {
            // Coast: 58 -> 63
            double t = smoothStep(-0.15, -0.04, cont);
            continentalHeight = 58.0 + t * 5.0;
        } else if (cont < 0.05f) {
            // Beach / Near Shore: 63 -> 66
            double t = smoothStep(-0.04, 0.05, cont);
            continentalHeight = 63.0 + t * 3.0;
        } else if (cont < 0.35f) {
            // Plains, Lowlands & Forests: 66 -> 76
            double t = smoothStep(0.05, 0.35, cont);
            continentalHeight = 66.0 + t * 10.0;
        } else if (cont < 0.65f) {
            // Highlands & Plateaus: 76 -> 96
            double t = smoothStep(0.35, 0.65, cont);
            continentalHeight = 76.0 + t * 20.0;
        } else {
            // Inland High Elevation: 96 -> 135
            double t = smoothStep(0.65, 1.0, cont);
            continentalHeight = 96.0 + t * 39.0;
        }

        // Multi-octave rolling hills
        float hill = hillNoise2D.noise2D(x, z, true) * 10.0f;

        // Smooth continuous mountain boost (Erosion: lower erosion = higher peaks)
        // Smoothly scales from 0.0 at eros = 0.1 to 1.0 at eros = -0.7
        double mountainT = smoothStep(0.1, -0.7, eros);
        double peakBonus = (0.5 + 0.5 * Math.abs(weird)) * 130.0;
        double mountainElevation = mountainT * peakBonus;

        return continentalHeight + (cont > -0.04f ? hill : hill * 0.3) + (cont > 0.0f ? mountainElevation : 0);
    }

    private double computeDensity(int x, int y, int z, double baseHeight) {
        double heightOffset = (baseHeight - y) * 0.07;

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
