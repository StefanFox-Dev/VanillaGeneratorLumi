package aeza.vanilla.generator.biome;

import cn.nukkit.level.biome.type.GrassyBiome;

public class CherryGroveBiome extends GrassyBiome {

    public static final int CHERRY_GROVE_ID = 182;

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
        return CHERRY_GROVE_ID;
    }
}
