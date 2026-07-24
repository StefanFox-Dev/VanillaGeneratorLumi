package aeza.vanilla.generator.biomegrid;

import aeza.vanilla.generator.Environment;

import java.util.SplittableRandom;

public abstract class MapLayer {
    protected SplittableRandom random;
    protected final long seed;

    public MapLayer(long seed) {
        this.seed = seed;
        this.random = new SplittableRandom(seed);
    }

    public void setCoordsSeed(int x, int z) {
        this.random = new SplittableRandom(x * 341873128712L + z * 132897987541L ^ seed);
    }

    public int nextInt(int max) {
        return random.nextInt(max);
    }

    public abstract int[] generateValues(int x, int z, int sizeX, int sizeZ);

    public static MapLayerPair initialize(long seed, int environment, String worldType) {
        if (environment == Environment.OVERWORLD && WorldType.FLAT.equals(worldType)) {
            return new MapLayerPair(new ConstantBiomeMapLayer(seed, BiomeIds.PLAINS), null);
        }
        if (environment == Environment.NETHER) {
            return new MapLayerPair(new ConstantBiomeMapLayer(seed, BiomeIds.HELL), null);
        }
        if (environment == Environment.THE_END) {
            return new MapLayerPair(new ConstantBiomeMapLayer(seed, BiomeIds.SKY), null);
        }

        int zoom = 2;
        if (WorldType.LARGE_BIOMES.equals(worldType)) {
            zoom = 4;
        }

        MapLayer layer = new NoiseMapLayer(seed);
        layer = new WhittakerMapLayer(seed + 1, layer, WhittakerMapLayer.WARM_WET);
        layer = new WhittakerMapLayer(seed + 1, layer, WhittakerMapLayer.COLD_DRY);
        layer = new WhittakerMapLayer(seed + 2, layer, WhittakerMapLayer.LARGER_BIOMES);

        for (int i = 0; i < 2; ++i) {
            layer = new ZoomMapLayer(seed + 100 + i, layer, ZoomMapLayer.BLURRY);
        }

        for (int i = 0; i < 2; ++i) {
            layer = new ErosionMapLayer(seed + 3 + i, layer);
        }

        layer = new DeepOceanMapLayer(seed + 4, layer);

        MapLayer layerMountains = new BiomeVariationMapLayer(seed + 200, layer, null);
        for (int i = 0; i < 2; ++i) {
            layerMountains = new ZoomMapLayer(seed + 200 + i, layerMountains, ZoomMapLayer.NORMAL);
        }

        layer = new BiomeMapLayer(seed + 5, layer);
        for (int i = 0; i < 2; ++i) {
            layer = new ZoomMapLayer(seed + 200 + i, layer, ZoomMapLayer.NORMAL);
        }

        layer = new BiomeEdgeMapLayer(seed + 200, layer);
        layer = new BiomeVariationMapLayer(seed + 200, layer, layerMountains);
        layer = new RarePlainsMapLayer(seed + 201, layer);
        layer = new ZoomMapLayer(seed + 300, layer, ZoomMapLayer.NORMAL);
        layer = new ErosionMapLayer(seed + 6, layer);
        layer = new ZoomMapLayer(seed + 400, layer, ZoomMapLayer.NORMAL);
        layer = new BiomeThinEdgeMapLayer(seed + 400, layer);
        layer = new ShoreMapLayer(seed + 7, layer);

        for (int i = 0; i < zoom; ++i) {
            layer = new ZoomMapLayer(seed + 500 + i, layer, ZoomMapLayer.NORMAL);
        }

        MapLayer layerRiver = layerMountains;
        layerRiver = new ZoomMapLayer(seed + 300, layerRiver, ZoomMapLayer.NORMAL);
        layerRiver = new ZoomMapLayer(seed + 400, layerRiver, ZoomMapLayer.NORMAL);
        for (int i = 0; i < zoom; ++i) {
            layerRiver = new ZoomMapLayer(seed + 500 + i, layerRiver, ZoomMapLayer.NORMAL);
        }
        layerRiver = new RiverMapLayer(seed + 10, layerRiver, null);
        layer = new RiverMapLayer(seed + 1000, layerRiver, layer);

        MapLayer layerLowerRes = layer;
        for (int i = 0; i < 2; ++i) {
            layer = new ZoomMapLayer(seed + 2000 + i, layer, ZoomMapLayer.NORMAL);
        }

        layer = new SmoothMapLayer(seed + 1001, layer);

        return new MapLayerPair(layer, layerLowerRes);
    }
}
