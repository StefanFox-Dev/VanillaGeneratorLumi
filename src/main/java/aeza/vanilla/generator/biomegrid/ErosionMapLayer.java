package aeza.vanilla.generator.biomegrid;

public class ErosionMapLayer extends MapLayer {
    private final MapLayer belowLayer;

    public ErosionMapLayer(long seed, MapLayer belowLayer) {
        super(seed);
        this.belowLayer = belowLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;
        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; ++i) {
            for (int j = 0; j < sizeX; ++j) {
                int upperLeft = values[j + i * gridSizeX];
                int lowerLeft = values[j + (i + 2) * gridSizeX];
                int upperRight = values[j + 2 + i * gridSizeX];
                int lowerRight = values[j + 2 + (i + 2) * gridSizeX];
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];

                setCoordsSeed(x + j, z + i);

                if (centerVal != 0 && (upperLeft == 0 || upperRight == 0 || lowerLeft == 0 || lowerRight == 0)) {
                    finalValues[j + i * sizeX] = nextInt(8) == 0 ? 0 : centerVal;
                } else if (centerVal == 0 && (upperLeft != 0 || upperRight != 0 || lowerLeft != 0 || lowerRight != 0)) {
                    finalValues[j + i * sizeX] = nextInt(2) == 0 ? upperLeft : 0;
                } else {
                    finalValues[j + i * sizeX] = centerVal;
                }
            }
        }

        return finalValues;
    }
}
