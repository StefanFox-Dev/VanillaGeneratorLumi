package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class MansionPopulator {

    private static final int MANSION_GRID_SIZE = 5; // 5x5 grid of 8x8 units = 40x40 blocks footprint
    private static final int ROOM_SIZE = 8;
    private static final int FLOOR_HEIGHT = 7;

    public static void generateMansion(ChunkManager world, SplittableRandom random, int startX, int startY, int startZ, String worldName) {
        int width = MANSION_GRID_SIZE * ROOM_SIZE; // 40 blocks
        int depth = MANSION_GRID_SIZE * ROOM_SIZE; // 40 blocks
        int heightFloors = 3;

        // 1. Solid Cobblestone Foundation
        for (int x = -2; x <= width + 2; x++) {
            for (int z = -2; z <= depth + 2; z++) {
                int px = startX + x;
                int pz = startZ + z;
                for (int fy = startY - 1; fy >= startY - 10; fy--) {
                    int cur = world.getBlockIdAt(px, fy, pz);
                    if (cur == BlockID.AIR || cur == BlockID.WATER || cur == BlockID.STILL_WATER || cur == BlockID.LEAVES || cur == BlockID.TALL_GRASS) {
                        world.setBlockAt(px, fy, pz, BlockID.COBBLESTONE, 0);
                    } else {
                        break;
                    }
                }
            }
        }

        // 2. Build Floor 1, Floor 2, Floor 3 Structure Grid
        for (int floor = 0; floor < heightFloors; floor++) {
            int floorY = startY + (floor * FLOOR_HEIGHT);

            // Floor base slab/plank layer
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    world.setBlockAt(startX + x, floorY, startZ + z, BlockID.PLANKS, 1); // Dark Oak Planks
                }
            }

            // Fill rooms in grid
            boolean[][] occupied = new boolean[MANSION_GRID_SIZE][MANSION_GRID_SIZE];

            // Ground floor entrance facing front
            if (floor == 0) {
                NBTStructure entrance = StructureManager.getStructure("mansion/entrance");
                if (entrance != null) {
                    entrance.place(world, "mansion", startX + 16, floorY, startZ);
                    occupied[2][0] = true;
                    occupied[2][1] = true;
                }
            }

            // Populate 2x2 and 1x2 and 1x1 rooms
            for (int gx = 0; gx < MANSION_GRID_SIZE; gx++) {
                for (int gz = 0; gz < MANSION_GRID_SIZE; gz++) {
                    if (occupied[gx][gz]) continue;

                    int rx = startX + (gx * ROOM_SIZE);
                    int rz = startZ + (gz * ROOM_SIZE);

                    // Try 2x2 room
                    if (gx < MANSION_GRID_SIZE - 1 && gz < MANSION_GRID_SIZE - 1 && !occupied[gx+1][gz] && !occupied[gx][gz+1] && !occupied[gx+1][gz+1] && random.nextInt(4) == 0) {
                        NBTStructure room2x2 = StructureManager.getRandomStructure("mansion/2x2", random);
                        if (room2x2 != null) {
                            room2x2.place(world, "mansion", rx, floorY, rz);
                            occupied[gx][gz] = true;
                            occupied[gx+1][gz] = true;
                            occupied[gx][gz+1] = true;
                            occupied[gx+1][gz+1] = true;
                            continue;
                        }
                    }

                    // Try 1x2 room
                    if (gz < MANSION_GRID_SIZE - 1 && !occupied[gx][gz+1] && random.nextInt(3) == 0) {
                        NBTStructure room1x2 = StructureManager.getRandomStructure("mansion/1x2", random);
                        if (room1x2 != null) {
                            room1x2.place(world, "mansion", rx, floorY, rz);
                            occupied[gx][gz] = true;
                            occupied[gx][gz+1] = true;
                            continue;
                        }
                    }

                    // 1x1 room or corridor
                    NBTStructure room1x1 = StructureManager.getRandomStructure("mansion/1x1", random);
                    if (room1x1 == null) {
                        room1x1 = StructureManager.getRandomStructure("mansion", random);
                    }
                    if (room1x1 != null) {
                        room1x1.place(world, "mansion", rx, floorY, rz);
                        occupied[gx][gz] = true;
                    }
                }
            }

            // Outer Frame & Walls per floor
            buildFloorOuterWalls(world, startX, floorY, startZ, width, depth);
        }

        // 3. Sloped Dark Oak Roof on top
        buildGrandRoof(world, startX, startY + (heightFloors * FLOOR_HEIGHT), startZ, width, depth);

        StructureManager.registerGeneratedStructure(worldName, "mansion", startX, startY, startZ);
    }

    private static void buildFloorOuterWalls(ChunkManager world, int startX, int floorY, int startZ, int width, int depth) {
        NBTStructure wallWindow = StructureManager.getStructure("mansion/wall_window");
        NBTStructure wallFlat = StructureManager.getStructure("mansion/wall_flat");
        NBTStructure wallCorner = StructureManager.getStructure("mansion/wall_corner");

        // North & South walls
        for (int x = 0; x < width; x += 8) {
            NBTStructure wall = (x % 16 == 0 && wallWindow != null) ? wallWindow : wallFlat;
            if (wall != null) {
                wall.place(world, "mansion", startX + x, floorY, startZ);
                wall.place(world, "mansion", startX + x, floorY, startZ + depth - 1);
            }
        }

        // East & West walls
        for (int z = 0; z < depth; z += 8) {
            NBTStructure wall = (z % 16 == 0 && wallWindow != null) ? wallWindow : wallFlat;
            if (wall != null) {
                wall.place(world, "mansion", startX, floorY, startZ + z);
                wall.place(world, "mansion", startX + width - 1, floorY, startZ + z);
            }
        }

        // Corners
        if (wallCorner != null) {
            wallCorner.place(world, "mansion", startX, floorY, startZ);
            wallCorner.place(world, "mansion", startX + width, floorY, startZ);
            wallCorner.place(world, "mansion", startX, floorY, startZ + depth);
            wallCorner.place(world, "mansion", startX + width, floorY, startZ + depth);
        }
    }

    private static void buildGrandRoof(ChunkManager world, int startX, int roofY, int startZ, int width, int depth) {
        NBTStructure roofTile = StructureManager.getStructure("mansion/roof");
        NBTStructure roofCorner = StructureManager.getStructure("mansion/roof_corner");
        NBTStructure roofFront = StructureManager.getStructure("mansion/roof_front");

        int maxStep = 5;
        for (int step = 0; step < maxStep; step++) {
            int currentY = roofY + step;
            int rxMin = startX - 1 + step;
            int rxMax = startX + width + 1 - step;
            int rzMin = startZ - 1 + step;
            int rzMax = startZ + depth + 1 - step;

            if (rxMin >= rxMax || rzMin >= rzMax) break;

            for (int x = rxMin; x <= rxMax; x++) {
                world.setBlockAt(x, currentY, rzMin, BlockID.WOODEN_STAIRS, 5); // Dark oak stairs
                world.setBlockAt(x, currentY, rzMax, BlockID.WOODEN_STAIRS, 5);
            }

            for (int z = rzMin; z <= rzMax; z++) {
                world.setBlockAt(rxMin, currentY, z, BlockID.WOODEN_STAIRS, 5);
                world.setBlockAt(rxMax, currentY, z, BlockID.WOODEN_STAIRS, 5);
            }

            // Cobblestone outline border on outer rim
            if (step == 0) {
                for (int x = rxMin; x <= rxMax; x += 8) {
                    if (roofFront != null) {
                        roofFront.place(world, "mansion", x, currentY, rzMin);
                        roofFront.place(world, "mansion", x, currentY, rzMax);
                    }
                }
                if (roofCorner != null) {
                    roofCorner.place(world, "mansion", rxMin, currentY, rzMin);
                    roofCorner.place(world, "mansion", rxMax, currentY, rzMin);
                    roofCorner.place(world, "mansion", rxMin, currentY, rzMax);
                    roofCorner.place(world, "mansion", rxMax, currentY, rzMax);
                }
            }
        }

        // Fill roof cap center with dark oak planks
        int capY = roofY + maxStep;
        for (int x = startX + maxStep; x <= startX + width - maxStep; x++) {
            for (int z = startZ + maxStep; z <= startZ + depth - maxStep; z++) {
                world.setBlockAt(x, capY, z, BlockID.PLANKS, 1);
            }
        }
    }
}
