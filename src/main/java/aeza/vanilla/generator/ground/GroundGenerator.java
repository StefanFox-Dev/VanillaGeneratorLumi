package aeza.vanilla.generator.ground;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class GroundGenerator {
    protected int topBlockId;
    protected int topBlockMeta;
    protected int groundBlockId;
    protected int groundBlockMeta;
    protected int bedrockRoughness = 5;

    public GroundGenerator(int topBlockId, int topBlockMeta, int groundBlockId, int groundBlockMeta) {
        this.topBlockId = topBlockId;
        this.topBlockMeta = topBlockMeta;
        this.groundBlockId = groundBlockId;
        this.groundBlockMeta = groundBlockMeta;
    }

    public GroundGenerator(int topBlockId, int groundBlockId) {
        this(topBlockId, 0, groundBlockId, 0);
    }

    public GroundGenerator() {
        this(BlockID.GRASS, 0, BlockID.DIRT, 0);
    }

    public void generateTerrainColumn(FullChunk chunk, SplittableRandom random, int blockX, int blockZ, int biome, double surfaceNoise) {
        int seaLevel = 64;

        int surfaceHeight = Math.max((int) (surfaceNoise / 3.0 + 3.0 + random.nextDouble() * 0.25), 1);
        int deep = -1;

        int minY = 0;
        int maxY = 255;

        boolean isDesert = (topBlockId == BlockID.SAND);

        for (int y = maxY; y >= minY; --y) {
            if (y <= minY + random.nextInt(bedrockRoughness)) {
                chunk.setBlockId(blockX, y, blockZ, BlockID.BEDROCK);
            } else {
                int matId = chunk.getBlockId(blockX, y, blockZ);
                if (matId == BlockID.AIR) {
                    deep = -1;
                } else if (matId == BlockID.STONE) {
                    if (deep == -1) {
                        deep = surfaceHeight;

                        if (isDesert) {
                            // Top 3-5 blocks of desert: SAND
                            chunk.setBlockId(blockX, y, blockZ, BlockID.SAND);
                            chunk.setBlockData(blockX, y, blockZ, 0);
                        } else {
                            if (y >= seaLevel - 1) {
                                chunk.setBlockId(blockX, y, blockZ, topBlockId);
                                chunk.setBlockData(blockX, y, blockZ, topBlockMeta);
                            } else {
                                // Underwater floor
                                chunk.setBlockId(blockX, y, blockZ, groundBlockId);
                                chunk.setBlockData(blockX, y, blockZ, groundBlockMeta);
                            }
                        }
                    } else if (deep > 0) {
                        --deep;

                        if (isDesert) {
                            if (deep > 0) {
                                chunk.setBlockId(blockX, y, blockZ, BlockID.SAND);
                                chunk.setBlockData(blockX, y, blockZ, 0);
                            } else {
                                // Sub-layer under desert sand: SANDSTONE
                                chunk.setBlockId(blockX, y, blockZ, BlockID.SANDSTONE);
                                chunk.setBlockData(blockX, y, blockZ, 0);
                                deep = 3 + random.nextInt(3); // 3-6 layers of sandstone
                                isDesert = false; // Next layers below sandstone revert to natural stone
                            }
                        } else {
                            chunk.setBlockId(blockX, y, blockZ, groundBlockId);
                            chunk.setBlockData(blockX, y, blockZ, groundBlockMeta);
                        }
                    } else {
                        // Deep underground: pure solid STONE
                        chunk.setBlockId(blockX, y, blockZ, BlockID.STONE);
                        chunk.setBlockData(blockX, y, blockZ, 0);
                    }
                }
            }
        }
    }
}
