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
        int topId = topBlockId;
        int topMeta = topBlockMeta;
        int groundId = groundBlockId;
        int groundMeta = groundBlockMeta;

        int surfaceHeight = Math.max((int) (surfaceNoise / 3.0 + 3.0 + random.nextDouble() * 0.25), 1);
        int deep = -1;

        int minY = 0;
        int maxY = 255;

        for (int y = maxY; y >= minY; --y) {
            if (y <= minY + random.nextInt(bedrockRoughness)) {
                chunk.setBlockId(blockX, y, blockZ, BlockID.BEDROCK);
            } else {
                int matId = chunk.getBlockId(blockX, y, blockZ);
                if (matId == BlockID.AIR) {
                    deep = -1;
                } else if (matId == BlockID.STONE) {
                    if (deep == -1) {
                        if (y >= seaLevel - 5 && y <= seaLevel) {
                            topId = topBlockId;
                            topMeta = topBlockMeta;
                            groundId = groundBlockId;
                            groundMeta = groundBlockMeta;
                        }

                        deep = surfaceHeight;
                        if (y >= seaLevel - 2) {
                            chunk.setBlockId(blockX, y, blockZ, topId);
                            chunk.setBlockData(blockX, y, blockZ, topMeta);
                        } else if (y < seaLevel - 8 - surfaceHeight) {
                            topId = BlockID.AIR;
                            topMeta = 0;
                            groundId = BlockID.STONE;
                            groundMeta = 0;
                            chunk.setBlockId(blockX, y, blockZ, BlockID.GRAVEL);
                        } else {
                            chunk.setBlockId(blockX, y, blockZ, groundId);
                            chunk.setBlockData(blockX, y, blockZ, groundMeta);
                        }
                    } else if (deep > 0) {
                        --deep;
                        chunk.setBlockId(blockX, y, blockZ, groundId);
                        chunk.setBlockData(blockX, y, blockZ, groundMeta);

                        if (deep == 0 && groundId == BlockID.SAND) {
                            deep = random.nextInt(4) + Math.max(0, y - seaLevel - 1);
                            groundId = BlockID.SANDSTONE;
                            groundMeta = 0;
                        }
                    }
                }
            }
        }
    }
}
