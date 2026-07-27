package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.structure.LootPopulator;
import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class MansionPopulator {

    public static void generateMansion(ChunkManager world, SplittableRandom random, int startX, int startY, int startZ, String worldName) {
        if (startY < 60) return;

        int width = 36;
        int depth = 36;

        // 1. Solid Cobblestone Foundation Platform
        for (int x = -2; x <= width + 2; x++) {
            for (int z = -2; z <= depth + 2; z++) {
                int px = startX + x;
                int pz = startZ + z;
                for (int y = startY - 4; y <= startY; y++) {
                    world.setBlockAt(px, y, pz, BlockID.COBBLESTONE, 0);
                }
            }
        }

        // 2. Main Entrance (entrance.nbt)
        NBTStructure entrance = StructureManager.getStructure("mansion/entrance");
        if (entrance != null) {
            entrance.place(world, "mansion", startX + 12, startY + 1, startZ);
        }

        // 3. Outer Walls & Windows (wall_flat.nbt, wall_window.nbt, wall_corner.nbt)
        NBTStructure wallFlat = StructureManager.getStructure("mansion/wall_flat");
        NBTStructure wallWindow = StructureManager.getStructure("mansion/wall_window");
        NBTStructure wallCorner = StructureManager.getStructure("mansion/wall_corner");

        // Ground Floor Walls
        for (int floor = 0; floor < 3; floor++) {
            int floorY = startY + 1 + (floor * 7);

            // Front & Back Walls
            for (int dx = 0; dx <= width; dx += 8) {
                if (floor == 0 && (dx >= 8 && dx <= 16)) continue; // Entrance gap

                NBTStructure w = (dx % 16 == 0 && wallWindow != null) ? wallWindow : wallFlat;
                if (w != null) {
                    w.place(world, "mansion", startX + dx, floorY, startZ);
                    w.place(world, "mansion", startX + dx, floorY, startZ + depth);
                }
            }

            // Side Walls
            for (int dz = 0; dz <= depth; dz += 8) {
                NBTStructure w = (dz % 16 == 0 && wallWindow != null) ? wallWindow : wallFlat;
                if (w != null) {
                    w.place(world, "mansion", startX, floorY, startZ + dz);
                    w.place(world, "mansion", startX + width, floorY, startZ + dz);
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

        // 4. Authentic NBT Rooms (1x1, 1x2, 2x2 layout)
        int[][] roomPositions = new int[][] {
            { 8, 8 }, { 20, 8 }, { 8, 20 }, { 20, 20 }
        };

        for (int floor = 0; floor < 3; floor++) {
            int floorY = startY + 1 + (floor * 7);

            for (int[] pos : roomPositions) {
                int rx = startX + pos[0];
                int rz = startZ + pos[1];

                NBTStructure room = StructureManager.getRandomStructure("mansion/1x1", random);
                if (room == null) room = StructureManager.getRandomStructure("mansion/1x2", random);
                if (room == null) room = StructureManager.getRandomStructure("mansion/2x2", random);

                if (room != null) {
                    room.place(world, "mansion", rx, floorY, rz);
                }
            }
        }

        // 5. Dark Oak Sloped Roof (roof.nbt, roof_corner.nbt, roof_front.nbt)
        NBTStructure roof = StructureManager.getStructure("mansion/roof");
        NBTStructure roofCorner = StructureManager.getStructure("mansion/roof_corner");

        int roofY = startY + 22;
        for (int dx = -2; dx <= width + 2; dx += 4) {
            for (int dz = -2; dz <= depth + 2; dz += 4) {
                NBTStructure r = ((dx == -2 || dx >= width) && roofCorner != null) ? roofCorner : roof;
                if (r != null) {
                    r.place(world, "mansion", startX + dx, roofY, startZ + dz);
                }
            }
        }

        // 6. Illagers (Evokers & Vindicators) & Loot Chests
        if (world instanceof FullChunk chunk && chunk.getProvider() != null && chunk.getProvider().getLevel() != null) {
            var level = chunk.getProvider().getLevel();

            // Spawn Evoker & Vindicators inside
            Location evokerLoc = new Location(startX + 18, startY + 9, startZ + 18, level);
            Entity.createEntity("Evoker", evokerLoc);

            for (int i = 0; i < 4; i++) {
                Location vindLoc = new Location(startX + 8 + (i * 6), startY + 2, startZ + 10, level);
                Entity.createEntity("Vindicator", vindLoc);
            }
        }

        // Populate Mansion Chests
        for (int dx = 0; dx <= width; dx += 4) {
            for (int dy = 1; dy <= 24; dy++) {
                for (int dz = 0; dz <= depth; dz += 4) {
                    int bx = startX + dx;
                    int by = startY + dy;
                    int bz = startZ + dz;
                    int id = world.getBlockIdAt(bx, by, bz);
                    if (id == BlockID.CHEST || id == BlockID.TRAPPED_CHEST) {
                        LootPopulator.populateChest(world, bx, by, bz);
                    }
                }
            }
        }

        StructureManager.registerGeneratedStructure(worldName, "mansion", startX, startY, startZ);
    }
}
