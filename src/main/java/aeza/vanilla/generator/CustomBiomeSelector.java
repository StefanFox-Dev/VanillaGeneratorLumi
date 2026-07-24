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

    public CustomBiomeSelector(NukkitRandom random) {
        this.temperature = new SimplexF(random, 2F, 1F / 8F, 1F / 2048f);
        this.rainfall = new SimplexF(random, 2F, 1F / 8F, 1F / 2048f);
        this.river = new SimplexF(random, 6f, 2 / 4f, 1 / 1024f);
        this.ocean = new SimplexF(random, 6f, 2 / 4f, 1 / 2048f);
        this.hills = new SimplexF(random, 2f, 2 / 4f, 1 / 2048f);
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
        if (Math.abs(noiseRiver) < 0.035f) {
            return temperature < -0.4f ? EnumBiome.FROZEN_RIVER.biome : EnumBiome.RIVER.biome;
        }

        float hills = this.hills.noise2D(x, z, true);

        // Beaches
        if (noiseOcean < -0.15f) {
            return temperature < -0.379f ? EnumBiome.COLD_BEACH.biome : EnumBiome.BEACH.biome;
        }

        // 🏜 BADLANDS / MESA: Dry hot land, strictly above sea level
        if (temperature > 0.4f && rainfall < -0.15f && noiseOcean >= -0.10f) {
            return hills > 0.1f ? EnumBiome.MESA_PLATEAU.biome : EnumBiome.MESA.biome;
        }

        // 🌸 CHERRY GROVE: High mountain slopes & plateaus in temperate climate (Vanilla rarity)
        if (hills > 0.38f && temperature >= -0.2f && temperature < 0.35f && rainfall > 0.0f) {
            return CHERRY_GROVE_BIOME;
        }

        // HIGH ELEVATION: MOUNTAINS & PEAKS (hills > 0.25f)
        if (hills > 0.25f) {
            if (temperature < -0.379f) {
                return EnumBiome.ICE_MOUNTAINS.biome;
            }
            if (rainfall > 0.2f) {
                return EnumBiome.EXTREME_HILLS_PLUS.biome;
            }
            return EnumBiome.EXTREME_HILLS.biome;
        }

        // MODERATE ELEVATION: WOODED HILLS (hills > 0.05f)
        if (hills > 0.05f) {
            if (temperature < -0.379f) {
                return EnumBiome.COLD_TAIGA_HILLS.biome;
            }
            if (rainfall > 0.3f) {
                return EnumBiome.FOREST_HILLS.biome;
            }
            return EnumBiome.TAIGA_HILLS.biome;
        }

        // LOW ELEVATION: PLAINS, FORESTS, DESERTS, SAVANNAS & TAIGAS
        if (temperature < -0.379f) {
            return EnumBiome.ICE_PLAINS.biome;
        }

        if (temperature < 0.2f) {
            if (rainfall < -0.1f) {
                return EnumBiome.PLAINS.biome;
            }
            if (rainfall < 0.3f) {
                return EnumBiome.SUNFLOWER_PLAINS.biome;
            }
            return EnumBiome.TAIGA.biome;
        }

        if (temperature < 0.5f) {
            if (rainfall < -0.2f) {
                return EnumBiome.PLAINS.biome;
            }
            if (rainfall < 0.1f) {
                return EnumBiome.FOREST.biome;
            }
            if (rainfall < 0.4f) {
                return EnumBiome.BIRCH_FOREST.biome;
            }
            return EnumBiome.SWAMP.biome;
        }

        // Hot climate
        if (rainfall < 0f) {
            return EnumBiome.DESERT.biome;
        }
        if (rainfall > 0.3f) {
            return EnumBiome.JUNGLE.biome;
        }
        return EnumBiome.SAVANNA.biome;
    }
}
