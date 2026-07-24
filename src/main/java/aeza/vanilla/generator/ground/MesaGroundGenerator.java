package aeza.vanilla.generator.ground;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;

import java.util.Arrays;
import java.util.SplittableRandom;

public class MesaGroundGenerator extends GroundGenerator {
    public static final int NORMAL = 0;
    public static final int BRYCE = 1;
    public static final int FOREST = 2;

    private final int type;
    private final int[] colorLayer = new int[64];

    public MesaGroundGenerator(int type) {
        super(BlockID.TERRACOTTA, 0, BlockID.TERRACOTTA, 1); // Orange terracotta
        this.type = type;
        initColorLayers();
    }

    public MesaGroundGenerator() {
        this(NORMAL);
    }

    private void initColorLayers() {
        Arrays.fill(colorLayer, -1);
        for (int i = 0; i < 64; i += 8) {
            colorLayer[i] = 1; // Orange
        }
        colorLayer[3] = 8;  // Light gray
        colorLayer[14] = 4; // Yellow
        colorLayer[21] = 1; // Orange
        colorLayer[25] = 14; // Red
        colorLayer[26] = 14;
        colorLayer[34] = 1;
        colorLayer[42] = 14; // Red
        colorLayer[50] = 8;
    }

    @Override
    public void generateTerrainColumn(FullChunk chunk, SplittableRandom random, int blockX, int blockZ, int biome, double surfaceNoise) {
        int seaLevel = 64;
        int surfaceHeight = Math.max((int) (surfaceNoise / 3.0 + 3.0 + random.nextDouble() * 0.25), 1);
        boolean colored = Math.cos(surfaceNoise / 3.0 * Math.PI) <= 0;

        int deep = -1;
        boolean groundSet = false;

        int minY = -64;
        int maxY = 319;

        for (int y = maxY; y >= minY; --y) {
            if (y <= minY + random.nextInt(bedrockRoughness)) {
                chunk.setBlockId(blockX, y, blockZ, BlockID.BEDROCK);
            } else {
                int matId = chunk.getBlockId(blockX, y, blockZ);
                if (matId == BlockID.AIR) {
                    deep = -1;
                } else if (matId == BlockID.STONE) {
                    if (deep == -1) {
                        groundSet = false;
                        deep = surfaceHeight;
                        if (y >= seaLevel - 1) {
                            if (type == FOREST) {
                                chunk.setBlockId(blockX, y, blockZ, BlockID.GRASS);
                            } else {
                                chunk.setBlockId(blockX, y, blockZ, BlockID.SAND);
                                chunk.setBlockData(blockX, y, blockZ, 1); // Red sand
                            }
                        } else {
                            chunk.setBlockId(blockX, y, blockZ, BlockID.TERRACOTTA);
                        }
                    } else if (deep > 0) {
                        --deep;
                        if (colored) {
                            int color = colorLayer[(y + 64) & 63];
                            if (color >= 0) {
                                chunk.setBlockId(blockX, y, blockZ, BlockID.STAINED_TERRACOTTA);
                                chunk.setBlockData(blockX, y, blockZ, color);
                            } else {
                                chunk.setBlockId(blockX, y, blockZ, BlockID.TERRACOTTA);
                            }
                        } else {
                            chunk.setBlockId(blockX, y, blockZ, BlockID.TERRACOTTA);
                        }
                    }
                }
            }
        }
    }
}
