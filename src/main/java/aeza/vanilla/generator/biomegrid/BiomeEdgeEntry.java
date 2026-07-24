package aeza.vanilla.generator.biomegrid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BiomeEdgeEntry {
    public final Map<Integer, Integer> key;
    public final Set<Integer> value;

    public BiomeEdgeEntry(Map<Integer, Integer> key, int[] value) {
        this.key = key;
        if (value != null) {
            this.value = new HashSet<>();
            for (int v : value) {
                this.value.add(v);
            }
        } else {
            this.value = null;
        }
    }
}
