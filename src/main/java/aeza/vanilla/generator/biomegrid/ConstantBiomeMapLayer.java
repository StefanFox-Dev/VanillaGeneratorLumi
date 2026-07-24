package aeza.vanilla.generator.biomegrid;

import java.util.Arrays;

public class ConstantBiomeMapLayer extends MapLayer {
    private final int biomeId;

    public ConstantBiomeMapLayer(long seed, int biomeId) {
        super(seed);
        this.biomeId = biomeId;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = new int[sizeX * sizeZ];
        Arrays.fill(values, biomeId);
        return values;
    }
}
