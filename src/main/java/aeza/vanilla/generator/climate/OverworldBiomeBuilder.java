package aeza.vanilla.generator.climate;

import aeza.vanilla.generator.biomegrid.BiomeIds;

import java.util.ArrayList;
import java.util.List;

public class OverworldBiomeBuilder {

    private final List<Climate.BiomeEntry> entries = new ArrayList<>();

    public OverworldBiomeBuilder() {
        buildBiomeEntries();
    }

    public List<Climate.BiomeEntry> getEntries() {
        return entries;
    }

    private void add(float temp, float hum, float cont, float eros, float depth, float weird, float offset, int biomeId) {
        entries.add(new Climate.BiomeEntry(new Climate.ParameterPoint(temp, hum, cont, eros, depth, weird, offset), biomeId));
    }

    private void buildBiomeEntries() {
        // 1. Mushroom Fields (Deep Ocean, low erosion)
        add(0.0f, 0.0f, -1.05f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.MUSHROOM_ISLAND);

        // 2. Oceans & Deep Oceans (Continentalness < -0.2f)
        // Frozen Oceans
        add(-0.7f, 0.0f, -0.4f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.FROZEN_OCEAN);
        add(-0.7f, 0.0f, -0.7f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.COLD_DEEP_OCEAN);
        // Cold Oceans
        add(-0.3f, 0.0f, -0.4f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.COLD_OCEAN);
        add(-0.3f, 0.0f, -0.7f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.COLD_DEEP_OCEAN);
        // Normal Oceans
        add(0.0f, 0.0f, -0.4f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.OCEAN);
        add(0.0f, 0.0f, -0.7f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.DEEP_OCEAN);
        // Lukewarm Oceans
        add(0.4f, 0.0f, -0.4f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.LUKEWARM_OCEAN);
        add(0.4f, 0.0f, -0.7f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.LUKEWARM_DEEP_OCEAN);
        // Warm Oceans
        add(0.8f, 0.0f, -0.4f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.WARM_OCEAN);
        add(0.8f, 0.0f, -0.7f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.WARM_DEEP_OCEAN);

        // 3. Coast / Beach (Continentalness -0.2f to -0.05f)
        add(-0.6f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.COLD_BEACH);
        add(0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.BEACH);
        add(0.6f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.BEACH);
        add(0.0f, 0.0f, -0.1f, -0.6f, 0.0f, 0.0f, 0.0f, BiomeIds.STONE_BEACH);

        // 4. Mountain Peaks & Slopes (High Continentalness > 0.35f, Low Erosion < -0.3f)
        // Frozen Peaks & Jagged Peaks
        add(-0.7f, 0.0f, 0.5f, -0.6f, 0.0f, 0.0f, 0.0f, BiomeIds.FROZEN_PEAKS);
        add(-0.3f, 0.0f, 0.5f, -0.6f, 0.0f, 0.0f, 0.0f, BiomeIds.JAGGED_PEAKS);
        add(0.5f, 0.0f, 0.5f, -0.6f, 0.0f, 0.0f, 0.0f, BiomeIds.STONY_PEAKS);

        // Snowy Slopes & Groves
        add(-0.6f, 0.0f, 0.4f, -0.3f, 0.0f, 0.0f, 0.0f, BiomeIds.SNOWY_SLOPES);
        add(-0.2f, 0.2f, 0.4f, -0.3f, 0.0f, 0.0f, 0.0f, BiomeIds.MOUNTAIN_GROVE);

        // Cherry Grove
        add(0.1f, 0.3f, 0.4f, -0.3f, 0.0f, 0.0f, 0.0f, BiomeIds.CHERRY_GROVE);

        // Mountain Meadow
        add(0.1f, -0.1f, 0.4f, -0.2f, 0.0f, 0.0f, 0.0f, BiomeIds.MOUNTAIN_MEADOW);

        // Windswept Hills
        add(0.0f, -0.3f, 0.4f, -0.4f, 0.0f, 0.0f, 0.0f, BiomeIds.WINDSWEPT_HILLS);

        // 5. Inland Biomes (Continentalness > 0.0f)
        // Cold Biomes
        add(-0.7f, -0.4f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.ICE_PLAINS);
        add(-0.7f, 0.4f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.COLD_TAIGA);
        add(-0.7f, 0.0f, 0.2f, 0.6f, 0.0f, 0.0f, 0.0f, BiomeIds.ICE_PLAINS_SPIKES);

        // Taiga & Old Growth Taiga
        add(-0.3f, 0.2f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.TAIGA);
        add(-0.3f, 0.6f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.MEGA_TAIGA);

        // Plains & Forests
        add(0.0f, -0.3f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.PLAINS);
        add(0.0f, -0.3f, 0.2f, 0.6f, 0.0f, 0.0f, 0.0f, BiomeIds.SUNFLOWER_PLAINS);
        add(0.0f, 0.2f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.FOREST);
        add(0.0f, 0.2f, 0.2f, 0.6f, 0.0f, 0.0f, 0.0f, BiomeIds.FLOWER_FOREST);
        add(0.1f, 0.1f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.BIRCH_FOREST);

        // Dark Forest (Roofed Forest)
        add(0.1f, 0.5f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.ROOFED_FOREST);

        // Swamps
        add(0.2f, 0.6f, 0.1f, 0.3f, 0.0f, 0.0f, 0.0f, BiomeIds.SWAMPLAND);

        // Jungles
        add(0.6f, 0.5f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.JUNGLE);
        add(0.6f, 0.5f, 0.2f, -0.3f, 0.0f, 0.0f, 0.0f, BiomeIds.JUNGLE_HILLS);

        // Savannas
        add(0.6f, -0.2f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.SAVANNA);
        add(0.6f, -0.2f, 0.3f, -0.2f, 0.0f, 0.0f, 0.0f, BiomeIds.SAVANNA_PLATEAU);
        add(0.6f, -0.2f, 0.3f, -0.5f, 0.0f, 0.0f, 0.0f, BiomeIds.WINDSWEPT_SAVANNA);

        // Deserts
        add(0.8f, -0.6f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.DESERT);
        add(0.8f, -0.6f, 0.2f, -0.3f, 0.0f, 0.0f, 0.0f, BiomeIds.DESERT_HILLS);

        // Badlands (Mesa / Eroded Badlands / Wooded Badlands)
        add(0.8f, -0.2f, 0.3f, 0.0f, 0.0f, 0.0f, 0.0f, BiomeIds.MESA);
        add(0.8f, -0.2f, 0.3f, -0.5f, 0.0f, 0.0f, 0.0f, BiomeIds.MESA_BRYCE);
        add(0.8f, 0.1f, 0.3f, -0.2f, 0.0f, 0.0f, 0.0f, BiomeIds.MESA_PLATEAU);

        // 6. Underground Cave Biomes (Depth < -0.3f)
        add(0.0f, 0.6f, 0.0f, 0.0f, -0.6f, 0.0f, 0.0f, BiomeIds.LUSH_CAVES);
        add(0.0f, -0.5f, 0.0f, 0.0f, -0.6f, 0.0f, 0.0f, BiomeIds.DRIPSTONE_CAVES);
        add(0.0f, 0.0f, 0.0f, 0.0f, -0.9f, 0.0f, 0.0f, BiomeIds.DEEP_DARK);
    }
}
