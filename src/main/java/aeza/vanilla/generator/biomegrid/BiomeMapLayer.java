package aeza.vanilla.generator.biomegrid;

public class BiomeMapLayer extends MapLayer {

    private static final int[] WARM = {BiomeIds.DESERT, BiomeIds.DESERT, BiomeIds.DESERT, BiomeIds.SAVANNA, BiomeIds.SAVANNA, BiomeIds.PLAINS};
    private static final int[] WET = {BiomeIds.PLAINS, BiomeIds.PLAINS, BiomeIds.FOREST, BiomeIds.BIRCH_FOREST, BiomeIds.ROOFED_FOREST, BiomeIds.EXTREME_HILLS, BiomeIds.SWAMPLAND};
    private static final int[] DRY = {BiomeIds.PLAINS, BiomeIds.FOREST, BiomeIds.TAIGA, BiomeIds.EXTREME_HILLS};
    private static final int[] COLD = {BiomeIds.ICE_PLAINS, BiomeIds.ICE_PLAINS, BiomeIds.COLD_TAIGA};
    private static final int[] WARM_LARGE = {BiomeIds.MESA_PLATEAU_STONE, BiomeIds.MESA_PLATEAU_STONE, BiomeIds.MESA_PLATEAU};
    private static final int[] DRY_LARGE = {BiomeIds.MEGA_TAIGA};
    private static final int[] WET_LARGE = {BiomeIds.JUNGLE};

    private final MapLayer belowLayer;

    public BiomeMapLayer(long seed, MapLayer belowLayer) {
        super(seed);
        this.belowLayer = belowLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = belowLayer.generateValues(x, z, sizeX, sizeZ);
        int[] finalValues = new int[sizeX * sizeZ];

        for (int i = 0; i < sizeZ; ++i) {
            for (int j = 0; j < sizeX; ++j) {
                int val = values[j + i * sizeX];
                if (val != 0) {
                    setCoordsSeed(x + j, z + i);
                    switch (val) {
                        case 1 -> val = DRY[nextInt(DRY.length)];
                        case 2 -> val = WARM[nextInt(WARM.length)];
                        case 3, 1003 -> val = COLD[nextInt(COLD.length)];
                        case 4 -> val = WET[nextInt(WET.length)];
                        case 1001 -> val = DRY_LARGE[nextInt(DRY_LARGE.length)];
                        case 1002 -> val = WARM_LARGE[nextInt(WARM_LARGE.length)];
                        case 1004 -> val = WET_LARGE[nextInt(WET_LARGE.length)];
                    }
                }
                finalValues[j + i * sizeX] = val;
            }
        }

        return finalValues;
    }
}
