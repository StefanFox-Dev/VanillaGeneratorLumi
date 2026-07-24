package aeza.vanilla;

import aeza.vanilla.command.StructureCommand;
import aeza.vanilla.command.StructureSetCommand;
import aeza.vanilla.generator.OverworldGenerator;
import aeza.vanilla.generator.populator.EndStructurePopulator;
import aeza.vanilla.generator.populator.NetherStructurePopulator;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkPopulateEvent;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.SplittableRandom;

@Slf4j
public class VanillaGeneratorLumi extends PluginBase implements Listener {

    private static VanillaGeneratorLumi instance;

    private final NetherStructurePopulator netherStructurePopulator = new NetherStructurePopulator();
    private final EndStructurePopulator endStructurePopulator = new EndStructurePopulator();

    public static VanillaGeneratorLumi getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        try {
            overrideGenerators();
            StructureManager.init();
            getLogger().info("VanillaGeneratorLumi plugin successfully loaded!");
        } catch (Exception e) {
            getLogger().error("Failed to register VanillaGeneratorLumi world generators", e);
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getCommandMap().register("structure", new StructureCommand("structure"));
        getServer().getCommandMap().register("stset", new StructureSetCommand("stset"));
        getLogger().info("VanillaGeneratorLumi plugin successfully enabled, registered on EventBus, /structure and /stset commands added!");
    }

    @Override
    public void onDisable() {
        getLogger().info("VanillaGeneratorLumi plugin disabled!");
    }

    @EventHandler
    public void onLevelInit(LevelInitEvent event) {
        Level level = event.getLevel();
        if (level != null) {
            overrideGenerators();
        }
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        Level level = event.getLevel();
        if (level == null) return;

        FullChunk chunk = event.getChunk();
        if (chunk == null) return;

        int dimension = level.getDimension();
        SplittableRandom random = new SplittableRandom(level.getSeed() ^ (chunk.getX() * 341873128712L + chunk.getZ() * 132897987541L));

        if (dimension == Level.DIMENSION_NETHER) {
            netherStructurePopulator.populate(level, random, chunk.getX(), chunk.getZ(), chunk);
        } else if (dimension == Level.DIMENSION_THE_END) {
            endStructurePopulator.populate(level, random, chunk.getX(), chunk.getZ(), chunk);
        }
    }

    @SuppressWarnings("unchecked")
    private void overrideGenerators() {
        try {
            Class<? extends Generator> generatorClass = OverworldGenerator.class;

            Field nameListField = Generator.class.getDeclaredField("nameList");
            nameListField.setAccessible(true);
            Map<String, Class<? extends Generator>> nameList = (Map<String, Class<? extends Generator>>) nameListField.get(null);

            nameList.put("normal", generatorClass);
            nameList.put("default", generatorClass);
            nameList.put("infinite", generatorClass);
            nameList.put("overworld", generatorClass);
            nameList.put("vanilla", generatorClass);
            nameList.put("vanilla_overworld", generatorClass);

            Field typeListField = Generator.class.getDeclaredField("typeList");
            typeListField.setAccessible(true);
            Map<Integer, Class<? extends Generator>> typeList = (Map<Integer, Class<? extends Generator>>) typeListField.get(null);

            typeList.put(Generator.TYPE_INFINITE, generatorClass);

            Generator.addGenerator(generatorClass, "normal", Generator.TYPE_INFINITE);
            Generator.addGenerator(generatorClass, "default", Generator.TYPE_INFINITE);
            Generator.addGenerator(generatorClass, "infinite", Generator.TYPE_INFINITE);
            Generator.addGenerator(generatorClass, "overworld", Generator.TYPE_INFINITE);
            Generator.addGenerator(generatorClass, "vanilla", Generator.TYPE_INFINITE);

            getLogger().info("Successfully registered VanillaOverworldGenerator overrides for normal/default/infinite level generators.");
        } catch (Exception e) {
            getLogger().error("Failed to override Generator class mappings via reflection", e);
        }
    }
}
