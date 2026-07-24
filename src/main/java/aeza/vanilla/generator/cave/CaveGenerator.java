package aeza.vanilla.generator.cave;

import aeza.vanilla.generator.noise.SimplexOctaveGenerator;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitRandom;

import java.util.SplittableRandom;

public class CaveGenerator {
    private static final double CAVE_FREQUENCY = 0.06;
    private static final double CAVERN_FREQUENCY = 0.01;
    private static final int TUNNEL_LENGTH_MIN = 40;
    private static final int TUNNEL_LENGTH_MAX = 120;
    private static final int CAVERN_SIZE_MIN = 4;
    private static final int CAVERN_SIZE_MAX = 9;
    private static final int MIN_CAVE_Y = 5;
    private static final int MAX_CAVE_Y = 55;

    private final long seed;
    private final NukkitRandom random;
    private final SimplexOctaveGenerator flowNoise;

    public CaveGenerator(long seed) {
        this.seed = seed;
        this.random = new NukkitRandom(seed);
        NukkitRandom flowRand = new NukkitRandom(seed + 11);
        this.flowNoise = new SimplexOctaveGenerator(flowRand, 3);
    }

    public void carveDirectly(FullChunk chunk, int chunkX, int chunkZ) {
        if (chunk == null) return;
        generateRegionalCaves(chunk, chunkX, chunkZ);
        applyLavaPools(chunk);
    }

    private void generateRegionalCaves(FullChunk chunk, int chunkX, int chunkZ) {
        SplittableRandom rng = new SplittableRandom(seed ^ (chunkX * 341873128712L + chunkZ * 132897987541L));
        for (int regionX = chunkX - 1; regionX <= chunkX + 1; regionX++) {
            for (int regionZ = chunkZ - 1; regionZ <= chunkZ + 1; regionZ++) {
                int caveAttempts = 2 + rng.nextInt(3);
                for (int i = 0; i < caveAttempts; i++) {
                    if (rng.nextDouble() < CAVE_FREQUENCY) {
                        int startX = (regionX << 4) + rng.nextInt(16);
                        int startY = MIN_CAVE_Y + rng.nextInt(MAX_CAVE_Y - MIN_CAVE_Y);
                        int startZ = (regionZ << 4) + rng.nextInt(16);
                        generateCaveSystem(chunk, startX, startY, startZ, chunkX, chunkZ, rng);
                    }
                }

                if (rng.nextDouble() < CAVERN_FREQUENCY) {
                    int centerX = (regionX << 4) + rng.nextInt(16);
                    int centerY = MIN_CAVE_Y + rng.nextInt(MAX_CAVE_Y - MIN_CAVE_Y);
                    int centerZ = (regionZ << 4) + rng.nextInt(16);
                    generateNaturalCavern(chunk, centerX, centerY, centerZ, chunkX, chunkZ, rng);
                }
            }
        }
    }

    private void generateCaveSystem(FullChunk chunk, int startX, int startY, int startZ, int targetChunkX, int targetChunkZ, SplittableRandom rng) {
        int length = TUNNEL_LENGTH_MIN + rng.nextInt(TUNNEL_LENGTH_MAX - TUNNEL_LENGTH_MIN);
        double x = startX;
        double y = startY;
        double z = startZ;

        double yaw = rng.nextDouble() * Math.PI * 2.0;
        double pitch = (rng.nextDouble() - 0.5) * 0.25;

        for (int i = 0; i < length; i++) {
            double progress = i / (double) length;
            double baseRadius = 1.3 + rng.nextDouble() * 1.2;
            double sizeVariation = Math.sin(progress * Math.PI * 2.4) * 0.28 + (rng.nextDouble() - 0.5) * 0.3;
            double radius = Math.max(1.1, baseRadius + sizeVariation);

            carveSphere(chunk, (int) Math.round(x), (int) Math.round(y), (int) Math.round(z), (float) radius, targetChunkX, targetChunkZ);

            double noiseYaw = flowNoise.getNoise2D(x * 0.01, z * 0.01, 1.0, 1.0);
            double noisePitch = flowNoise.getNoise2D((x + 1000) * 0.01, (z + 1000) * 0.01, 1.0, 1.0);
            yaw += noiseYaw * 0.35;
            pitch += noisePitch * 0.22;
            pitch = Math.max(-0.46, Math.min(0.46, pitch));

            x += Math.cos(yaw) * Math.cos(pitch) * 0.95;
            y += Math.sin(pitch) * 0.95;
            z += Math.sin(yaw) * Math.cos(pitch) * 0.95;

            if (y < MIN_CAVE_Y + 2) {
                y = MIN_CAVE_Y + 2;
                pitch = Math.abs(pitch);
            } else if (y > MAX_CAVE_Y - 2) {
                y = MAX_CAVE_Y - 2;
                pitch = -Math.abs(pitch);
            }
        }
    }

    private void generateNaturalCavern(FullChunk chunk, int centerX, int centerY, int centerZ, int targetChunkX, int targetChunkZ, SplittableRandom rng) {
        int radiusX = CAVERN_SIZE_MIN + rng.nextInt(CAVERN_SIZE_MAX - CAVERN_SIZE_MIN);
        int radiusY = CAVERN_SIZE_MIN + rng.nextInt(CAVERN_SIZE_MAX - CAVERN_SIZE_MIN) / 2;
        int radiusZ = CAVERN_SIZE_MIN + rng.nextInt(CAVERN_SIZE_MAX - CAVERN_SIZE_MIN);

        int minX = centerX - radiusX;
        int maxX = centerX + radiusX;
        int minY = Math.max(MIN_CAVE_Y, centerY - radiusY);
        int maxY = Math.min(MAX_CAVE_Y, centerY + radiusY);
        int minZ = centerZ - radiusZ;
        int maxZ = centerZ + radiusZ;

        int targetBaseX = targetChunkX << 4;
        int targetBaseZ = targetChunkZ << 4;

        for (int bx = minX; bx <= maxX; bx++) {
            if (bx < targetBaseX || bx >= targetBaseX + 16) continue;
            double dx = (bx - centerX) / (double) radiusX;

            for (int bz = minZ; bz <= maxZ; bz++) {
                if (bz < targetBaseZ || bz >= targetBaseZ + 16) continue;
                double dz = (bz - centerZ) / (double) radiusZ;

                for (int by = minY; by <= maxY; by++) {
                    double dy = (by - centerY) / (double) radiusY;

                    if (dx * dx + dy * dy + dz * dz <= 1.0) {
                        int localX = bx & 15;
                        int localZ = bz & 15;
                        int currentBlock = chunk.getBlockId(localX, by, localZ);

                        if (currentBlock != BlockID.BEDROCK && currentBlock != BlockID.WATER && currentBlock != BlockID.STILL_WATER) {
                            chunk.setBlockId(localX, by, localZ, BlockID.AIR);
                        }
                    }
                }
            }
        }
    }

    private void carveSphere(FullChunk chunk, int cx, int cy, int cz, float radius, int targetChunkX, int targetChunkZ) {
        int r = (int) Math.ceil(radius);
        int minX = cx - r;
        int maxX = cx + r;
        int minY = Math.max(MIN_CAVE_Y, cy - r);
        int maxY = Math.min(MAX_CAVE_Y, cy + r);
        int minZ = cz - r;
        int maxZ = cz + r;

        int targetBaseX = targetChunkX << 4;
        int targetBaseZ = targetChunkZ << 4;
        float rSq = radius * radius;

        for (int bx = minX; bx <= maxX; bx++) {
            if (bx < targetBaseX || bx >= targetBaseX + 16) continue;
            int dx = bx - cx;

            for (int bz = minZ; bz <= maxZ; bz++) {
                if (bz < targetBaseZ || bz >= targetBaseZ + 16) continue;
                int dz = bz - cz;

                for (int by = minY; by <= maxY; by++) {
                    int dy = by - cy;

                    if (dx * dx + dy * dy + dz * dz <= rSq) {
                        int localX = bx & 15;
                        int localZ = bz & 15;
                        int currentBlock = chunk.getBlockId(localX, by, localZ);

                        if (currentBlock != BlockID.BEDROCK && currentBlock != BlockID.WATER && currentBlock != BlockID.STILL_WATER) {
                            chunk.setBlockId(localX, by, localZ, BlockID.AIR);
                        }
                    }
                }
            }
        }
    }

    private void applyLavaPools(FullChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 1; y <= 10; y++) {
                    if (chunk.getBlockId(x, y, z) == BlockID.AIR) {
                        chunk.setBlockId(x, y, z, BlockID.STILL_LAVA);
                    }
                }
            }
        }
    }
}
