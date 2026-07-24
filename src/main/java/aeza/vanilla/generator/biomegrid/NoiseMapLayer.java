package aeza.vanilla.generator.biomegrid;

public class NoiseMapLayer extends MapLayer {

    public NoiseMapLayer(long seed) {
        super(seed);
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; ++i) {
            for (int j = 0; j < sizeX; ++j) {
                setCoordsSeed(x + j, z + i);
                values[j + i * sizeX] = nextInt(10) == 0 ? 1 : 0;
            }
        }
        return values;
    }
}
