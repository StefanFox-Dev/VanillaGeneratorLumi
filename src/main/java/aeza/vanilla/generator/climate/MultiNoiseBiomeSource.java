package aeza.vanilla.generator.climate;

import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.NukkitRandom;

import java.util.List;

public class MultiNoiseBiomeSource {

    private final SimplexF temperatureNoise;
    private final SimplexF humidityNoise;
    private final SimplexF continentalnessNoise;
    private final SimplexF erosionNoise;
    private final SimplexF weirdnessNoise;

    private final List<Climate.BiomeEntry> entries;

    public MultiNoiseBiomeSource(long seed) {
        NukkitRandom rand = new NukkitRandom(seed);
        this.temperatureNoise = new SimplexF(rand, 2F, 1F / 8F, 1F / 768f);
        this.humidityNoise = new SimplexF(rand, 2F, 1F / 8F, 1F / 768f);
        this.continentalnessNoise = new SimplexF(rand, 2F, 1F / 8F, 1F / 1024f);
        this.erosionNoise = new SimplexF(rand, 2F, 1F / 8F, 1F / 768f);
        this.weirdnessNoise = new SimplexF(rand, 2F, 1F / 8F, 1F / 512f);

        this.entries = new OverworldBiomeBuilder().getEntries();
    }

    public float getTemperature(int x, int z) {
        return this.temperatureNoise.noise2D(x, z, true);
    }

    public float getHumidity(int x, int z) {
        return this.humidityNoise.noise2D(x, z, true);
    }

    public float getContinentalness(int x, int z) {
        return this.continentalnessNoise.noise2D(x, z, true);
    }

    public float getErosion(int x, int z) {
        return this.erosionNoise.noise2D(x, z, true);
    }

    public float getWeirdness(int x, int z) {
        return this.weirdnessNoise.noise2D(x, z, true);
    }

    public int getBiomeId(int x, int y, int z) {
        float temp = getTemperature(x, z);
        float hum = getHumidity(x, z);
        float cont = getContinentalness(x, z);
        float eros = getErosion(x, z);
        float weird = getWeirdness(x, z);

        // Depth is negative underground (below Y = 0)
        float depth = y < 0 ? (float) y / 64.0f : 0.0f;

        float bestFitness = Float.MAX_VALUE;
        int bestBiome = 1; // Default Plains

        for (Climate.BiomeEntry entry : entries) {
            float fitness = entry.point().fitness(temp, hum, cont, eros, depth, weird);
            if (fitness < bestFitness) {
                bestFitness = fitness;
                bestBiome = entry.biomeId();
            }
        }

        return bestBiome;
    }

    public Biome getBiome(int x, int y, int z) {
        int id = getBiomeId(x, y, z);
        Biome b = Biome.getBiome(id);
        return b != null ? b : Biome.getBiome(1);
    }
}
