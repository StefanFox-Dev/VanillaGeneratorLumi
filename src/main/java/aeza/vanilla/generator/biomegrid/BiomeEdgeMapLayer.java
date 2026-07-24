package aeza.vanilla.generator.biomegrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeEdgeMapLayer extends MapLayer {

    private static final List<BiomeEdgeEntry> EDGES = new ArrayList<>();

    static {
        Map<Integer, Integer> mesa = new HashMap<>();
        mesa.put(BiomeIds.MESA_PLATEAU_STONE, BiomeIds.MESA);
        mesa.put(BiomeIds.MESA_PLATEAU, BiomeIds.MESA);

        Map<Integer, Integer> megaTaiga = new HashMap<>();
        megaTaiga.put(BiomeIds.MEGA_TAIGA, BiomeIds.TAIGA);

        Map<Integer, Integer> desert = new HashMap<>();
        desert.put(BiomeIds.DESERT, BiomeIds.EXTREME_HILLS_PLUS_TREES);

        Map<Integer, Integer> swamp1 = new HashMap<>();
        swamp1.put(BiomeIds.SWAMPLAND, BiomeIds.PLAINS);

        Map<Integer, Integer> swamp2 = new HashMap<>();
        swamp2.put(BiomeIds.SWAMPLAND, BiomeIds.JUNGLE_EDGE);

        EDGES.add(new BiomeEdgeEntry(mesa, null));
        EDGES.add(new BiomeEdgeEntry(megaTaiga, null));
        EDGES.add(new BiomeEdgeEntry(desert, new int[]{BiomeIds.ICE_PLAINS}));
        EDGES.add(new BiomeEdgeEntry(swamp1, new int[]{BiomeIds.DESERT, BiomeIds.COLD_TAIGA, BiomeIds.ICE_PLAINS}));
        EDGES.add(new BiomeEdgeEntry(swamp2, new int[]{BiomeIds.JUNGLE}));
    }

    private final MapLayer belowLayer;

    public BiomeEdgeMapLayer(long seed, MapLayer belowLayer) {
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
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                int val = centerVal;
                for (BiomeEdgeEntry edge : EDGES) {
                    if (edge.key.containsKey(centerVal)) {
                        int upperVal = values[j + 1 + i * gridSizeX];
                        int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                        int leftVal = values[j + (i + 1) * gridSizeX];
                        int rightVal = values[j + 2 + (i + 1) * gridSizeX];

                        if (edge.value == null && (!edge.key.containsKey(upperVal) || !edge.key.containsKey(lowerVal) || !edge.key.containsKey(leftVal) || !edge.key.containsKey(rightVal))) {
                            val = edge.key.get(centerVal);
                            break;
                        }

                        if (edge.value != null && (edge.value.contains(upperVal) || edge.value.contains(lowerVal) || edge.value.contains(leftVal) || edge.value.contains(rightVal))) {
                            val = edge.key.get(centerVal);
                            break;
                        }
                    }
                }
                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }
}
