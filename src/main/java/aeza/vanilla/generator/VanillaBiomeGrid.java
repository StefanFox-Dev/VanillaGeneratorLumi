package aeza.vanilla.generator;

public class VanillaBiomeGrid {
    public final int[] biomes = new int[256];

    public int getBiome(int x, int z) {
        return biomes[(x & 0x0f) | ((z & 0x0f) << 4)];
    }

    public void setBiome(int x, int z, int biomeId) {
        biomes[(x & 0x0f) | ((z & 0x0f) << 4)] = biomeId;
    }
}
