package aeza.vanilla.generator.biomegrid;

public class MapLayerPair {
    public final MapLayer highResolution;
    public final MapLayer lowResolution;

    public MapLayerPair(MapLayer highResolution, MapLayer lowResolution) {
        this.highResolution = highResolution;
        this.lowResolution = lowResolution;
    }
}
