package aeza.vanilla.generator.biomegrid;

public class ZoomMapLayer extends MapLayer {
    public static final int NORMAL = 0;
    public static final int BLURRY = 1;

    private final MapLayer belowLayer;
    private final int zoomType;

    public ZoomMapLayer(long seed, MapLayer belowLayer, int zoomType) {
        super(seed);
        this.belowLayer = belowLayer;
        this.zoomType = zoomType;
    }

    public ZoomMapLayer(long seed, MapLayer belowLayer) {
        this(seed, belowLayer, NORMAL);
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x >> 1;
        int gridZ = z >> 1;
        int gridSizeX = (sizeX >> 1) + 2;
        int gridSizeZ = (sizeZ >> 1) + 2;
        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int zoomSizeX = (gridSizeX - 1) << 1;
        int zoomSizeZ = (gridSizeZ - 1) << 1;
        int[] tmpValues = new int[zoomSizeX * zoomSizeZ];

        for (int i = 0; i < gridSizeZ - 1; ++i) {
            int n = i * 2 * zoomSizeX;
            int upperLeftVal = values[i * gridSizeX];
            int lowerLeftVal = values[(i + 1) * gridSizeX];
            for (int j = 0; j < gridSizeX - 1; ++j) {
                setCoordsSeed((gridX + j) << 1, (gridZ + i) << 1);
                tmpValues[n] = upperLeftVal;
                tmpValues[n + zoomSizeX] = nextInt(2) > 0 ? upperLeftVal : lowerLeftVal;
                int upperRightVal = values[j + 1 + i * gridSizeX];
                int lowerRightVal = values[j + 1 + (i + 1) * gridSizeX];
                tmpValues[n + 1] = nextInt(2) > 0 ? upperLeftVal : upperRightVal;
                tmpValues[n + 1 + zoomSizeX] = getNearest(upperLeftVal, upperRightVal, lowerLeftVal, lowerRightVal);
                upperLeftVal = upperRightVal;
                lowerLeftVal = lowerRightVal;
                n += 2;
            }
        }

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; ++i) {
            for (int j = 0; j < sizeX; ++j) {
                finalValues[j + i * sizeX] = tmpValues[j + (i + (z & 1)) * zoomSizeX + (x & 1)];
            }
        }
        return finalValues;
    }

    private int getNearest(int upperLeft, int upperRight, int lowerLeft, int lowerRight) {
        if (zoomType == NORMAL) {
            if (upperRight == lowerLeft && lowerLeft == lowerRight) return upperRight;
            if (upperLeft == upperRight && upperLeft == lowerLeft) return upperLeft;
            if (upperLeft == upperRight && upperLeft == lowerRight) return upperLeft;
            if (upperLeft == lowerLeft && upperLeft == lowerRight) return upperLeft;
            if (upperLeft == upperRight && lowerLeft != lowerRight) return upperLeft;
            if (upperLeft == lowerLeft && upperRight != lowerRight) return upperLeft;
            if (upperLeft == lowerRight && upperRight != lowerLeft) return upperLeft;
            if (upperRight == lowerLeft && upperLeft != lowerRight) return upperRight;
            if (upperRight == lowerRight && upperLeft != lowerLeft) return upperRight;
            if (lowerLeft == lowerRight && upperLeft != upperRight) return lowerLeft;
        }

        int[] vals = {upperLeft, upperRight, lowerLeft, lowerRight};
        return vals[nextInt(4)];
    }
}
