package aeza.vanilla.generator;

import aeza.vanilla.generator.biome.CherryGroveBiome;
import aeza.vanilla.generator.biomegrid.BiomeIds;
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

    public Biome pickBiome(int x, int z) {
        float noiseOcean = ocean.noise2D(x, z, true);
        float noiseRiver = river.noise2D(x, z, true);
        float temp = this.temperature.noise2D(x, z, true);
        float rain = this.rainfall.noise2D(x, z, true);

        // 1. Ocean Biomes (Warm, Lukewarm, Normal, Cold, Frozen)
        if (noiseOcean < -0.20f) {
            boolean isDeep = noiseOcean < -0.40f;

            if (temp > 0.45f) {
                return Biome.getBiome(isDeep ? BiomeIds.WARM_DEEP_OCEAN : BiomeIds.WARM_OCEAN);
            } else if (temp > 0.15f) {
                return Biome.getBiome(isDeep ? BiomeIds.LUKEWARM_DEEP_OCEAN : BiomeIds.LUKEWARM_OCEAN);
            } else if (temp < -0.40f) {
                return Biome.getBiome(isDeep ? BiomeIds.COLD_DEEP_OCEAN : BiomeIds.FROZEN_OCEAN);
            } else if (temp < -0.15f) {
                return Biome.getBiome(isDeep ? BiomeIds.COLD_DEEP_OCEAN : BiomeIds.COLD_OCEAN);
            }
            return Biome.getBiome(isDeep ? BiomeIds.DEEP_OCEAN : BiomeIds.OCEAN);
        }

        // 2. River Systems
        if (noiseRiver > -0.04f && noiseRiver < 0.04f) {
            if (temp < -0.4f) {
                return EnumBiome.FROZEN_RIVER.biome;
            }
            return EnumBiome.RIVER.biome;
        }

        float hillNoise = hills.noise2D(x, z, true);

        // 3. Mountain Biomes (Peaks, Slopes, Groves, Meadows)
        if (hillNoise > 0.58f) { // High Peaks
            if (temp < -0.4f) {
                return Biome.getBiome(BiomeIds.FROZEN_PEAKS);
            } else if (temp < 0.1f) {
                return Biome.getBiome(BiomeIds.JAGGED_PEAKS);
            } else if (temp > 0.45f) {
                return Biome.getBiome(BiomeIds.STONY_PEAKS);
            }
            return EnumBiome.ICE_MOUNTAINS.biome;
        } else if (hillNoise > 0.42f) { // Mountain Slopes & Groves
            if (temp > -0.2f && temp < 0.45f && rain > 0.1f) {
                return CHERRY_GROVE_BIOME;
            } else if (temp < -0.3f) {
                return Biome.getBiome(BiomeIds.SNOWY_SLOPES);
            } else if (temp < 0.0f) {
                return Biome.getBiome(BiomeIds.MOUNTAIN_GROVE);
            }
            return EnumBiome.EXTREME_HILLS.biome;
        } else if (hillNoise > 0.32f) { // Foothills & Meadows
            if (temp > -0.1f && temp < 0.4f) {
                return Biome.getBiome(BiomeIds.MOUNTAIN_MEADOW);
            }
            return Biome.getBiome(BiomeIds.WINDSWEPT_HILLS);
        }

        // 4. Warm Biomes (Desert, Savanna, Badlands, Wooded Badlands, Eroded Badlands)
        if (temp > 0.55f) {
            if (rain < -0.45f) {
                return EnumBiome.DESERT.biome;
            } else if (rain < -0.25f) {
                return Biome.getBiome(BiomeIds.MESA_BRYCE); // Eroded Badlands
            } else if (rain < -0.05f) {
                return Biome.getBiome(BiomeIds.MESA); // Badlands
            } else if (rain < 0.20f) {
                if (hillNoise > 0.20f) {
                    return Biome.getBiome(BiomeIds.WINDSWEPT_SAVANNA);
                }
                return Biome.getBiome(BiomeIds.SAVANNA_PLATEAU);
            } else if (rain < 0.45f) {
                return Biome.getBiome(BiomeIds.MESA_PLATEAU); // Wooded Badlands
            }
            return EnumBiome.JUNGLE.biome;
        }

        // 5. Temperate & Cold Biomes
        if (temp < -0.4f) {
            if (rain < 0f) {
                return EnumBiome.ICE_PLAINS.biome;
            }
            return EnumBiome.COLD_TAIGA.biome;
        } else if (temp < 0.1f) {
            if (rain < -0.2f) {
                return EnumBiome.PLAINS.biome;
            } else if (rain < 0.3f) {
                return EnumBiome.TAIGA.biome;
            }
            return EnumBiome.FOREST.biome;
        } else {
            if (rain < -0.3f) {
                return EnumBiome.DESERT.biome;
            } else if (rain < 0.15f) {
                return EnumBiome.PLAINS.biome;
            } else if (rain < 0.45f) {
                return EnumBiome.FOREST.biome;
            }
            return EnumBiome.SWAMP.biome;
        }
    }
}
