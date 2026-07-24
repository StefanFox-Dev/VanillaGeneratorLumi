package aeza.vanilla.generator;

import aeza.vanilla.generator.biome.CherryGroveBiome;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.NukkitRandom;

public class CustomBiomeSelector {

    private final SimplexF temperature;
    private final SimplexF rainfall;
    private final SimplexF river;
    private final SimplexF ocean;
    private final SimplexF hills;

    private static final Biome CHERRY_GROVE_BIOME = new CherryGroveBiome();

    public CustomBiomeSelector(long seed) {
        NukkitRandom rand = new NukkitRandom(seed);
        this.temperature = new SimplexF(rand, 2F, 1F / 8F, 1F / 2048f);
        this.rainfall = new SimplexF(rand, 2F, 1F / 8F, 1F / 2048f);
        this.river = new SimplexF(rand, 6f, 2 / 4f, 1 / 1024f);
        this.ocean = new SimplexF(rand, 6f, 2 / 4f, 1 / 2048f);
        this.hills = new SimplexF(rand, 2f, 2 / 4f, 1 / 2048f);
    }

    public CustomBiomeSelector(NukkitRandom random) {
        this(random.getSeed());
    }

    public Biome pickBiome(int x, int z) {
        float noiseOcean = ocean.noise2D(x, z, true);
        float noiseRiver = river.noise2D(x, z, true);
        float temperature = this.temperature.noise2D(x, z, true);
        float rainfall = this.rainfall.noise2D(x, z, true);

        // Oceans
        if (noiseOcean < -0.20f) {
            if (rainfall < 0f) {
                return temperature < -0.4f ? EnumBiome.FROZEN_OCEAN.biome : EnumBiome.OCEAN.biome;
            }
            return EnumBiome.DEEP_OCEAN.biome;
        }

        // Rivers
        if (noiseRiver > -0.05f && noiseRiver < 0.05f) {
            if (temperature < -0.4f) {
                return EnumBiome.FROZEN_RIVER.biome;
            }
            return EnumBiome.RIVER.biome;
        }

        float hillNoise = hills.noise2D(x, z, true);

        // Cherry Grove: Spawns on high mountain slopes & plateaus with vanilla rarity
        if (hillNoise > 0.38f && temperature > -0.2f && temperature < 0.45f && rainfall > 0.1f) {
            return CHERRY_GROVE_BIOME;
        }

        // Mountains / Hills
        if (hillNoise > 0.40f) {
            if (temperature < -0.3f) {
                return EnumBiome.ICE_MOUNTAINS.biome;
            }
            return EnumBiome.EXTREME_HILLS.biome;
        }

        // Biome climate selection
        if (temperature < -0.4f) {
            if (rainfall < 0f) {
                return EnumBiome.ICE_PLAINS.biome;
            }
            return EnumBiome.COLD_TAIGA.biome;
        } else if (temperature < 0.1f) {
            if (rainfall < -0.2f) {
                return EnumBiome.PLAINS.biome;
            } else if (rainfall < 0.3f) {
                return EnumBiome.TAIGA.biome;
            }
            return EnumBiome.FOREST.biome;
        } else if (temperature < 0.6f) {
            if (rainfall < -0.4f) {
                return EnumBiome.DESERT.biome;
            } else if (rainfall < 0.2f) {
                return EnumBiome.PLAINS.biome;
            } else if (rainfall < 0.5f) {
                return EnumBiome.FOREST.biome;
            }
            return EnumBiome.SWAMP.biome;
        } else {
            if (rainfall < -0.2f) {
                return EnumBiome.DESERT.biome;
            } else if (rainfall < 0.2f) {
                return EnumBiome.SAVANNA.biome;
            }
            return EnumBiome.JUNGLE.biome;
        }
    }
}
