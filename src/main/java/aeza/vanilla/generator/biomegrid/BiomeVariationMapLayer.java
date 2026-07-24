package aeza.vanilla.generator.biomegrid;

import java.util.HashMap;
import java.util.Map;

public class BiomeVariationMapLayer extends MapLayer {

    private static final int[] ISLANDS = {BiomeIds.PLAINS, BiomeIds.FOREST};
    private static final Map<Integer, int[]> VARIATIONS = new HashMap<>();

    static {
        VARIATIONS.put(BiomeIds.DESERT, new int[]{BiomeIds.DESERT_HILLS});
        VARIATIONS.put(BiomeIds.FOREST, new int[]{BiomeIds.FOREST_HILLS});
        VARIATIONS.put(BiomeIds.BIRCH_FOREST, new int[]{BiomeIds.BIRCH_FOREST_HILLS});
        VARIATIONS.put(BiomeIds.ROOFED_FOREST, new int[]{BiomeIds.PLAINS});
        VARIATIONS.put(BiomeIds.TAIGA, new int[]{BiomeIds.TAIGA_HILLS});
        VARIATIONS.put(BiomeIds.MEGA_TAIGA, new int[]{BiomeIds.MEGA_TAIGA_HILLS});
        VARIATIONS.put(BiomeIds.COLD_TAIGA, new int[]{BiomeIds.COLD_TAIGA_HILLS});
        VARIATIONS.put(BiomeIds.PLAINS, new int[]{BiomeIds.FOREST, BiomeIds.FOREST, BiomeIds.FOREST_HILLS});
        VARIATIONS.put(BiomeIds.ICE_PLAINS, new int[]{BiomeIds.ICE_MOUNTAINS});
        VARIATIONS.put(BiomeIds.JUNGLE, new int[]{BiomeIds.JUNGLE_HILLS});
        VARIATIONS.put(BiomeIds.OCEAN, new int[]{BiomeIds.DEEP_OCEAN});
        VARIATIONS.put(BiomeIds.EXTREME_HILLS, new int[]{BiomeIds.EXTREME_HILLS_PLUS_TREES});
        VARIATIONS.put(BiomeIds.SAVANNA, new int[]{BiomeIds.SAVANNA_PLATEAU});
        VARIATIONS.put(BiomeIds.MESA_PLATEAU_STONE, new int[]{BiomeIds.MESA});
        VARIATIONS.put(BiomeIds.MESA_PLATEAU, new int[]{BiomeIds.MESA});
        VARIATIONS.put(BiomeIds.MESA, new int[]{BiomeIds.MESA});
    }

    private static boolean isValidBiome(int id) {
        return id >= 0 && id <= 167;
    }

    private final MapLayer belowLayer;
    private final MapLayer variationLayer;

    public BiomeVariationMapLayer(long seed, MapLayer belowLayer, MapLayer variationLayer) {
        super(seed);
        this.belowLayer = belowLayer;
        this.variationLayer = variationLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        if (variationLayer == null) {
            return generateRandomValues(x, z, sizeX, sizeZ);
        }
        return mergeValues(x, z, sizeX, sizeZ);
    }

    public int[] generateRandomValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = belowLayer.generateValues(x, z, sizeX, sizeZ);
        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; ++i) {
            for (int j = 0; j < sizeX; ++j) {
                int val = values[j + i * sizeX];
                if (val > 0) {
                    setCoordsSeed(x + j, z + i);
                    val = nextInt(30) + 2;
                }
                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }

    public int[] mergeValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;

        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);
        int[] variationValues = variationLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; ++i) {
            for (int j = 0; j < sizeX; ++j) {
                setCoordsSeed(x + j, z + i);
                int centerValue = values[j + 1 + (i + 1) * gridSizeX];
                int variationValue = variationValues[j + 1 + (i + 1) * gridSizeX];

                if (centerValue != 0 && variationValue == 3 && centerValue < 128) {
                    finalValues[j + i * sizeX] = isValidBiome(centerValue + 128) ? centerValue + 128 : centerValue;
                } else if (variationValue == 2 || nextInt(3) == 0) {
                    int val = centerValue;
                    if (VARIATIONS.containsKey(centerValue)) {
                        int[] vars = VARIATIONS.get(centerValue);
                        val = vars[nextInt(vars.length)];
                    } else if (centerValue == BiomeIds.DEEP_OCEAN && nextInt(3) == 0) {
                        val = ISLANDS[nextInt(ISLANDS.length)];
                    }
                    if (variationValue == 2 && val != centerValue) {
                        val = isValidBiome(val + 128) ? val + 128 : centerValue;
                    }
                    if (val != centerValue) {
                        int count = 0;
                        if (values[j + 1 + i * gridSizeX] == centerValue) ++count;
                        if (values[j + 1 + (i + 2) * gridSizeX] == centerValue) ++count;
                        if (values[j + (i + 1) * gridSizeX] == centerValue) ++count;
                        if (values[j + 2 + (i + 1) * gridSizeX] == centerValue) ++count;

                        finalValues[j + i * sizeX] = count < 3 ? centerValue : val;
                    } else {
                        finalValues[j + i * sizeX] = val;
                    }
                } else {
                    finalValues[j + i * sizeX] = centerValue;
                }
            }
        }

        return finalValues;
    }
}
