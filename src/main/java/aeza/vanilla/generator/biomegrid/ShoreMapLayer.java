package aeza.vanilla.generator.biomegrid;

import java.util.HashMap;
import java.util.Map;

public class ShoreMapLayer extends MapLayer {

    private static final Map<Integer, Integer> SPECIAL_SHORES = new HashMap<>();

    static {
        SPECIAL_SHORES.put(BiomeIds.EXTREME_HILLS, BiomeIds.STONE_BEACH);
        SPECIAL_SHORES.put(BiomeIds.EXTREME_HILLS_PLUS_TREES, BiomeIds.STONE_BEACH);
        SPECIAL_SHORES.put(BiomeIds.EXTREME_HILLS_MUTATED, BiomeIds.STONE_BEACH);
        SPECIAL_SHORES.put(BiomeIds.EXTREME_HILLS_PLUS_TREES_MUTATED, BiomeIds.STONE_BEACH);
        SPECIAL_SHORES.put(BiomeIds.ICE_PLAINS, BiomeIds.COLD_BEACH);
        SPECIAL_SHORES.put(BiomeIds.ICE_MOUNTAINS, BiomeIds.COLD_BEACH);
        SPECIAL_SHORES.put(BiomeIds.ICE_PLAINS_SPIKES, BiomeIds.COLD_BEACH);
        SPECIAL_SHORES.put(BiomeIds.COLD_TAIGA, BiomeIds.COLD_BEACH);
        SPECIAL_SHORES.put(BiomeIds.COLD_TAIGA_HILLS, BiomeIds.COLD_BEACH);
        SPECIAL_SHORES.put(BiomeIds.COLD_TAIGA_MUTATED, BiomeIds.COLD_BEACH);
        SPECIAL_SHORES.put(BiomeIds.MUSHROOM_ISLAND, BiomeIds.MUSHROOM_ISLAND_SHORE);
        SPECIAL_SHORES.put(BiomeIds.SWAMPLAND, BiomeIds.SWAMPLAND);
        SPECIAL_SHORES.put(BiomeIds.MESA, BiomeIds.MESA);
        SPECIAL_SHORES.put(BiomeIds.MESA_PLATEAU_STONE, BiomeIds.MESA_PLATEAU_STONE);
        SPECIAL_SHORES.put(BiomeIds.MESA_PLATEAU_STONE_MUTATED, BiomeIds.MESA_PLATEAU_STONE_MUTATED);
        SPECIAL_SHORES.put(BiomeIds.MESA_PLATEAU, BiomeIds.MESA_PLATEAU);
        SPECIAL_SHORES.put(BiomeIds.MESA_PLATEAU_MUTATED, BiomeIds.MESA_PLATEAU_MUTATED);
        SPECIAL_SHORES.put(BiomeIds.MESA_BRYCE, BiomeIds.MESA_BRYCE);
    }

    private static boolean isOcean(int val) {
        return val == BiomeIds.OCEAN || val == BiomeIds.DEEP_OCEAN;
    }

    private final MapLayer belowLayer;

    public ShoreMapLayer(long seed, MapLayer belowLayer) {
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
                int upperVal = values[j + 1 + i * gridSizeX];
                int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                int leftVal = values[j + (i + 1) * gridSizeX];
                int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                if (!isOcean(centerVal) && (isOcean(upperVal) || isOcean(lowerVal) || isOcean(leftVal) || isOcean(rightVal))) {
                    finalValues[j + i * sizeX] = SPECIAL_SHORES.getOrDefault(centerVal, BiomeIds.BEACH);
                } else {
                    finalValues[j + i * sizeX] = centerVal;
                }
            }
        }
        return finalValues;
    }
}
