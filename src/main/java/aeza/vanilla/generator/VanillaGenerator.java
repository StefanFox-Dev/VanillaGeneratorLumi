package aeza.vanilla.generator;

import aeza.vanilla.generator.biomegrid.MapLayer;
import aeza.vanilla.generator.biomegrid.MapLayerPair;
import aeza.vanilla.generator.biomegrid.WorldType;
import aeza.vanilla.generator.populator.Populator;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public abstract class VanillaGenerator extends Generator {

    protected ChunkManager level;
    protected NukkitRandom random;
    protected SplittableRandom splittableRandom;
    protected MapLayerPair biomeGrid;
    protected final List<Populator> populators = new ArrayList<>();
    protected long seed;

    public VanillaGenerator(Map<String, Object> options) {
        this(0);
    }

    public VanillaGenerator(long seed) {
        this.seed = seed;
    }

    public VanillaGenerator() {
        this(Collections.emptyMap());
    }

    @Override
    public int getId() {
        return Generator.TYPE_INFINITE;
    }

    @Override
    public String getName() {
        return "vanilla";
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0, 64, 0);
    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.random = random;
        this.seed = random.getSeed();
        this.splittableRandom = new SplittableRandom(this.seed);
        this.biomeGrid = MapLayer.initialize(this.seed, Environment.OVERWORLD, WorldType.NORMAL);
    }

    protected void addPopulators(Populator... populatorList) {
        Collections.addAll(this.populators, populatorList);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        VanillaBiomeGrid biomes = new VanillaBiomeGrid();
        int[] biomeValues = this.biomeGrid.highResolution.generateValues(chunkX * 16, chunkZ * 16, 16, 16);
        System.arraycopy(biomeValues, 0, biomes.biomes, 0, 256);

        generateChunkData(chunkX, chunkZ, biomes);
    }

    protected abstract void generateChunkData(int chunkX, int chunkZ, VanillaBiomeGrid biomes);

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        FullChunk chunk = level.getChunk(chunkX, chunkZ);
        if (chunk == null) return;
        SplittableRandom popRand = new SplittableRandom(seed ^ (chunkX * 341873128712L + chunkZ * 132897987541L));
        for (Populator populator : populators) {
            populator.populate(level, popRand, chunkX, chunkZ, chunk);
        }
    }

    @Override
    public void populateStructure(int chunkX, int chunkZ) {
    }
}
