package aeza.vanilla.generator.biome;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import cn.nukkit.level.biome.type.GrassyBiome;

public class CherryGroveBiome extends GrassyBiome {

    public CherryGroveBiome() {
        super();
        this.setBaseHeight(0.8f);
        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return "Cherry Grove";
    }

    @Override
    public int getId() {
        return BiomeIds.CHERRY_GROVE;
    }

    @Override
    public boolean canRain() {
        return true;
    }
}
