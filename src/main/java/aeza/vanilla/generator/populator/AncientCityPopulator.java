package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.structure.LootPopulator;
import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class AncientCityPopulator {

    private static final int CITY_RADIUS = 32;

    public static void generateAncientCity(ChunkManager world, SplittableRandom random, int startX, int startY, int startZ, String worldName) {
        int cityY = startY; // Vanilla Y = -51 deep underground right above Bedrock (-64)

        // 1. Carve a hollow underground cavern (64x64x30 blocks) in Deepslate layer
        for (int x = -CITY_RADIUS; x <= CITY_RADIUS; x++) {
            for (int z = -CITY_RADIUS; z <= CITY_RADIUS; z++) {
                int px = startX + x;
                int pz = startZ + z;

                // Floor layer at bottom of city (-52)
                world.setBlockAt(px, cityY - 1, pz, BlockID.DEEPSLATE, 0);

                // Carve air space above floor (-51 to -23)
                for (int y = cityY; y <= cityY + 28; y++) {
                    int cur = world.getBlockIdAt(px, y, pz);
                    if (cur == BlockID.STONE || cur == BlockID.DEEPSLATE || cur == BlockID.DIRT || cur == BlockID.GRAVEL || cur == BlockID.TUFF) {
                        world.setBlockAt(px, y, pz, BlockID.AIR, 0);
                    }
                }
            }
        }

        // 2. Central Warden Portal Frame (City Center)
        NBTStructure cityCenter = StructureManager.getRandomStructure("ancient_city/city_center", random);
        if (cityCenter == null) {
            cityCenter = StructureManager.getStructure("ancient_city/city_center_1");
        }

        if (cityCenter != null) {
            cityCenter.place(world, "ancient_city", startX - 12, cityY, startZ - 12);
        }

        // 3. Extent Paths, Bridges & Chambers along 4 cardinal directions
        int[][] pathDirections = new int[][] {
            { -24, 0 }, { 24, 0 }, { 0, -24 }, { 0, 24 },
            { -24, -24 }, { 24, -24 }, { -24, 24 }, { 24, 24 }
        };

        for (int[] dir : pathDirections) {
            int px = startX + dir[0];
            int pz = startZ + dir[1];

            // Primary path / bridge
            NBTStructure path = StructureManager.getRandomStructure("ancient_city/city_center/entrance_path", random);
            if (path == null) path = StructureManager.getRandomStructure("ancient_city/walls", random);
            if (path != null) {
                path.place(world, "ancient_city_piece", px, cityY, pz);
            }

            // Side buildings (chambers, barracks, ice box, ruins, statues)
            NBTStructure building = StructureManager.getRandomStructure("ancient_city/structures", random);
            if (building == null) building = StructureManager.getRandomStructure("ancient_city/city", random);
            if (building != null) {
                int offX = dir[0] + (dir[0] > 0 ? 8 : -8);
                int offZ = dir[1] + (dir[1] > 0 ? 8 : -8);
                building.place(world, "ancient_city_piece", startX + offX, cityY, startZ + offZ);
            }
        }

        // 4. Spread Sculk Carpet, Sensors, Shriekers & Soul Lanterns across cavern floor
        for (int x = -CITY_RADIUS + 4; x <= CITY_RADIUS - 4; x += 3) {
            for (int z = -CITY_RADIUS + 4; z <= CITY_RADIUS - 4; z += 3) {
                int px = startX + x + random.nextInt(3);
                int pz = startZ + z + random.nextInt(3);

                int floorBlock = world.getBlockIdAt(px, cityY - 1, pz);
                if (floorBlock == BlockID.DEEPSLATE || floorBlock == BlockID.STONE) {
                    if (random.nextInt(3) == 0) {
                        world.setBlockAt(px, cityY - 1, pz, BlockID.SCULK, 0);

                        // Random Sculk Sensor / Shrieker / Catalyst on top
                        int randType = random.nextInt(20);
                        if (randType == 0) {
                            world.setBlockAt(px, cityY, pz, BlockID.SCULK_SHRIEKER, 0);
                        } else if (randType == 1 || randType == 2) {
                            world.setBlockAt(px, cityY, pz, BlockID.SCULK_SENSOR, 0);
                        } else if (randType == 3) {
                            world.setBlockAt(px, cityY, pz, BlockID.SCULK_CATALYST, 0);
                        } else if (randType == 4) {
                            world.setBlockAt(px, cityY, pz, BlockID.SOUL_TORCH, 0);
                        }
                    }
                }
            }
        }

        // 5. Populate Chests with Ancient Loot
        for (int dx = -CITY_RADIUS; dx <= CITY_RADIUS; dx += 2) {
            for (int dy = 0; dy <= 25; dy++) {
                for (int dz = -CITY_RADIUS; dz <= CITY_RADIUS; dz += 2) {
                    int bx = startX + dx;
                    int by = cityY + dy;
                    int bz = startZ + dz;
                    int id = world.getBlockIdAt(bx, by, bz);
                    if (id == BlockID.CHEST || id == BlockID.TRAPPED_CHEST || id == BlockID.BARREL) {
                        LootPopulator.populateAncientCityChest(world, bx, by, bz);
                    }
                }
            }
        }

        // 6. Spawn the Warden at City Center
        FullChunk chunk = world.getChunk(startX >> 4, startZ >> 4);
        if (chunk != null && chunk.getProvider() != null && chunk.getProvider().getLevel() != null) {
            Level lvl = chunk.getProvider().getLevel();
            Location wardenLoc = new Location(startX, cityY + 1, startZ, lvl);
            try {
                Entity warden = Entity.createEntity("Warden", wardenLoc);
                if (warden != null) warden.spawnToAll();
            } catch (Exception ignored) {}
        }

        StructureManager.registerGeneratedStructure(worldName, "ancient_city", startX, cityY, startZ);
    }
}
