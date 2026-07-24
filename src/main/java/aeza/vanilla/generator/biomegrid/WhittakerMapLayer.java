package aeza.vanilla.generator.biomegrid;

public class WhittakerMapLayer extends MapLayer {
    public static final int WARM_WET = 0;
    public static final int COLD_DRY = 1;
    public static final int LARGER_BIOMES = 2;

    private static class Climate {
        final int value;
        final int[] crossTypes;
        final int finalValue;

        Climate(int value, int[] crossTypes, int finalValue) {
            this.value = value;
            this.crossTypes = crossTypes;
            this.finalValue = finalValue;
        }
    }

    private static final Climate[] MAP = new Climate[2];

    static {
        MAP[WARM_WET] = new Climate(2, new int[]{3, 1}, 4);
        MAP[COLD_DRY] = new Climate(3, new int[]{2, 4}, 1);
    }

    private final MapLayer belowLayer;
    private final int type;

    public WhittakerMapLayer(long seed, MapLayer belowLayer, int type) {
        super(seed);
        this.belowLayer = belowLayer;
        this.type = type;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        if (type == WARM_WET || type == COLD_DRY) {
            return swapValues(x, z, sizeX, sizeZ);
        }
        return modifyValues(x, z, sizeX, sizeZ);
    }

    private int[] swapValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;
        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        Climate climate = MAP[type];
        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; ++i) {
            for (int j = 0; j < sizeX; ++j) {
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                if (centerVal == climate.value) {
                    int upperVal = values[j + 1 + i * gridSizeX];
                    int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                    int leftVal = values[j + (i + 1) * gridSizeX];
                    int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                    for (int crossType : climate.crossTypes) {
                        if (upperVal == crossType || lowerVal == crossType || leftVal == crossType || rightVal == crossType) {
                            centerVal = climate.finalValue;
                            break;
                        }
                    }
                }
                finalValues[j + i * sizeX] = centerVal;
            }
        }
        return finalValues;
    }

    private int[] modifyValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = belowLayer.generateValues(x, z, sizeX, sizeZ);
        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; ++i) {
            for (int j = 0; j < sizeX; ++j) {
                int val = values[j + i * sizeX];
                if (val != 0) {
                    setCoordsSeed(x + j, z + i);
                    if (nextInt(13) == 0) {
                        val += 1000;
                    }
                }
                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }
}
