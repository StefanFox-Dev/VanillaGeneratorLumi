package aeza.vanilla.generator.structure;

import aeza.vanilla.VanillaGeneratorLumi;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class StructureManager {

    public static class StructureLocation {
        public final String world;
        public final String category;
        public final int x;
        public final int y;
        public final int z;

        public StructureLocation(String world, String category, int x, int y, int z) {
            this.world = world;
            this.category = category;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static final Map<String, List<NBTStructure>> CATEGORIZED_STRUCTURES = new HashMap<>();
    private static final List<StructureLocation> TRACKED_STRUCTURES = new ArrayList<>();
    private static boolean loaded = false;

    public static synchronized void init() {
        if (loaded) return;
        loaded = true;

        log.info("Initializing embedded NBT structure resources...");
        try {
            File pluginJar = null;
            if (VanillaGeneratorLumi.getInstance() != null) {
                pluginJar = VanillaGeneratorLumi.getInstance().getFile();
            }

            if (pluginJar != null && pluginJar.exists() && pluginJar.getName().endsWith(".jar")) {
                loadFromJar(pluginJar);
            }

            if (CATEGORIZED_STRUCTURES.isEmpty()) {
                URL codeSource = StructureManager.class.getProtectionDomain().getCodeSource().getLocation();
                File codeSourceFile = new File(codeSource.toURI());
                if (codeSourceFile.isFile() && codeSourceFile.getName().endsWith(".jar")) {
                    loadFromJar(codeSourceFile);
                }
            }

            if (CATEGORIZED_STRUCTURES.isEmpty()) {
                File[] searchDirs = new File[] {
                    new File("plugins/structures"),
                    new File("plugins/VanillaGeneratorLumi/structures"),
                    new File("plugins/VanillaGeneratorLumi/src/main/resources/structures"),
                    new File("src/main/resources/structures"),
                    new File("structures")
                };

                for (File dir : searchDirs) {
                    if (dir.exists()) {
                        log.info("Scanning filesystem structures directory: " + dir.getAbsolutePath());
                        scanDirectory(dir, "");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to load embedded structure resources", e);
        }

        log.info("Loaded " + CATEGORIZED_STRUCTURES.size() + " structure categories with " + countTotalStructures() + " total templates from resources.");
    }

    private static boolean isTechnicalAirTemplate(String filename) {
        String f = filename.toLowerCase();
        return f.contains("air_base") || f.contains("jigsaw_test") || f.contains("empty") || f.endsWith("/air.nbt") || f.contains("terminator");
    }

    private static void loadFromJar(File jarFile) {
        log.info("Reading JAR structure entries from: " + jarFile.getAbsolutePath());
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName().replace('\\', '/');
                String lower = name.toLowerCase();

                int idx = lower.indexOf("structures/");
                if (!entry.isDirectory() && idx != -1 && lower.endsWith(".nbt") && !isTechnicalAirTemplate(lower)) {
                    String subPath = name.substring(idx + "structures/".length());
                    int lastSlash = subPath.lastIndexOf('/');
                    String category = lastSlash != -1 ? subPath.substring(0, lastSlash) : "common";

                    try (var stream = jar.getInputStream(entry)) {
                        NBTStructure struct = NBTStructure.loadFromStream(stream);
                        if (struct != null && (!struct.getBlocks().isEmpty() || struct.getSizeX() > 0)) {
                            CATEGORIZED_STRUCTURES.computeIfAbsent(category.toLowerCase(), k -> new ArrayList<>()).add(struct);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error reading JAR entries for structures", e);
        }
    }

    private static void scanDirectory(File dir, String category) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                String subCat = category.isEmpty() ? file.getName() : category + "/" + file.getName();
                scanDirectory(file, subCat);
            } else if (file.getName().endsWith(".nbt") && !isTechnicalAirTemplate(file.getName())) {
                NBTStructure struct = NBTStructure.load(file);
                if (struct != null && (!struct.getBlocks().isEmpty() || struct.getSizeX() > 0)) {
                    String cat = category.isEmpty() ? "common" : category;
                    CATEGORIZED_STRUCTURES.computeIfAbsent(cat.toLowerCase(), k -> new ArrayList<>()).add(struct);
                }
            }
        }
    }

    private static int countTotalStructures() {
        return CATEGORIZED_STRUCTURES.values().stream().mapToInt(List::size).sum();
    }

    public static synchronized void registerGeneratedStructure(String world, String category, int x, int y, int z) {
        TRACKED_STRUCTURES.add(new StructureLocation(world, category, x, y, z));
    }

    public static synchronized StructureLocation findNearestStructure(String world, String categoryFilter, int playerX, int playerZ) {
        StructureLocation nearest = null;
        double minSqDist = Double.MAX_VALUE;

        for (StructureLocation loc : TRACKED_STRUCTURES) {
            if (!loc.world.equalsIgnoreCase(world)) continue;
            if (categoryFilter != null && !categoryFilter.isEmpty() && !categoryFilter.equalsIgnoreCase("auto")) {
                if (!loc.category.toLowerCase().contains(categoryFilter.toLowerCase())) continue;
            }

            double dx = loc.x - playerX;
            double dz = loc.z - playerZ;
            double distSq = dx * dx + dz * dz;

            if (distSq < minSqDist) {
                minSqDist = distSq;
                nearest = loc;
            }
        }

        return nearest;
    }

    public static NBTStructure getRandomStructure(String category, Random random) {
        if (category == null || category.isEmpty()) return null;
        String key = category.toLowerCase();

        List<NBTStructure> aggregated = new ArrayList<>();
        String prefix = key + "/";

        for (Map.Entry<String, List<NBTStructure>> entry : CATEGORIZED_STRUCTURES.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key) || entry.getKey().startsWith(prefix)) {
                for (NBTStructure s : entry.getValue()) {
                    if (s != null && !s.getBlocks().isEmpty()) {
                        aggregated.add(s);
                    }
                }
            }
        }

        if (!aggregated.isEmpty()) {
            return aggregated.get(random.nextInt(aggregated.size()));
        }
        return null;
    }
}
