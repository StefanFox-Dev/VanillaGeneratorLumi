package aeza.vanilla;

import aeza.vanilla.command.StructureCommand;
import aeza.vanilla.command.StructureSetCommand;
import aeza.vanilla.generator.OverworldGenerator;
import aeza.vanilla.generator.populator.EndStructurePopulator;
import aeza.vanilla.generator.populator.NetherStructurePopulator;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkPopulateEvent;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.PopChunkManager;
import cn.nukkit.math.NukkitRandom;
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
        overrideGenerators();
        injectIntoAllLevels();

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getCommandMap().register("structure", new StructureCommand("structure"));
        getServer().getCommandMap().register("stset", new StructureSetCommand("stset"));
        getLogger().info("VanillaGeneratorLumi plugin successfully enabled, registered on EventBus, /structure and /stset commands added!");
    }

    @Override
    public void onDisable() {
        getLogger().info("VanillaGeneratorLumi plugin disabled!");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLevelInit(LevelInitEvent event) {
        Level level = event.getLevel();
        if (level != null) {
            overrideGenerators();
            injectGenerator(level);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLevelLoad(LevelLoadEvent event) {
        Level level = event.getLevel();
        if (level != null) {
            overrideGenerators();
            injectGenerator(level);
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

    public void injectIntoAllLevels() {
        try {
            for (Level level : getServer().getLevels().values()) {
                injectGenerator(level);
            }
        } catch (Exception e) {
            getLogger().error("Error injecting generator into loaded levels", e);
        }
    }

    public void injectGenerator(Level level) {
        if (level == null) return;
        if (level.getDimension() != Level.DIMENSION_OVERWORLD) return;

        try {
            Class<? extends Generator> targetGenerator = OverworldGenerator.class;

            Field generatorClassField = Level.class.getDeclaredField("generatorClass");
            generatorClassField.setAccessible(true);
            generatorClassField.set(level, targetGenerator);

            Field generatorsField = Level.class.getDeclaredField("generators");
            generatorsField.setAccessible(true);

            ThreadLocal<Generator> customThreadLocal = new ThreadLocal<>() {
                @Override
                public Generator initialValue() {
                    try {
                        Generator generator = targetGenerator.getConstructor(Map.class).newInstance(level.requireProvider().getGeneratorOptions());
                        NukkitRandom rand = new NukkitRandom(level.getSeed());
                        if (Server.getInstance().isPrimaryThread()) {
                            generator.init(level, rand);
                        }
                        generator.init(new PopChunkManager(level.getSeed(), level::getDimensionData), rand);
                        return generator;
                    } catch (Throwable t) {
                        Server.getInstance().getLogger().logException(t);
                        return null;
                    }
                }
            };

            generatorsField.set(level, customThreadLocal);

            // Pre-initialize on primary thread
            Generator gen = customThreadLocal.get();
            if (gen != null) {
                getLogger().info("Successfully injected OverworldGenerator into level: " + level.getName());
            }
        } catch (Exception e) {
            getLogger().error("Failed to inject OverworldGenerator into level: " + level.getName(), e);
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
