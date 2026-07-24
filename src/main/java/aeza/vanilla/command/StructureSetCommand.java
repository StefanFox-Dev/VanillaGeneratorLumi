package aeza.vanilla.command;

import aeza.vanilla.generator.structure.LootPopulator;
import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;

import java.util.Random;

public class StructureSetCommand extends Command {

    public StructureSetCommand(String name) {
        super(name, "Spawn a full structure directly at your location", "/stset <structure_name>");
        this.setPermission("vanillagenerator.command.stset");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used in-game.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /stset <structure_name> (e.g. /stset mansion, /stset igloo, /stset ancient_city, /stset bastion, /stset village)");
            player.sendMessage("§eAvailable categories: mansion, igloo, ancient_city, bastion, village, endcity, ruined_portal, pillageroutpost, shipwreck, ruin, fossils");
            return true;
        }

        String category = args[0].toLowerCase();
        Random rand = new Random();

        int px = player.getFloorX();
        int py = player.getFloorY();
        int pz = player.getFloorZ();

        // 1. IGLOO FULL STRUCTURE (Surface Snow Dome + Secret Basement Ladder & Lab)
        if (category.contains("igloo")) {
            NBTStructure top = StructureManager.getRandomStructure("igloo/igloo_top_trapdoor", rand);
            if (top == null) top = StructureManager.getRandomStructure("igloo/igloo_top_no_trapdoor", rand);
            if (top == null) top = StructureManager.getRandomStructure("igloo", rand);

            if (top != null) {
                top.place(player.getLevel(), "igloo", px, py, pz);

                // Secret basement ladder shaft down
                NBTStructure mid = StructureManager.getRandomStructure("igloo/igloo_middle", rand);
                if (mid != null) {
                    for (int depth = 3; depth <= 12; depth += 2) {
                        mid.place(player.getLevel(), "igloo", px, py - depth, pz);
                    }
                }

                // Secret underground laboratory room
                NBTStructure bottom = StructureManager.getRandomStructure("igloo/igloo_bottom", rand);
                if (bottom != null) {
                    bottom.place(player.getLevel(), "igloo", px - 2, py - 14, pz - 2);
                }

                StructureManager.registerGeneratedStructure(player.getLevel().getName(), "igloo", px, py, pz);
                player.teleport(new Location(px + 2, py + 2, pz + 2, player.getLevel()));
                player.sendMessage("§a[Structures] Successfully spawned full Igloo with secret underground basement at " + px + ", " + py + ", " + pz);
                return true;
            }
        }

        // 2. WOODLAND MANSION FULL MULTI-STORY PLACEMENT
        if (category.contains("mansion")) {
            NBTStructure entrance = StructureManager.getRandomStructure("mansion/entrance", rand);
            if (entrance == null) entrance = StructureManager.getRandomStructure("mansion", rand);

            if (entrance != null) {
                // Place grand entrance lobby
                entrance.place(player.getLevel(), "mansion", px, py, pz);

                // Floor 1 & Floor 2 rooms around entrance
                int[][] roomOffsets = new int[][] {
                    {-16, 0}, {16, 0}, {-16, 16}, {16, 16},
                    {-32, 0}, {32, 0}, {-32, 16}, {32, 16},
                    {-16, -16}, {16, -16}, {0, -16}, {0, 16}
                };

                for (int[] off : roomOffsets) {
                    NBTStructure room = StructureManager.getRandomStructure("mansion", rand);
                    if (room != null) {
                        room.place(player.getLevel(), "mansion", px + off[0], py, pz + off[1]);
                        // Floor 2
                        NBTStructure room2 = StructureManager.getRandomStructure("mansion", rand);
                        if (room2 != null) {
                            room2.place(player.getLevel(), "mansion", px + off[0], py + 7, pz + off[1]);
                        }
                    }
                }

                StructureManager.registerGeneratedStructure(player.getLevel().getName(), "mansion", px, py, pz);
                player.teleport(new Location(px, py + 4, pz, player.getLevel()));
                player.sendMessage("§a[Structures] Successfully spawned full 2-story Woodland Mansion at " + px + ", " + py + ", " + pz);
                return true;
            }
        }

        // 3. ANCIENT CITY FULL MULTI-PART PLACEMENT
        if (category.contains("ancient")) {
            NBTStructure center = StructureManager.getRandomStructure("ancient_city/city_center", rand);
            if (center == null) center = StructureManager.getRandomStructure("ancient_city", rand);

            if (center != null) {
                center.place(player.getLevel(), "ancient_city", px, py, pz);

                int[][] offsets = new int[][] {
                    {-24, -24}, {0, -28}, {24, -24},
                    {-28, 0},             {28, 0},
                    {-24, 24},  {0, 28},  {24, 24}
                };

                for (int[] off : offsets) {
                    NBTStructure sub = StructureManager.getRandomStructure("ancient_city/city", rand);
                    if (sub == null) sub = StructureManager.getRandomStructure("ancient_city/structures", rand);
                    if (sub == null) sub = StructureManager.getRandomStructure("ancient_city/walls", rand);
                    if (sub != null) {
                        sub.place(player.getLevel(), "ancient_city", px + off[0], py, pz + off[1]);
                    }
                }
                StructureManager.registerGeneratedStructure(player.getLevel().getName(), "ancient_city", px, py, pz);
                player.teleport(new Location(px, py + 4, pz, player.getLevel()));
                player.sendMessage("§a[Structures] Successfully spawned full Ancient City settlement at " + px + ", " + py + ", " + pz);
                return true;
            }
        }

        // 4. BASTION FULL MULTI-PART PLACEMENT
        if (category.contains("bastion")) {
            NBTStructure center = StructureManager.getRandomStructure("bastion/treasure", rand);
            if (center == null) center = StructureManager.getRandomStructure("bastion/units/center_pieces", rand);
            if (center == null) center = StructureManager.getRandomStructure("bastion/units", rand);

            if (center != null) {
                center.place(player.getLevel(), "bastion", px, py, pz);

                int[][] offsets = new int[][] {
                    {-20, -20}, {0, -24}, {20, -20},
                    {-24, 0},             {24, 0},
                    {-20, 20},  {0, 24},  {20, 20}
                };

                for (int[] off : offsets) {
                    NBTStructure sub = StructureManager.getRandomStructure("bastion/units/walls", rand);
                    if (sub == null) sub = StructureManager.getRandomStructure("bastion/bridge", rand);
                    if (sub == null) sub = StructureManager.getRandomStructure("bastion/hoglin_stable", rand);
                    if (sub != null) {
                        sub.place(player.getLevel(), "bastion", px + off[0], py, pz + off[1]);
                    }
                }

                // Register chest tile entities and populate loot
                for (int dx = -30; dx <= 30; dx += 2) {
                    for (int dy = -5; dy <= 25; dy++) {
                        for (int dz = -30; dz <= 30; dz += 2) {
                            int bx = px + dx;
                            int by = py + dy;
                            int bz = pz + dz;
                            int id = player.getLevel().getBlockIdAt(bx, by, bz);
                            if (id == BlockID.CHEST || id == BlockID.TRAPPED_CHEST || id == BlockID.BARREL) {
                                LootPopulator.populateChest(player.getLevel(), bx, by, bz);
                            }
                        }
                    }
                }

                StructureManager.registerGeneratedStructure(player.getLevel().getName(), "bastion", px, py, pz);
                player.teleport(new Location(px, py + 4, pz, player.getLevel()));
                player.sendMessage("§a[Structures] Successfully spawned full Bastion with chest loot at " + px + ", " + py + ", " + pz);
                return true;
            }
        }

        // 5. DEFAULT SINGLE/MULTI STRUCTURE PLACEMENT
        NBTStructure struct = StructureManager.getRandomStructure(category, rand);
        if (struct == null) {
            player.sendMessage("§c[Structures] Unknown structure template '" + category + "'.");
            player.sendMessage("§eAvailable: mansion, igloo, ancient_city, bastion, village, endcity, ruined_portal, pillageroutpost, shipwreck, ruin, fossils");
            return true;
        }

        struct.place(player.getLevel(), category, px, py, pz);
        StructureManager.registerGeneratedStructure(player.getLevel().getName(), category, px, py, pz);

        player.teleport(new Location(px, py + 2, pz, player.getLevel()));
        player.sendMessage("§a[Structures] Successfully spawned structure '" + category + "' at " + px + ", " + py + ", " + pz);
        return true;
    }
}
