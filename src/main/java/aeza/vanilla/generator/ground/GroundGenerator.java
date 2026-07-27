package aeza.vanilla.generator.ground;

import aeza.vanilla.generator.biomegrid.BiomeIds;
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

        boolean isDesert = (topBlockId == BlockID.SAND) || (biome == BiomeIds.DESERT || biome == BiomeIds.DESERT_HILLS || biome == BiomeIds.DESERT_MUTATED);

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
                            chunk.setBlockId(blockX, y, blockZ, BlockID.SAND);
                            chunk.setBlockData(blockX, y, blockZ, 0);
                        } else {
                            if (y >= seaLevel - 1) {
                                chunk.setBlockId(blockX, y, blockZ, topBlockId);
                                chunk.setBlockData(blockX, y, blockZ, topBlockMeta);
                            } else {
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
                                chunk.setBlockId(blockX, y, blockZ, BlockID.SANDSTONE);
                                chunk.setBlockData(blockX, y, blockZ, 0);
                                deep = 4 + random.nextInt(3);
                                isDesert = false;
                            }
                        } else {
                            chunk.setBlockId(blockX, y, blockZ, groundBlockId);
                            chunk.setBlockData(blockX, y, blockZ, groundBlockMeta);
                        }
                    } else {
                        chunk.setBlockId(blockX, y, blockZ, BlockID.STONE);
                        chunk.setBlockData(blockX, y, blockZ, 0);
                    }
                }
            }
        }
    }
}
