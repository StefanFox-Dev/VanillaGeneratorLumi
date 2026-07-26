package aeza.vanilla.generator.cave;

import aeza.vanilla.generator.noise.SimplexOctaveGenerator;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;

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

    private static final boolean[] CARVABLE_BLOCKS = new boolean[1024];

    static {
        CARVABLE_BLOCKS[BlockID.STONE] = true;
        CARVABLE_BLOCKS[BlockID.DIRT] = true;
        CARVABLE_BLOCKS[BlockID.GRAVEL] = true;
        CARVABLE_BLOCKS[BlockID.GRASS] = true;
        CARVABLE_BLOCKS[BlockID.SAND] = true;
        CARVABLE_BLOCKS[BlockID.SANDSTONE] = true;
        CARVABLE_BLOCKS[BlockID.DEEPSLATE] = true;
    }

    private static boolean isCarvable(int id) {
        return id >= 0 && id < 1024 && CARVABLE_BLOCKS[id];
    }

    private final long seed;
    private final SimplexOctaveGenerator flowNoise;

    public CaveGenerator(long seed) {
        this.seed = seed;
        SplittableRandom flowRand = new SplittableRandom(seed + 11);
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

        carveEllipsoid(chunk, centerX, centerY, centerZ, radiusX, radiusY, radiusZ, targetChunkX, targetChunkZ);
    }

    private void carveSphere(FullChunk chunk, int centerX, int centerY, int centerZ, float radius, int targetChunkX, int targetChunkZ) {
        int minX = (int) Math.floor(centerX - radius);
        int maxX = (int) Math.ceil(centerX + radius);
        int minY = Math.max(1, (int) Math.floor(centerY - radius));
        int maxY = Math.min(250, (int) Math.ceil(centerY + radius));
        int minZ = (int) Math.floor(centerZ - radius);
        int maxZ = (int) Math.ceil(centerZ + radius);

        int chunkMinX = targetChunkX << 4;
        int chunkMaxX = chunkMinX + 15;
        int chunkMinZ = targetChunkZ << 4;
        int chunkMaxZ = chunkMinZ + 15;

        if (maxX < chunkMinX || minX > chunkMaxX || maxZ < chunkMinZ || minZ > chunkMaxZ) {
            return;
        }

        float radiusSq = radius * radius;

        for (int x = Math.max(minX, chunkMinX); x <= Math.min(maxX, chunkMaxX); x++) {
            float dx = x - centerX;
            float dxSq = dx * dx;
            int localX = x & 0x0f;

            for (int z = Math.max(minZ, chunkMinZ); z <= Math.min(maxZ, chunkMaxZ); z++) {
                float dz = z - centerZ;
                float dzSq = dz * dz;
                int localZ = z & 0x0f;

                for (int y = minY; y <= maxY; y++) {
                    float dy = y - centerY;
                    if (dxSq + dy * dy + dzSq <= radiusSq) {
                        int cur = chunk.getBlockId(localX, y, localZ);
                        if (isCarvable(cur)) {
                            chunk.setBlockId(localX, y, localZ, BlockID.AIR);
                        }
                    }
                }
            }
        }
    }

    private void carveEllipsoid(FullChunk chunk, int centerX, int centerY, int centerZ, float radX, float radY, float radZ, int targetChunkX, int targetChunkZ) {
        int minX = (int) Math.floor(centerX - radX);
        int maxX = (int) Math.ceil(centerX + radX);
        int minY = Math.max(1, (int) Math.floor(centerY - radY));
        int maxY = Math.min(250, (int) Math.ceil(centerY + radY));
        int minZ = (int) Math.floor(centerZ - radZ);
        int maxZ = (int) Math.ceil(centerZ + radZ);

        int chunkMinX = targetChunkX << 4;
        int chunkMaxX = chunkMinX + 15;
        int chunkMinZ = targetChunkZ << 4;
        int chunkMaxZ = chunkMinZ + 15;

        if (maxX < chunkMinX || minX > chunkMaxX || maxZ < chunkMinZ || minZ > chunkMaxZ) {
            return;
        }

        for (int x = Math.max(minX, chunkMinX); x <= Math.min(maxX, chunkMaxX); x++) {
            float dx = (x - centerX) / radX;
            float dxSq = dx * dx;
            int localX = x & 0x0f;

            for (int z = Math.max(minZ, chunkMinZ); z <= Math.min(maxZ, chunkMaxZ); z++) {
                float dz = (z - centerZ) / radZ;
                float dzSq = dz * dz;
                int localZ = z & 0x0f;

                for (int y = minY; y <= maxY; y++) {
                    float dy = (y - centerY) / radY;
                    if (dxSq + dy * dy + dzSq <= 1.0f) {
                        int cur = chunk.getBlockId(localX, y, localZ);
                        if (isCarvable(cur)) {
                            chunk.setBlockId(localX, y, localZ, BlockID.AIR);
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
