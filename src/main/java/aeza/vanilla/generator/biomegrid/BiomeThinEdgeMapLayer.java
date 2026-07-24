package aeza.vanilla.generator.biomegrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeThinEdgeMapLayer extends MapLayer {

    private static final List<BiomeEdgeEntry> EDGES = new ArrayList<>();

    static {
        Map<Integer, Integer> mesa = new HashMap<>();
        mesa.put(BiomeIds.MESA, BiomeIds.DESERT);
        mesa.put(BiomeIds.MESA_BRYCE, BiomeIds.DESERT);
        mesa.put(BiomeIds.MESA_PLATEAU_STONE, BiomeIds.DESERT);
        mesa.put(BiomeIds.MESA_PLATEAU_STONE_MUTATED, BiomeIds.DESERT);
        mesa.put(BiomeIds.MESA_PLATEAU, BiomeIds.DESERT);
        mesa.put(BiomeIds.MESA_PLATEAU_MUTATED, BiomeIds.DESERT);

        Map<Integer, Integer> jungle = new HashMap<>();
        jungle.put(BiomeIds.JUNGLE, BiomeIds.JUNGLE_EDGE);
        jungle.put(BiomeIds.JUNGLE_HILLS, BiomeIds.JUNGLE_EDGE);
        jungle.put(BiomeIds.JUNGLE_MUTATED, BiomeIds.JUNGLE_EDGE);
        jungle.put(BiomeIds.JUNGLE_EDGE_MUTATED, BiomeIds.JUNGLE_EDGE);

        EDGES.add(new BiomeEdgeEntry(mesa, null));
        EDGES.add(new BiomeEdgeEntry(jungle, new int[]{
                BiomeIds.JUNGLE, BiomeIds.JUNGLE_HILLS, BiomeIds.JUNGLE_MUTATED, BiomeIds.JUNGLE_EDGE_MUTATED, BiomeIds.FOREST, BiomeIds.TAIGA
        }));
    }

    private static boolean isOcean(int val) {
        return val == BiomeIds.OCEAN || val == BiomeIds.DEEP_OCEAN;
    }

    private final MapLayer belowLayer;

    public BiomeThinEdgeMapLayer(long seed, MapLayer belowLayer) {
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

                        if (edge.value == null && (
                                (!isOcean(upperVal) && !edge.key.containsKey(upperVal)) ||
                                (!isOcean(lowerVal) && !edge.key.containsKey(lowerVal)) ||
                                (!isOcean(leftVal) && !edge.key.containsKey(leftVal)) ||
                                (!isOcean(rightVal) && !edge.key.containsKey(rightVal))
                        )) {
                            val = edge.key.get(centerVal);
                            break;
                        }

                        if (edge.value != null && (
                                (!isOcean(upperVal) && !edge.value.contains(upperVal)) ||
                                (!isOcean(lowerVal) && !edge.value.contains(lowerVal)) ||
                                (!isOcean(leftVal) && !edge.value.contains(leftVal)) ||
                                (!isOcean(rightVal) && !edge.value.contains(rightVal))
                        )) {
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
