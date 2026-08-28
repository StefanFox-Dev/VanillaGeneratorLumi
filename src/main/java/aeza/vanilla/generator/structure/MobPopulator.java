package aeza.vanilla.generator.structure;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;

public class MobPopulator {

    public static void spawnStructureMobs(ChunkManager level, String category, int startX, int startY, int startZ) {
        FullChunk chunk = level.getChunk(startX >> 4, startZ >> 4);
        if (chunk == null || chunk.getProvider() == null || chunk.getProvider().getLevel() == null) return;

        var lvl = chunk.getProvider().getLevel();
        Location loc = new Location(startX + 4, startY + 1, startZ + 4, lvl);

        String cat = category.toLowerCase();

        // Only spawn mobs for primary structure anchors (not for every sub-block or room piece)
        if (cat.equals("village/town_centers") || cat.equals("village")) {
            for (int i = 0; i < 3; i++) {
                try {
                    Entity v = Entity.createEntity("Villager", loc.add(i * 2, 0, i));
                    if (v != null) v.spawnToAll();
                } catch (Exception ignored) {}
            }
            try {
                Entity g = Entity.createEntity("IronGolem", loc.add(1, 0, 1));
                if (g != null) g.spawnToAll();
            } catch (Exception ignored) {}
        } else if (cat.equals("bastion/treasure") || cat.equals("bastion")) {
            for (int i = 0; i < 3; i++) {
                try {
                    Entity p = Entity.createEntity("Piglin", loc.add(i * 2, 0, i));
                    if (p != null) p.spawnToAll();
                } catch (Exception ignored) {}
            }
        } else if (cat.equals("pillageroutpost_main") || cat.equals("pillageroutpost")) {
            for (int i = 0; i < 3; i++) {
                try {
                    Entity p = Entity.createEntity("Pillager", loc.add(i * 2, 0, i));
                    if (p != null) p.spawnToAll();
                } catch (Exception ignored) {}
            }
        } else if (cat.equals("mansion_main")) {
            try {
                Entity e = Entity.createEntity("Evoker", loc);
                if (e != null) e.spawnToAll();
            } catch (Exception ignored) {}
            for (int i = 0; i < 3; i++) {
                try {
                    Entity v = Entity.createEntity("Vindicator", loc.add(i * 3, 0, i));
                    if (v != null) v.spawnToAll();
                } catch (Exception ignored) {}
            }
        }
    }
}
