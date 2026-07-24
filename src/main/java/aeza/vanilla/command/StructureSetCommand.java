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
            player.sendMessage("§cUsage: /stset <structure_name> (e.g. /stset ancient_city, /stset bastion, /stset village, /stset endcity)");
            player.sendMessage("§eAvailable categories: ancient_city, bastion, village, endcity, ruined_portal, pillageroutpost, mansion, igloo, shipwreck, ruin, fossils");
            return true;
        }

        String category = args[0].toLowerCase();
        Random rand = new Random();

        int px = player.getFloorX();
        int py = player.getFloorY();
        int pz = player.getFloorZ();

        // 1. ANCIENT CITY FULL MULTI-PART PLACEMENT
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

        // 2. BASTION FULL MULTI-PART PLACEMENT
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

        // 3. DEFAULT SINGLE/MULTI STRUCTURE PLACEMENT
        NBTStructure struct = StructureManager.getRandomStructure(category, rand);
        if (struct == null) {
            player.sendMessage("§c[Structures] Unknown structure template '" + category + "'.");
            player.sendMessage("§eAvailable: ancient_city, bastion, village, endcity, ruined_portal, pillageroutpost, mansion, igloo, shipwreck, ruin, fossils");
            return true;
        }

        struct.place(player.getLevel(), category, px, py, pz);
        StructureManager.registerGeneratedStructure(player.getLevel().getName(), category, px, py, pz);

        player.teleport(new Location(px, py + 2, pz, player.getLevel()));
        player.sendMessage("§a[Structures] Successfully spawned structure '" + category + "' at " + px + ", " + py + ", " + pz);
        return true;
    }
}
