package aeza.vanilla.generator;

import aeza.vanilla.generator.cave.CaveGenerator;
import aeza.vanilla.generator.populator.BambooPopulator;
import aeza.vanilla.generator.populator.CherryGrovePopulator;
import aeza.vanilla.generator.populator.FeatureRulesPopulator;
import aeza.vanilla.generator.populator.OceanPopulator;
import aeza.vanilla.generator.populator.StructurePopulator;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.NukkitRandom;

import java.util.Map;
import java.util.SplittableRandom;

public class OverworldGenerator extends Normal {

    private CustomBiomeSelector customSelector;
    private CaveGenerator caveGenerator;
    private StructurePopulator structurePopulator;
    private OceanPopulator oceanPopulator;
    private FeatureRulesPopulator featureRulesPopulator;
    private CherryGrovePopulator cherryGrovePopulator;
    private BambooPopulator bambooPopulator;

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
        this.customSelector = new CustomBiomeSelector(level.getSeed());
        this.caveGenerator = new CaveGenerator(level.getSeed());
        this.structurePopulator = new StructurePopulator();
        this.oceanPopulator = new OceanPopulator();
        this.featureRulesPopulator = new FeatureRulesPopulator();
        this.cherryGrovePopulator = new CherryGrovePopulator();
        this.bambooPopulator = new BambooPopulator();
    }

    @Override
    public Biome pickBiome(int x, int z) {
        if (this.customSelector != null) {
            return this.customSelector.pickBiome(x, z);
        }
        return super.pickBiome(x, z);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        super.generateChunk(chunkX, chunkZ);

        var chunk = getChunkManager().getChunk(chunkX, chunkZ);
        if (chunk != null) {
            if (this.customSelector != null) {
                int baseX = chunkX << 4;
                int baseZ = chunkZ << 4;
                for (int x = 0; x < 16; x++) {
                    int worldX = baseX + x;
                    for (int z = 0; z < 16; z++) {
                        int worldZ = baseZ + z;
                        Biome picked = this.customSelector.pickBiome(worldX, worldZ);
                        if (picked != null) {
                            chunk.setBiomeId(x, z, picked.getId());
                        }
                    }
                }
            }

            if (this.caveGenerator != null) {
                this.caveGenerator.carveDirectly(chunk, chunkX, chunkZ);
            }
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        super.populateChunk(chunkX, chunkZ);

        var chunk = getChunkManager().getChunk(chunkX, chunkZ);
        if (chunk != null) {
            long chunkSeed = getChunkManager().getSeed() ^ (chunkX * 341873128712L + chunkZ * 132897987541L);
            SplittableRandom random = new SplittableRandom(chunkSeed);

            this.cherryGrovePopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.bambooPopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.oceanPopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.featureRulesPopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
            this.structurePopulator.populate(getChunkManager(), random, chunkX, chunkZ, chunk);
        }
    }
}
