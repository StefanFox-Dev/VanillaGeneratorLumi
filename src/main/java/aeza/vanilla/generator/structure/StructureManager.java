package aeza.vanilla.generator.structure;

import cn.nukkit.math.Vector3;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StructureManager {

    public static class StructureLocation {
        public final String category;
        public final int x;
        public final int y;
        public final int z;

        public StructureLocation(String category, int x, int y, int z) {
            this.category = category;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static final Map<String, NBTStructure> LOADED_STRUCTURES = new ConcurrentHashMap<>();
    private static final Map<String, List<NBTStructure>> CATEGORIZED_STRUCTURES = new ConcurrentHashMap<>();
    private static final Map<String, List<StructureLocation>> GENERATED_STRUCTURE_LOCATIONS = new ConcurrentHashMap<>();

    public static void init() {
        if (!LOADED_STRUCTURES.isEmpty()) return;
        loadClasspathStructures();
        loadExternalStructures();
    }

    private static void loadClasspathStructures() {
        List<String> filesToLoad = new ArrayList<>();
        try (InputStream manifestIn = StructureManager.class.getClassLoader().getResourceAsStream("structures_manifest.txt")) {
            if (manifestIn != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(manifestIn, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty()) {
                            filesToLoad.add(line);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to load structures_manifest.txt", e);
        }

        int count = 0;
        for (String relativePath : filesToLoad) {
            String resPath = "structures/" + relativePath;
            try (InputStream in = StructureManager.class.getClassLoader().getResourceAsStream(resPath)) {
                if (in != null) {
                    NBTStructure s = NBTStructure.loadFromStream(in);
                    if (s != null) {
                        String key = relativePath;
                        int extIdx = key.lastIndexOf('.');
                        if (extIdx != -1) {
                            key = key.substring(0, extIdx);
                        }
                        registerStructure(key, s);
                        count++;
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to load classpath structure {}", resPath);
            }
        }
        log.info("Successfully loaded {} NBT structures from classpath manifest into StructureManager.", count);
    }

    private static void loadExternalStructures() {
        File folder = new File("plugins/structures");
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }
        scanDirectory(folder, "");
    }

    private static void scanDirectory(File dir, String category) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                String subCategory = category.isEmpty() ? f.getName() : category + "/" + f.getName();
                scanDirectory(f, subCategory);
            } else if (f.getName().endsWith(".nbt") || f.getName().endsWith(".mcstructure")) {
                NBTStructure s = NBTStructure.load(f);
                if (s != null) {
                    String name = f.getName().substring(0, f.getName().lastIndexOf('.'));
                    String key = category.isEmpty() ? name : category + "/" + name;
                    registerStructure(key, s);
                }
            }
        }
    }

    public static void registerStructure(String key, NBTStructure structure) {
        if (key == null || structure == null) return;
        String normalizedKey = key.toLowerCase();
        LOADED_STRUCTURES.put(normalizedKey, structure);

        String category = "common";
        if (normalizedKey.contains("/")) {
            category = normalizedKey.substring(0, normalizedKey.lastIndexOf('/'));
        }

        CATEGORIZED_STRUCTURES.computeIfAbsent(category, k -> Collections.synchronizedList(new ArrayList<>())).add(structure);
    }

    public static NBTStructure getStructure(String name) {
        if (name == null) return null;
        return LOADED_STRUCTURES.get(name.toLowerCase());
    }

    public static void registerGeneratedStructure(String worldName, String category, int x, int y, int z) {
        String key = worldName.toLowerCase();
        GENERATED_STRUCTURE_LOCATIONS.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new StructureLocation(category, x, y, z));
    }

    public static StructureLocation findNearestStructure(String worldName, String categoryFilter, int posX, int posZ) {
        String key = worldName.toLowerCase();
        List<StructureLocation> locs = GENERATED_STRUCTURE_LOCATIONS.get(key);
        if (locs == null || locs.isEmpty()) return null;

        StructureLocation nearest = null;
        double minDistanceSq = Double.MAX_VALUE;

        boolean isAuto = categoryFilter == null || categoryFilter.isEmpty() || "auto".equalsIgnoreCase(categoryFilter);

        for (StructureLocation loc : locs) {
            if (isAuto || loc.category.equalsIgnoreCase(categoryFilter) || loc.category.startsWith(categoryFilter.toLowerCase())) {
                double dx = loc.x - posX;
                double dz = loc.z - posZ;
                double distSq = dx * dx + dz * dz;
                if (distSq < minDistanceSq) {
                    minDistanceSq = distSq;
                    nearest = loc;
                }
            }
        }
        return nearest;
    }

    public static Vector3 getNearestStructure(String worldName, String category, Vector3 from) {
        StructureLocation loc = findNearestStructure(worldName, category, from.getFloorX(), from.getFloorZ());
        if (loc != null) {
            return new Vector3(loc.x, loc.y, loc.z);
        }
        return null;
    }

    public static NBTStructure getRandomStructure(String category, SplittableRandom random) {
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
