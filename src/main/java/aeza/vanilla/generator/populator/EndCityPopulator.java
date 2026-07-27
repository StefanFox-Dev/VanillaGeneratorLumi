package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.structure.LootPopulator;
import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;

import java.util.SplittableRandom;

public class EndCityPopulator {

    public static void generateEndCity(ChunkManager world, SplittableRandom random, int startX, int startY, int startZ, String worldName) {
        if (startY < 30) return;

        // 1. Central Base Tower
        NBTStructure baseFloor = StructureManager.getStructure("endcity/base_floor");
        if (baseFloor == null) baseFloor = StructureManager.getRandomStructure("endcity", random);

        if (baseFloor != null) {
            baseFloor.place(world, "endcity", startX, startY, startZ);

            int currentY = startY + baseFloor.getSizeY();

            // Additional floors (2nd & 3rd floor)
            NBTStructure secondFloor = StructureManager.getStructure("endcity/second_floor");
            if (secondFloor != null) {
                secondFloor.place(world, "endcity", startX, currentY, startZ);
                currentY += secondFloor.getSizeY();
            }

            NBTStructure thirdFloor = StructureManager.getStructure("endcity/third_floor");
            if (thirdFloor != null) {
                thirdFloor.place(world, "endcity", startX, currentY, startZ);
                currentY += thirdFloor.getSizeY();
            }

            NBTStructure baseRoof = StructureManager.getStructure("endcity/base_roof");
            if (baseRoof != null) {
                baseRoof.place(world, "endcity", startX, currentY, startZ);
            }

            // 2. High Vertical Towers (Main Tower + Fat Tower)
            int mainTowerHeight = currentY + 12;
            NBTStructure fatTowerBase = StructureManager.getStructure("endcity/fat_tower_base");
            if (fatTowerBase != null) {
                fatTowerBase.place(world, "endcity", startX + 16, currentY, startZ + 16);

                NBTStructure fatTowerMid = StructureManager.getStructure("endcity/fat_tower_middle");
                if (fatTowerMid != null) {
                    fatTowerMid.place(world, "endcity", startX + 16, currentY + 12, startZ + 16);
                }

                NBTStructure fatTowerTop = StructureManager.getStructure("endcity/fat_tower_top");
                if (fatTowerTop != null) {
                    fatTowerTop.place(world, "endcity", startX + 16, currentY + 24, startZ + 16);
                }
            }

            // 3. Branching Bridges and Side Towers
            int[][] sideOffsets = new int[][] {
                { -20, 0 }, { 20, 0 }, { 0, -20 }, { 0, 20 }
            };

            for (int[] off : sideOffsets) {
                int bx = startX + off[0];
                int bz = startZ + off[1];

                // Connecting Bridges
                NBTStructure bridge = StructureManager.getStructure("endcity/bridge_piece");
                if (bridge == null) bridge = StructureManager.getStructure("endcity/bridge_steep_stairs");
                if (bridge != null) {
                    bridge.place(world, "endcity", startX + (off[0] / 2), currentY + 4, startZ + (off[1] / 2));
                }

                // Side Tower
                NBTStructure towerBase = StructureManager.getStructure("endcity/tower_base");
                if (towerBase == null) towerBase = StructureManager.getRandomStructure("endcity", random);

                if (towerBase != null) {
                    towerBase.place(world, "endcity", bx, currentY, bz);

                    NBTStructure towerTop = StructureManager.getStructure("endcity/tower_top");
                    if (towerTop != null) {
                        towerTop.place(world, "endcity", bx, currentY + towerBase.getSizeY(), bz);
                    }
                }
            }

            // 4. Floating End Ship (End Ship) (~80% chance floating in the air near high tower)
            if (random.nextInt(10) < 8) {
                NBTStructure endShip = StructureManager.getStructure("endcity/ship");
                if (endShip != null) {
                    int shipX = startX + 32;
                    int shipY = mainTowerHeight + 14;
                    int shipZ = startZ + 16;
                    endShip.place(world, "endcity", shipX, shipY, shipZ);
                }
            }

            // 5. Populate Chests with End City Loot
            for (int dx = -30; dx <= 50; dx += 2) {
                for (int dy = 0; dy <= 60; dy++) {
                    for (int dz = -30; dz <= 50; dz += 2) {
                        int bx = startX + dx;
                        int by = startY + dy;
                        int bz = startZ + dz;
                        int id = world.getBlockIdAt(bx, by, bz);
                        if (id == BlockID.CHEST || id == BlockID.TRAPPED_CHEST || id == BlockID.BARREL) {
                            LootPopulator.populateChest(world, bx, by, bz);
                        }
                    }
                }
            }

            StructureManager.registerGeneratedStructure(worldName, "endcity", startX, startY, startZ);
        }
    }
}
