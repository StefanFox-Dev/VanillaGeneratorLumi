package aeza.vanilla.generator.structure;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.impl.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.SplittableRandom;

public class LootPopulator {

    private static final int[][] GENERAL_LOOT_TABLE = {
        { ItemID.EMERALD, 1, 6 },
        { ItemID.GOLD_INGOT, 2, 8 },
        { ItemID.IRON_INGOT, 2, 8 },
        { ItemID.DIAMOND, 1, 4 },
        { ItemID.NETHERITE_SCRAP, 1, 2 },
        { ItemID.BREAD, 2, 8 },
        { ItemID.APPLE, 1, 4 },
        { ItemID.GOLDEN_APPLE, 1, 2 },
        { BlockID.OBSIDIAN, 2, 6 },
        { ItemID.CROSSBOW, 1, 1 },
        { ItemID.TOTEM, 1, 1 },
        { ItemID.BOOK, 1, 3 },
        { ItemID.ARROW, 4, 16 }
    };

    private static final int[][] ANCIENT_CITY_LOOT_TABLE = {
        { ItemID.ENCHANTED_GOLDEN_APPLE, 1, 2 },
        { ItemID.RECORD_5, 1, 1 },
        { ItemID.DISC_FRAGMENT_5, 1, 3 },
        { ItemID.GOLDEN_APPLE, 1, 4 },
        { BlockID.SCULK_SENSOR, 1, 3 },
        { BlockID.SCULK_CATALYST, 1, 2 },
        { ItemID.DIAMOND_LEGGINGS, 1, 1 },
        { ItemID.DIAMOND_HOE, 1, 1 },
        { BlockID.SOUL_TORCH, 1, 15 },
        { ItemID.NAME_TAG, 1, 2 },
        { ItemID.LEAD, 1, 2 },
        { ItemID.RECORD_13, 1, 1 },
        { ItemID.RECORD_CAT, 1, 1 },
        { ItemID.EXPERIENCE_BOTTLE, 1, 3 },
        { ItemID.ENCHANTED_BOOK, 1, 2 },
        { BlockID.CANDLE, 1, 4 }
    };

    public static void populateChest(ChunkManager level, int x, int y, int z) {
        populateChestInternal(level, x, y, z, GENERAL_LOOT_TABLE);
    }

    public static void populateAncientCityChest(ChunkManager level, int x, int y, int z) {
        populateChestInternal(level, x, y, z, ANCIENT_CITY_LOOT_TABLE);
    }

    private static void populateChestInternal(ChunkManager level, int x, int y, int z, int[][] lootTable) {
        FullChunk chunk = level.getChunk(x >> 4, z >> 4);
        if (chunk == null) return;

        int localX = x & 0x0f;
        int localZ = z & 0x0f;

        BlockEntity entity = chunk.getTile(localX, y, localZ);
        if (entity == null) {
            CompoundTag nbt = BlockEntity.getDefaultCompound(new Vector3(x, y, z), BlockEntity.CHEST);
            entity = BlockEntity.createBlockEntity(BlockEntity.CHEST, chunk, nbt);
            if (entity != null) {
                chunk.addBlockEntity(entity);
            }
        }

        if (entity instanceof BlockEntityChest chest) {
            var inv = chest.getRealInventory();
            if (inv != null) {
                inv.clearAll();
                SplittableRandom rand = new SplittableRandom((long) x * 31L + (long) y * 17L + (long) z * 13L + level.getSeed());
                int itemCount = 6 + rand.nextInt(5);

                for (int i = 0; i < itemCount; i++) {
                    int slot = rand.nextInt(27);
                    int[] loot = lootTable[rand.nextInt(lootTable.length)];
                    int count = loot[1] + (loot[2] > loot[1] ? rand.nextInt(loot[2] - loot[1] + 1) : 0);
                    inv.setItem(slot, Item.get(loot[0], 0, count));
                }
            }
        }
    }
}
