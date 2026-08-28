package aeza.vanilla.generator;

import aeza.vanilla.generator.cave.CaveGenerator;
import aeza.vanilla.generator.climate.MultiNoiseBiomeSource;
import aeza.vanilla.generator.density.OverworldNoiseRouter;
import aeza.vanilla.generator.populator.BambooPopulator;
import aeza.vanilla.generator.populator.CaveBiomePopulator;
import aeza.vanilla.generator.populator.CherryGrovePopulator;
import aeza.vanilla.generator.populator.FeatureRulesPopulator;
import aeza.vanilla.generator.populator.MountainBiomePopulator;
import aeza.vanilla.generator.populator.OceanPopulator;
import aeza.vanilla.generator.populator.OrePopulator;
import aeza.vanilla.generator.populator.StructurePopulator;
import aeza.vanilla.generator.populator.WarmBiomePopulator;
import aeza.vanilla.generator.surface.SurfaceSystem;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.NukkitRandom;

import java.util.Map;
import java.util.SplittableRandom;

public class OverworldGenerator extends Normal {

    private MultiNoiseBiomeSource multiNoiseBiomeSource;
    private OverworldNoiseRouter noiseRouter;
    private SurfaceSystem surfaceSystem;
    private CaveGenerator caveGenerator;

    // Feature and Biome Populators
    private StructurePopulator structurePopulator;
    private OceanPopulator oceanPopulator;
    private FeatureRulesPopulator featureRulesPopulator;
    private CherryGrovePopulator cherryGrovePopulator;
    private BambooPopulator bambooPopulator;
    private MountainBiomePopulator mountainBiomePopulator;
    private WarmBiomePopulator warmBiomePopulator;
    private CaveBiomePopulator caveBiomePopulator;
    private OrePopulator orePopulator;

    public OverworldGenerator() {
        super();
    }

    public OverworldGenerator(Map<String, Object> options) {
        super(options);
    }

    @Override
    public String getName() {
        return "normal";
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        super.init(level, random);
        long seed = level.getSeed();

        this.multiNoiseBiomeSource = new MultiNoiseBiomeSource(seed);
        this.noiseRouter = new OverworldNoiseRouter(seed, this.multiNoiseBiomeSource);
        this.surfaceSystem = new SurfaceSystem(this.multiNoiseBiomeSource);
        this.caveGenerator = new CaveGenerator(seed);

        this.structurePopulator = new StructurePopulator();
        this.oceanPopulator = new OceanPopulator();
        this.featureRulesPopulator = new FeatureRulesPopulator();
        this.cherryGrovePopulator = new CherryGrovePopulator();
        this.bambooPopulator = new BambooPopulator();
        this.mountainBiomePopulator = new MountainBiomePopulator();
        this.warmBiomePopulator = new WarmBiomePopulator();
        this.caveBiomePopulator = new CaveBiomePopulator();
        this.orePopulator = new OrePopulator();
    }

    @Override
    public Biome pickBiome(int x, int z) {
        if (this.multiNoiseBiomeSource != null) {
            return this.multiNoiseBiomeSource.getBiome(x, 64, z);
        }
        return super.pickBiome(x, z);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        var chunk = getChunkManager().getChunk(chunkX, chunkZ);
        if (chunk == null) return;

        long chunkSeed = getChunkManager().getSeed() ^ (chunkX * 341873128712L + chunkZ * 132897987541L);
        SplittableRandom random = new SplittableRandom(chunkSeed);

        // 1. Assign Multi-Noise 1.18+ Biomes to all 16x16 columns
        if (this.multiNoiseBiomeSource != null) {
            int baseX = chunkX << 4;
            int baseZ = chunkZ << 4;
            for (int x = 0; x < 16; x++) {
                int worldX = baseX + x;
                for (int z = 0; z < 16; z++) {
                    int worldZ = baseZ + z;
                    int biomeId = this.multiNoiseBiomeSource.getBiomeId(worldX, 64, worldZ);
                    chunk.setBiomeId(x, z, biomeId);
                }
            }
        }

        // 2. 3D Noise Density Terrain Generation (Heightmap, Slopes, Bedrock, Deepslate)
        if (this.noiseRouter != null) {
            this.noiseRouter.generateTerrain(chunk, random, chunkX, chunkZ);
        }

        // 3. 3D Cave Carvers & Caverns (Caves down to Y = -59, Lava Lakes)
        if (this.caveGenerator != null) {
            this.caveGenerator.carveDirectly(chunk, chunkX, chunkZ);
        }

        // 4. Surface System (Terracotta Banding, Red Sand, Calcite, Snow, Sandstone)
        if (this.surfaceSystem != null) {
            this.surfaceSystem.applySurface(chunk, random, chunkX, chunkZ);
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        var chunk = getChunkManager().getChunk(chunkX, chunkZ);
        if (chunk != null) {
            long chunkSeed = getChunkManager().getSeed() ^ (chunkX * 341873128712L + chunkZ * 132897987541L);
            SplittableRandom random = new SplittableRandom(chunkSeed);

            // Populators
            this.orePopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.mountainBiomePopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.warmBiomePopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.caveBiomePopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.cherryGrovePopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.bambooPopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.oceanPopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.featureRulesPopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.structurePopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
        }
    }
}
