package aeza.vanilla.generator.biomegrid;

import java.util.HashMap;
import java.util.Map;

public class RiverMapLayer extends MapLayer {

    private static final Map<Integer, Integer> SPECIAL_RIVERS = new HashMap<>();

    static {
        SPECIAL_RIVERS.put(BiomeIds.ICE_PLAINS, BiomeIds.FROZEN_RIVER);
        SPECIAL_RIVERS.put(BiomeIds.MUSHROOM_ISLAND, BiomeIds.MUSHROOM_ISLAND_SHORE);
        SPECIAL_RIVERS.put(BiomeIds.MUSHROOM_ISLAND_SHORE, BiomeIds.MUSHROOM_ISLAND_SHORE);
    }

    private static final int CLEAR_VALUE = 0;
    private static final int RIVER_VALUE = 1;

    private static boolean isOcean(int val) {
        return val == BiomeIds.OCEAN || val == BiomeIds.DEEP_OCEAN;
    }

    private final MapLayer belowLayer;
    private final MapLayer mergeLayer;

    public RiverMapLayer(long seed, MapLayer belowLayer, MapLayer mergeLayer) {
        super(seed);
        this.belowLayer = belowLayer;
        this.mergeLayer = mergeLayer;
    }

    public RiverMapLayer(long seed, MapLayer belowLayer) {
        this(seed, belowLayer, null);
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        if (mergeLayer == null) {
            return generateRivers(x, z, sizeX, sizeZ);
        }
        return mergeRivers(x, z, sizeX, sizeZ);
    }

    private int[] generateRivers(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;

        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);
        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; ++i) {
            for (int j = 0; j < sizeX; ++j) {
                int centerVal = values[j + 1 + (i + 1) * gridSizeX] & 1;
                int upperVal = values[j + 1 + i * gridSizeX] & 1;
                int lowerVal = values[j + 1 + (i + 2) * gridSizeX] & 1;
                int leftVal = values[j + (i + 1) * gridSizeX] & 1;
                int rightVal = values[j + 2 + (i + 1) * gridSizeX] & 1;
                int val = CLEAR_VALUE;
                if (centerVal != upperVal || centerVal != lowerVal || centerVal != leftVal || centerVal != rightVal) {
                    val = RIVER_VALUE;
                }
                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }

    private int[] mergeRivers(int x, int z, int sizeX, int sizeZ) {
        int[] values = belowLayer.generateValues(x, z, sizeX, sizeZ);
        int[] mergeValues = mergeLayer.generateValues(x, z, sizeX, sizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeX * sizeZ; ++i) {
            int val = mergeValues[i];
            if (isOcean(mergeValues[i])) {
                val = mergeValues[i];
            } else if (values[i] == RIVER_VALUE) {
                val = SPECIAL_RIVERS.getOrDefault(mergeValues[i], BiomeIds.RIVER);
            }
            finalValues[i] = val;
        }
        return finalValues;
    }
}
