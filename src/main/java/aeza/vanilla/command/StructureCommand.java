package aeza.vanilla.command;

import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;

import java.util.SplittableRandom;

public class StructureCommand extends Command {

    public StructureCommand(String name) {
        super(name, "Teleport to nearest structure or generate a structure", "/structure [auto|list|<name>]");
        this.setPermission("vanillagenerator.command.structure");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used in-game.");
            return true;
        }

        String filter = (args.length > 0) ? args[0].toLowerCase() : "auto";

        if (filter.equalsIgnoreCase("list") || filter.equalsIgnoreCase("help") || filter.equalsIgnoreCase("?")) {
            sendStructureList(player);
            return true;
        }

        StructureManager.StructureLocation nearest = StructureManager.findNearestStructure(
                player.getLevel().getName(), filter, player.getFloorX(), player.getFloorZ());

        if (nearest != null) {
            player.teleport(new Location(nearest.x, nearest.y + 2, nearest.z, player.getLevel()));
            player.sendMessage("§a[Structures] Teleported to nearest structure (" + nearest.category + ") at " + nearest.x + ", " + nearest.y + ", " + nearest.z);
            return true;
        }

        String category = filter.equalsIgnoreCase("auto") ? getAutoCategoryForDimension(player.getLevel().getDimension()) : filter;
        NBTStructure struct = StructureManager.getRandomStructure(category, new SplittableRandom());

        if (struct == null) {
            player.sendMessage("§c[Structures] Unknown structure '" + category + "'.");
            sendStructureList(player);
            return true;
        }

        int px = player.getFloorX() + 5;
        int py = player.getFloorY();
        int pz = player.getFloorZ() + 5;

        struct.place(player.getLevel(), px, py, pz);
        StructureManager.registerGeneratedStructure(player.getLevel().getName(), category, px, py, pz);

        player.teleport(new Location(px, py + 2, pz, player.getLevel()));
        player.sendMessage("§a[Structures] Generated and teleported to structure '" + category + "' at " + px + ", " + py + ", " + pz);
        return true;
    }

    private void sendStructureList(Player player) {
        player.sendMessage("§e=== Available Structures (/structure <name>) ===");
        player.sendMessage("§aOverworld: §fvillage, village/plains, village/desert, village/savanna, village/taiga, village/snowy, pillageroutpost, ruined_portal, mansion, igloo, shipwreck, ruin, ancient_city, fossils, trail_ruins, coralcrust");
        player.sendMessage("§cNether: §fbastion, nether_fossils, ruined_portal");
        player.sendMessage("§bEnd: §fendcity");
    }

    private String getAutoCategoryForDimension(int dimension) {
        return switch (dimension) {
            case Level.DIMENSION_NETHER -> "bastion";
            case Level.DIMENSION_THE_END -> "endcity";
            default -> "village";
        };
    }
}
