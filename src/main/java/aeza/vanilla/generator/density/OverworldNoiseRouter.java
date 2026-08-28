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
        this.terrainNoise3D = new SimplexF(rand, 3F, 1F / 2F, 1F / 64f);
        this.detailNoise3D = new SimplexF(rand, 2F, 1F / 2F, 1F / 24f);
        this.hillNoise2D = new SimplexF(rand, 4F, 1F / 2F, 1F / 128f);
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

                // Compute smooth natural target height
                double baseHeight = computeSmoothBaseHeight(worldX, worldZ, cont, eros, weird);

                int bedrockHeight = MIN_Y + random.nextInt(4);

                for (int y = MAX_Y; y >= MIN_Y; y--) {
                    if (y <= bedrockHeight) {
                        chunk.setBlockId(x, y, z, BlockID.BEDROCK);
                        continue;
                    }

                    double density = computeDensity(worldX, y, worldZ, baseHeight, eros);

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
        double height;

        // Multi-octave 2D rolling hills & relief noise
        float hill = hillNoise2D.noise2D(x, z, true) * 12.0f;

        if (cont < -0.18f) {
            // Deep Ocean to Shallow Ocean (Y = 32 to 60)
            double t = (cont + 1.0f) / 0.82f;
            height = 32.0 + t * 28.0;
        } else if (cont < 0.05f) {
            // Coast / Shore / Beach (Y = 60 to 66)
            double t = (cont + 0.18f) / 0.23f;
            height = 60.0 + t * 6.0;
        } else {
            // Inland Continents (Y = 66 to 110 base + natural hills)
            double t = (cont - 0.05f) / 0.95f;
            height = 66.0 + t * 38.0 + hill;

            // Low erosion + weirdness creates soaring mountain peaks (Y = 120..260)
            if (eros < -0.15f) {
                double mountainStrength = Math.abs(eros + 0.15) * 1.6;
                double peakBonus = (0.6 + Math.abs(weird) * 0.8) * 140.0;
                height += mountainStrength * peakBonus;
            }
        }

        return height;
    }

    private double computeDensity(int x, int y, int z, double baseHeight, float eros) {
        double heightOffset = (baseHeight - y) * 0.12;

        float n3d = terrainNoise3D.noise3D(x, y, z, true);
        float detail = detailNoise3D.noise3D(x, y, z, true) * 0.35f;

        return heightOffset + (n3d * 0.9) + detail;
    }
}
