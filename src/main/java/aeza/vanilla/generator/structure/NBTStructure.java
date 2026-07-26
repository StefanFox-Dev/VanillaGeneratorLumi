package aeza.vanilla.generator.structure;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.material.BlockType;
import cn.nukkit.block.material.BlockTypes;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.UpdateSubChunkBlocksPacket;
import cn.nukkit.network.protocol.types.BlockChangeEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.InflaterInputStream;

@Slf4j
public class NBTStructure {

    public static class BlockState {
        public final int id;
        public final int meta;

        public BlockState(int id, int meta) {
            this.id = id;
            this.meta = meta;
        }
    }

    public static class StructureBlock {
        public final int x;
        public final int y;
        public final int z;
        public final int stateIndex;

        public StructureBlock(int x, int y, int z, int stateIndex) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.stateIndex = stateIndex;
        }
    }

    private static class PlacedBlockRecord {
        final int x;
        final int y;
        final int z;
        final int id;
        final int meta;

        PlacedBlockRecord(int x, int y, int z, int id, int meta) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.id = id;
            this.meta = meta;
        }
    }

    private static class SubChunkKey {
        final int cx;
        final int cy;
        final int cz;

        SubChunkKey(int cx, int cy, int cz) {
            this.cx = cx;
            this.cy = cy;
            this.cz = cz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SubChunkKey that)) return false;
            return cx == that.cx && cy == that.cy && cz == that.cz;
        }

        @Override
        public int hashCode() {
            return (cx * 31 + cy) * 31 + cz;
        }
    }

    private static final Map<String, Integer> BLOCK_NAME_TO_ID = new HashMap<>();

    static {
        BLOCK_NAME_TO_ID.put("air", BlockID.AIR);
        BLOCK_NAME_TO_ID.put("structure_void", BlockID.AIR);
        BLOCK_NAME_TO_ID.put("structure_block", BlockID.AIR);
        BLOCK_NAME_TO_ID.put("barrier", BlockID.AIR);
        BLOCK_NAME_TO_ID.put("jigsaw", BlockID.AIR);

        // Nether & End
        BLOCK_NAME_TO_ID.put("blackstone", BlockID.BLACKSTONE);
        BLOCK_NAME_TO_ID.put("polished_blackstone_bricks", BlockID.POLISHED_BLACKSTONE_BRICKS);
        BLOCK_NAME_TO_ID.put("polished_blackstone_brick_stairs", BlockID.POLISHED_BLACKSTONE_BRICK_STAIRS);
        BLOCK_NAME_TO_ID.put("blackstone_stairs", BlockID.BLACKSTONE_STAIRS);
        BLOCK_NAME_TO_ID.put("blackstone_wall", BlockID.BLACKSTONE_WALL);
        BLOCK_NAME_TO_ID.put("polished_blackstone_brick_wall", BlockID.POLISHED_BLACKSTONE_BRICK_WALL);
        BLOCK_NAME_TO_ID.put("chiseled_polished_blackstone", BlockID.CHISELED_POLISHED_BLACKSTONE);
        BLOCK_NAME_TO_ID.put("cracked_polished_blackstone_bricks", BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        BLOCK_NAME_TO_ID.put("gilded_blackstone", BlockID.GILDED_BLACKSTONE);
        BLOCK_NAME_TO_ID.put("blackstone_slab", BlockID.BLACKSTONE_SLAB);
        BLOCK_NAME_TO_ID.put("polished_blackstone_brick_slab", BlockID.POLISHED_BLACKSTONE_BRICK_SLAB);
        BLOCK_NAME_TO_ID.put("chain", BlockID.CHAIN_BLOCK);
        BLOCK_NAME_TO_ID.put("crying_obsidian", BlockID.CRYING_OBSIDIAN);
        BLOCK_NAME_TO_ID.put("polished_blackstone", BlockID.POLISHED_BLACKSTONE);
        BLOCK_NAME_TO_ID.put("polished_blackstone_stairs", BlockID.POLISHED_BLACKSTONE_STAIRS);
        BLOCK_NAME_TO_ID.put("polished_blackstone_slab", BlockID.POLISHED_BLACKSTONE_SLAB);
        BLOCK_NAME_TO_ID.put("polished_blackstone_wall", BlockID.POLISHED_BLACKSTONE_WALL);
        BLOCK_NAME_TO_ID.put("basalt", BlockID.BASALT);
        BLOCK_NAME_TO_ID.put("polished_basalt", BlockID.POLISHED_BASALT);
        BLOCK_NAME_TO_ID.put("soul_soil", BlockID.SOUL_SOIL);
        BLOCK_NAME_TO_ID.put("soul_sand", BlockID.SOUL_SAND);
        BLOCK_NAME_TO_ID.put("soul_torch", BlockID.SOUL_TORCH);
        BLOCK_NAME_TO_ID.put("soul_lantern", BlockID.SOUL_LANTERN);
        BLOCK_NAME_TO_ID.put("netherrack", BlockID.NETHERRACK);
        BLOCK_NAME_TO_ID.put("nether_bricks", BlockID.NETHER_BRICK_BLOCK);
        BLOCK_NAME_TO_ID.put("nether_brick_stairs", BlockID.NETHER_BRICKS_STAIRS);
        BLOCK_NAME_TO_ID.put("chiseled_nether_bricks", BlockID.CHISELED_NETHER_BRICKS);
        BLOCK_NAME_TO_ID.put("cracked_nether_bricks", BlockID.CRACKED_NETHER_BRICKS);
        BLOCK_NAME_TO_ID.put("magma_block", BlockID.MAGMA);
        BLOCK_NAME_TO_ID.put("magma", BlockID.MAGMA);
        BLOCK_NAME_TO_ID.put("glowstone", BlockID.GLOWSTONE);
        BLOCK_NAME_TO_ID.put("gold_block", BlockID.GOLD_BLOCK);
        BLOCK_NAME_TO_ID.put("bone_block", BlockID.BONE_BLOCK);
        BLOCK_NAME_TO_ID.put("ancient_debris", BlockID.ANCIENT_DEBRIS);
        BLOCK_NAME_TO_ID.put("netherite_block", BlockID.NETHERITE_BLOCK);

        // Deepslate & Sculk
        BLOCK_NAME_TO_ID.put("deepslate", BlockID.DEEPSLATE);
        BLOCK_NAME_TO_ID.put("cobbled_deepslate", BlockID.COBBLED_DEEPSLATE);
        BLOCK_NAME_TO_ID.put("polished_deepslate", BlockID.POLISHED_DEEPSLATE);
        BLOCK_NAME_TO_ID.put("deepslate_bricks", BlockID.DEEPSLATE_BRICKS);
        BLOCK_NAME_TO_ID.put("deepslate_tiles", BlockID.DEEPSLATE_TILES);
        BLOCK_NAME_TO_ID.put("sculk", BlockID.SCULK);
        BLOCK_NAME_TO_ID.put("sculk_catalyst", BlockID.SCULK_CATALYST);
        BLOCK_NAME_TO_ID.put("sculk_shrieker", BlockID.SCULK_SHRIEKER);
        BLOCK_NAME_TO_ID.put("sculk_sensor", BlockID.SCULK_SENSOR);
        BLOCK_NAME_TO_ID.put("reinforced_deepslate", BlockID.REINFORCED_DEEPSLATE);

        // Village & Structure details
        BLOCK_NAME_TO_ID.put("dirt_path", BlockID.GRASS_PATH);
        BLOCK_NAME_TO_ID.put("grass_path", BlockID.GRASS_PATH);
        BLOCK_NAME_TO_ID.put("farmland", BlockID.FARMLAND);
        BLOCK_NAME_TO_ID.put("wheat", BlockID.WHEAT_BLOCK);
        BLOCK_NAME_TO_ID.put("carrots", BlockID.CARROT_BLOCK);
        BLOCK_NAME_TO_ID.put("potatoes", BlockID.POTATO_BLOCK);
        BLOCK_NAME_TO_ID.put("water", BlockID.STILL_WATER);
        BLOCK_NAME_TO_ID.put("lava", BlockID.MAGMA);
        BLOCK_NAME_TO_ID.put("flowing_lava", BlockID.MAGMA);
        BLOCK_NAME_TO_ID.put("oak_stairs", BlockID.OAK_WOOD_STAIRS);
        BLOCK_NAME_TO_ID.put("cobblestone_stairs", BlockID.COBBLESTONE_STAIRS);
        BLOCK_NAME_TO_ID.put("stone_stairs", BlockID.COBBLESTONE_STAIRS);
        BLOCK_NAME_TO_ID.put("stone_brick_stairs", BlockID.STONE_BRICK_STAIRS);
        BLOCK_NAME_TO_ID.put("spruce_stairs", BlockID.SPRUCE_WOOD_STAIRS);
        BLOCK_NAME_TO_ID.put("birch_stairs", BlockID.BIRCH_WOOD_STAIRS);
        BLOCK_NAME_TO_ID.put("jungle_stairs", BlockID.JUNGLE_WOOD_STAIRS);
        BLOCK_NAME_TO_ID.put("acacia_stairs", BlockID.ACACIA_WOOD_STAIRS);
        BLOCK_NAME_TO_ID.put("dark_oak_stairs", BlockID.DARK_OAK_WOOD_STAIRS);
        BLOCK_NAME_TO_ID.put("oak_slab", BlockID.OAK_SLAB);
        BLOCK_NAME_TO_ID.put("stone_slab", BlockID.SMOOTH_STONE_SLAB);
        BLOCK_NAME_TO_ID.put("cobblestone_wall", BlockID.COBBLE_WALL);
        BLOCK_NAME_TO_ID.put("oak_fence", BlockID.OAK_FENCE);
        BLOCK_NAME_TO_ID.put("fence", BlockID.OAK_FENCE);
        BLOCK_NAME_TO_ID.put("oak_door", BlockID.DOOR_BLOCK);
        BLOCK_NAME_TO_ID.put("door", BlockID.DOOR_BLOCK);
        BLOCK_NAME_TO_ID.put("iron_door", BlockID.IRON_DOOR_BLOCK);
        BLOCK_NAME_TO_ID.put("spruce_door", BlockID.SPRUCE_DOOR_BLOCK);
        BLOCK_NAME_TO_ID.put("birch_door", BlockID.BIRCH_DOOR_BLOCK);
        BLOCK_NAME_TO_ID.put("jungle_door", BlockID.JUNGLE_DOOR_BLOCK);
        BLOCK_NAME_TO_ID.put("acacia_door", BlockID.ACACIA_DOOR_BLOCK);
        BLOCK_NAME_TO_ID.put("dark_oak_door", BlockID.DARK_OAK_DOOR_BLOCK);
        BLOCK_NAME_TO_ID.put("oak_trapdoor", BlockID.TRAPDOOR);
        BLOCK_NAME_TO_ID.put("trapdoor", BlockID.TRAPDOOR);
        BLOCK_NAME_TO_ID.put("torch", BlockID.TORCH);
        BLOCK_NAME_TO_ID.put("wall_torch", BlockID.TORCH);
        BLOCK_NAME_TO_ID.put("wall_sign", BlockID.WALL_SIGN);
        BLOCK_NAME_TO_ID.put("standing_sign", BlockID.SIGN_POST);
        BLOCK_NAME_TO_ID.put("lantern", BlockID.LANTERN);
        BLOCK_NAME_TO_ID.put("hay_block", BlockID.HAY_BALE);
        BLOCK_NAME_TO_ID.put("hay_bale", BlockID.HAY_BALE);
        BLOCK_NAME_TO_ID.put("carpet", BlockID.CARPET);
        BLOCK_NAME_TO_ID.put("white_carpet", BlockID.CARPET);
        BLOCK_NAME_TO_ID.put("yellow_carpet", BlockID.CARPET);
        BLOCK_NAME_TO_ID.put("red_carpet", BlockID.CARPET);
        BLOCK_NAME_TO_ID.put("wool", BlockID.WOOL);
        BLOCK_NAME_TO_ID.put("white_wool", BlockID.WOOL);
        BLOCK_NAME_TO_ID.put("yellow_wool", BlockID.WOOL);
        BLOCK_NAME_TO_ID.put("red_wool", BlockID.WOOL);
        BLOCK_NAME_TO_ID.put("glass_pane", BlockID.GLASS_PANE);
        BLOCK_NAME_TO_ID.put("iron_bars", BlockID.IRON_BARS);
        BLOCK_NAME_TO_ID.put("smooth_stone", BlockID.SMOOTH_STONE);

        BLOCK_NAME_TO_ID.put("stone", BlockID.STONE);
        BLOCK_NAME_TO_ID.put("grass_block", BlockID.GRASS);
        BLOCK_NAME_TO_ID.put("grass", BlockID.GRASS);
        BLOCK_NAME_TO_ID.put("dirt", BlockID.DIRT);
        BLOCK_NAME_TO_ID.put("cobblestone", BlockID.COBBLESTONE);
        BLOCK_NAME_TO_ID.put("mossy_cobblestone", BlockID.MOSS_STONE);
        BLOCK_NAME_TO_ID.put("stone_bricks", BlockID.STONE_BRICKS);
        BLOCK_NAME_TO_ID.put("stonebrick", BlockID.STONE_BRICKS);
        BLOCK_NAME_TO_ID.put("mossy_stone_bricks", BlockID.STONE_BRICK);
        BLOCK_NAME_TO_ID.put("cracked_stone_bricks", BlockID.STONE_BRICK);
        BLOCK_NAME_TO_ID.put("planks", BlockID.PLANKS);
        BLOCK_NAME_TO_ID.put("oak_planks", BlockID.PLANKS);
        BLOCK_NAME_TO_ID.put("spruce_planks", BlockID.PLANKS);
        BLOCK_NAME_TO_ID.put("birch_planks", BlockID.PLANKS);
        BLOCK_NAME_TO_ID.put("log", BlockID.OAK_LOG);
        BLOCK_NAME_TO_ID.put("oak_log", BlockID.OAK_LOG);
        BLOCK_NAME_TO_ID.put("wood", BlockID.OAK_LOG);
        BLOCK_NAME_TO_ID.put("leaves", BlockID.LEAVES);
        BLOCK_NAME_TO_ID.put("glass", BlockID.GLASS);
        BLOCK_NAME_TO_ID.put("sandstone", BlockID.SANDSTONE);
        BLOCK_NAME_TO_ID.put("sand", BlockID.SAND);
        BLOCK_NAME_TO_ID.put("gravel", BlockID.GRAVEL);
        BLOCK_NAME_TO_ID.put("iron_block", BlockID.IRON_BLOCK);
        BLOCK_NAME_TO_ID.put("diamond_block", BlockID.DIAMOND_BLOCK);
        BLOCK_NAME_TO_ID.put("brick_block", BlockID.BRICKS);
        BLOCK_NAME_TO_ID.put("bricks", BlockID.BRICKS);
        BLOCK_NAME_TO_ID.put("bookshelf", BlockID.BOOKSHELF);
        BLOCK_NAME_TO_ID.put("obsidian", BlockID.OBSIDIAN);
        BLOCK_NAME_TO_ID.put("chest", BlockID.CHEST);
        BLOCK_NAME_TO_ID.put("trapped_chest", BlockID.TRAPPED_CHEST);
        BLOCK_NAME_TO_ID.put("barrel", BlockID.BARREL);
        BLOCK_NAME_TO_ID.put("crafting_table", BlockID.WORKBENCH);
        BLOCK_NAME_TO_ID.put("furnace", BlockID.FURNACE);
        BLOCK_NAME_TO_ID.put("ladder", BlockID.LADDER);
        BLOCK_NAME_TO_ID.put("purpur_block", BlockID.PURPUR_BLOCK);
        BLOCK_NAME_TO_ID.put("end_stone", BlockID.END_STONE);
    }

    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private final List<BlockState> palette = new ArrayList<>();
    private final List<StructureBlock> blocks = new ArrayList<>();

    public int getSizeX() { return sizeX; }
    public int getSizeY() { return sizeY; }
    public int getSizeZ() { return sizeZ; }
    public List<StructureBlock> getBlocks() { return blocks; }

    public static NBTStructure load(File file) {
        if (!file.exists()) return null;
        try (InputStream in = new FileInputStream(file)) {
            return loadFromStream(in);
        } catch (Exception e) {
            log.error("Failed to load structure file: " + file.getName(), e);
            return null;
        }
    }

    public static NBTStructure loadFromStream(InputStream in) {
        if (in == null) return null;
        try {
            byte[] data = in.readAllBytes();
            if (data.length < 5) return null;

            CompoundTag nbt = null;

            try {
                nbt = NBTIO.readCompressed(new ByteArrayInputStream(data), ByteOrder.BIG_ENDIAN);
            } catch (Exception ignored) {}

            if (nbt == null) {
                try {
                    nbt = NBTIO.readCompressed(new ByteArrayInputStream(data), ByteOrder.LITTLE_ENDIAN);
                } catch (Exception ignored) {}
            }

            if (nbt == null) {
                try {
                    nbt = NBTIO.read(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(data))), ByteOrder.BIG_ENDIAN);
                } catch (Exception ignored) {}
            }

            if (nbt == null) {
                try {
                    nbt = NBTIO.read(new BufferedInputStream(new ByteArrayInputStream(data)), ByteOrder.BIG_ENDIAN);
                } catch (Exception ignored) {}
            }

            if (nbt == null) {
                try {
                    nbt = NBTIO.read(new BufferedInputStream(new ByteArrayInputStream(data)), ByteOrder.LITTLE_ENDIAN);
                } catch (Exception ignored) {}
            }

            if (nbt == null) {
                return null;
            }

            NBTStructure structure = new NBTStructure();

            // 1. SIZE PARSING (Java & Bedrock formats)
            ListTag<?> sizeTag = null;
            if (nbt.contains("size")) {
                sizeTag = nbt.getList("size");
            } else if (nbt.contains("structure")) {
                CompoundTag sTag = nbt.getCompound("structure");
                if (sTag != null && sTag.contains("size")) {
                    sizeTag = sTag.getList("size");
                }
            }

            if (sizeTag != null && sizeTag.size() >= 3) {
                structure.sizeX = getTagIntValue(sizeTag.get(0));
                structure.sizeY = getTagIntValue(sizeTag.get(1));
                structure.sizeZ = getTagIntValue(sizeTag.get(2));
            }

            // 2. PALETTE PARSING (Java & Bedrock formats)
            ListTag<?> paletteTag = null;
            if (nbt.contains("palette")) {
                Tag t = nbt.get("palette");
                if (t instanceof ListTag<?> list) paletteTag = list;
            } else if (nbt.contains("palettes")) {
                ListTag<?> palettes = nbt.getList("palettes");
                if (palettes != null && !palettes.isEmpty() && palettes.get(0) instanceof ListTag<?> list) {
                    paletteTag = list;
                }
            } else if (nbt.contains("block_palette")) {
                paletteTag = nbt.getList("block_palette");
            }

            if (paletteTag == null && nbt.contains("structure")) {
                CompoundTag sTag = nbt.getCompound("structure");
                if (sTag != null && sTag.contains("palette")) {
                    CompoundTag palObj = sTag.getCompound("palette");
                    if (palObj != null && palObj.contains("default")) {
                        CompoundTag defObj = palObj.getCompound("default");
                        if (defObj != null && defObj.contains("block_palette")) {
                            paletteTag = defObj.getList("block_palette");
                        }
                    }
                }
            }

            if (paletteTag != null) {
                for (int i = 0; i < paletteTag.size(); i++) {
                    Tag t = paletteTag.get(i);
                    if (t instanceof CompoundTag entry) {
                        String name = entry.getString("Name");
                        if (name == null || name.isEmpty()) {
                            name = entry.getString("name");
                        }
                        structure.palette.add(parseBlockState(name, entry));
                    }
                }
            }

            // 3. JAVA BLOCKS ARRAY ("blocks")
            if (nbt.contains("blocks")) {
                ListTag<?> blocksTag = nbt.getList("blocks");
                if (blocksTag != null) {
                    for (int i = 0; i < blocksTag.size(); i++) {
                        Tag t = blocksTag.get(i);
                        if (t instanceof CompoundTag bEntry) {
                            ListTag<?> pos = bEntry.getList("pos");
                            int stateIndex = bEntry.contains("state") ? getTagIntValue(bEntry.get("state")) : 0;
                            if (pos != null && pos.size() >= 3) {
                                int bx = getTagIntValue(pos.get(0));
                                int by = getTagIntValue(pos.get(1));
                                int bz = getTagIntValue(pos.get(2));
                                structure.blocks.add(new StructureBlock(bx, by, bz, stateIndex));
                            }
                        }
                    }
                }
            }

            // 4. BEDROCK BLOCK INDICES ARRAY ("block_indices")
            if (structure.blocks.isEmpty()) {
                ListTag<?> blockIndicesTag = null;
                if (nbt.contains("block_indices")) {
                    blockIndicesTag = nbt.getList("block_indices");
                } else if (nbt.contains("structure")) {
                    CompoundTag sTag = nbt.getCompound("structure");
                    if (sTag != null && sTag.contains("block_indices")) {
                        blockIndicesTag = sTag.getList("block_indices");
                    }
                }

                if (blockIndicesTag != null && !blockIndicesTag.isEmpty() && blockIndicesTag.get(0) instanceof ListTag<?> layer0) {
                    int sx = structure.sizeX;
                    int sy = structure.sizeY;
                    int sz = structure.sizeZ;

                    if (sx > 0 && sy > 0 && sz > 0 && layer0.size() >= sx * sy * sz) {
                        for (int x = 0; x < sx; x++) {
                            for (int y = 0; y < sy; y++) {
                                for (int z = 0; z < sz; z++) {
                                    int index = (x * sy + y) * sz + z;
                                    if (index < layer0.size()) {
                                        int stateIndex = getTagIntValue(layer0.get(index));
                                        if (stateIndex >= 0 && stateIndex < structure.palette.size()) {
                                            structure.blocks.add(new StructureBlock(x, y, z, stateIndex));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if ((structure.sizeX <= 0 || structure.sizeZ <= 0) && !structure.blocks.isEmpty()) {
                int maxX = 0;
                int maxY = 0;
                int maxZ = 0;
                for (StructureBlock b : structure.blocks) {
                    if (b.x > maxX) maxX = b.x;
                    if (b.y > maxY) maxY = b.y;
                    if (b.z > maxZ) maxZ = b.z;
                }
                structure.sizeX = maxX + 1;
                structure.sizeY = maxY + 1;
                structure.sizeZ = maxZ + 1;
            }

            return structure;
        } catch (Exception e) {
            log.error("Failed to parse NBT structure stream", e);
            return null;
        }
    }

    private static int getTagIntValue(Tag tag) {
        if (tag == null) return 0;
        Object val = tag.parseValue();
        if (val instanceof Number num) {
            return num.intValue();
        }
        return 0;
    }

    private static boolean isDoorBlock(int id) {
        return id == BlockID.DOOR_BLOCK || id == BlockID.WOODEN_DOOR_BLOCK || id == BlockID.IRON_DOOR_BLOCK ||
               id == BlockID.SPRUCE_DOOR_BLOCK || id == BlockID.BIRCH_DOOR_BLOCK || id == BlockID.JUNGLE_DOOR_BLOCK ||
               id == BlockID.ACACIA_DOOR_BLOCK || id == BlockID.DARK_OAK_DOOR_BLOCK;
    }

    private static BlockState parseBlockState(String rawName, CompoundTag tag) {
        if (rawName == null || rawName.isEmpty()) return new BlockState(BlockID.AIR, 0);

        String name = rawName.toLowerCase();
        if (name.startsWith("minecraft:")) {
            name = name.substring("minecraft:".length());
        }

        // 1. Technical air checks MUST COME FIRST
        if (name.contains("structure_void") || name.contains("structure_block") || name.equals("air") || name.contains("barrier") || name.contains("jigsaw")) {
            return new BlockState(BlockID.AIR, 0);
        }

        int calculatedMeta = 0;
        if (tag != null) {
            CompoundTag states = tag.getCompound("states");
            if (states == null) states = tag.getCompound("Properties");
            if (states != null) {
                // Trapdoors
                if (name.contains("trapdoor")) {
                    String half = states.getString("half");
                    if ("top".equalsIgnoreCase(half) || "upper".equalsIgnoreCase(half)) calculatedMeta |= 8;
                    String open = states.getString("open");
                    if ("true".equalsIgnoreCase(open) || "1".equals(open)) calculatedMeta |= 4;
                    String facing = states.getString("facing");
                    if ("east".equalsIgnoreCase(facing)) calculatedMeta |= 2;
                    else if ("west".equalsIgnoreCase(facing)) calculatedMeta |= 3;
                    else if ("north".equalsIgnoreCase(facing)) calculatedMeta |= 1;
                }
            }
        }

        // 2. Direct map lookup
        Integer directId = BLOCK_NAME_TO_ID.get(name);
        if (directId != null) {
            return new BlockState(directId, calculatedMeta);
        }

        // 3. Dynamic Nukkit Block registry lookup
        try {
            BlockType type = BlockTypes.get(name);
            if (type == null) {
                type = BlockTypes.get("minecraft:" + name);
            }
            if (type != null) {
                Block b = type.createBlock();
                if (b != null) {
                    return new BlockState(b.getId(), calculatedMeta != 0 ? calculatedMeta : b.getDamage());
                }
            }
        } catch (Exception ignored) {}

        // 4. Safe specific fallbacks
        if (name.contains("wall_torch") || name.contains("torch")) return new BlockState(BlockID.TORCH, 0);
        if (name.contains("wall_sign") || name.contains("sign")) return new BlockState(BlockID.WALL_SIGN, 0);
        if (name.contains("stairs")) return new BlockState(BlockID.OAK_WOOD_STAIRS, 0);
        if (name.contains("slab")) return new BlockState(BlockID.OAK_SLAB, 0);
        if (name.contains("fence")) return new BlockState(BlockID.OAK_FENCE, 0);
        if (name.contains("cobblestone_wall") || name.contains("cobble_wall") || name.contains("blackstone_wall")) return new BlockState(BlockID.COBBLE_WALL, 0);
        if (name.contains("door")) return new BlockState(BlockID.DOOR_BLOCK, calculatedMeta);
        if (name.contains("light") || name.contains("lantern")) return new BlockState(BlockID.TORCH, 0);
        if (name.contains("path")) return new BlockState(BlockID.GRASS_PATH, 0);
        if (name.contains("lava")) return new BlockState(BlockID.MAGMA, 0);
        if (name.contains("water")) return new BlockState(BlockID.STILL_WATER, 0);

        if (name.contains("gilded_blackstone")) return new BlockState(BlockID.GILDED_BLACKSTONE, 0);
        if (name.contains("blackstone")) return new BlockState(BlockID.BLACKSTONE, 0);
        if (name.contains("basalt")) return new BlockState(BlockID.BASALT, 0);
        if (name.contains("obsidian")) return new BlockState(BlockID.OBSIDIAN, 0);
        if (name.contains("sculk")) return new BlockState(BlockID.SCULK, 0);
        if (name.contains("deepslate")) return new BlockState(BlockID.DEEPSLATE, 0);
        if (name.contains("nether")) return new BlockState(BlockID.NETHER_BRICK_BLOCK, 0);
        if (name.contains("soul")) return new BlockState(BlockID.SOUL_SOIL, 0);
        if (name.contains("magma")) return new BlockState(BlockID.MAGMA, 0);
        if (name.contains("chest")) return new BlockState(BlockID.CHEST, 0);
        if (name.contains("grass")) return new BlockState(BlockID.GRASS, 0);
        if (name.contains("dirt")) return new BlockState(BlockID.DIRT, 0);
        if (name.contains("cobble")) return new BlockState(BlockID.COBBLESTONE, 0);
        if (name.contains("brick")) return new BlockState(BlockID.BRICKS, 0);
        if (name.contains("stone")) return new BlockState(BlockID.STONE, 0);
        if (name.contains("planks")) return new BlockState(BlockID.PLANKS, 0);
        if (name.contains("log") || name.contains("wood")) return new BlockState(BlockID.OAK_LOG, 0);
        if (name.contains("leaves")) return new BlockState(BlockID.LEAVES, 0);
        if (name.contains("glass")) return new BlockState(BlockID.GLASS, 0);

        return new BlockState(BlockID.COBBLESTONE, 0);
    }

    public void place(ChunkManager level, int startX, int startY, int startZ) {
        place(level, "common", startX, startY, startZ);
    }

    public void place(ChunkManager level, String category, int startX, int startY, int startZ) {
        if (blocks.isEmpty() || palette.isEmpty()) return;

        Level actualLevel = null;
        if (level instanceof Level l) {
            actualLevel = l;
        } else {
            FullChunk ch = level.getChunk(startX >> 4, startZ >> 4);
            if (ch != null && ch.getProvider() != null) {
                actualLevel = ch.getProvider().getLevel();
            }
        }

        List<PlacedBlockRecord> placedRecords = new ArrayList<>();

        // Fast O(1) spatial grid for instant block-below queries (e.g. door upper half check)
        StructureBlock[][][] spatialGrid = null;
        if (sizeX > 0 && sizeY > 0 && sizeZ > 0 && sizeX <= 256 && sizeY <= 256 && sizeZ <= 256) {
            spatialGrid = new StructureBlock[sizeX][sizeY][sizeZ];
            for (StructureBlock b : blocks) {
                if (b.x >= 0 && b.x < sizeX && b.y >= 0 && b.y < sizeY && b.z >= 0 && b.z < sizeZ) {
                    spatialGrid[b.x][b.y][b.z] = b;
                }
            }
        }

        // 1. Fill foundation downwards so structure houses never float in the air
        for (StructureBlock b : blocks) {
            if (b.y == 0 && b.stateIndex >= 0 && b.stateIndex < palette.size()) {
                BlockState state = palette.get(b.stateIndex);
                if (state.id != BlockID.AIR) {
                    int px = startX + b.x;
                    int pz = startZ + b.z;
                    for (int fy = startY - 1; fy >= startY - 12; fy--) {
                        int cur = level.getBlockIdAt(px, fy, pz);
                        if (cur == BlockID.AIR || cur == BlockID.STILL_WATER || cur == BlockID.WATER || cur == BlockID.LEAVES || cur == BlockID.TALL_GRASS) {
                            if (actualLevel != null) {
                                actualLevel.setBlock(new Vector3(px, fy, pz), Block.get(BlockID.DIRT, 0), false, false);
                            } else {
                                level.setBlockAt(px, fy, pz, BlockID.DIRT, 0);
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        // 2. Set structure blocks in chunk memory with ZERO individual packet sends (directUpdate=false, sendUpdate=false)
        for (StructureBlock b : blocks) {
            if (b.stateIndex >= 0 && b.stateIndex < palette.size()) {
                BlockState state = palette.get(b.stateIndex);
                if (state.id != BlockID.AIR) {
                    int px = startX + b.x;
                    int py = startY + b.y;
                    int pz = startZ + b.z;

                    int targetMeta = state.meta;

                    // Fast O(1) check for 2-block door upper half meta bit (0x8)
                    if (isDoorBlock(state.id) && b.y > 0) {
                        StructureBlock below = null;
                        if (spatialGrid != null && b.x < sizeX && b.z < sizeZ && (b.y - 1) < sizeY) {
                            below = spatialGrid[b.x][b.y - 1][b.z];
                        } else {
                            for (StructureBlock other : blocks) {
                                if (other.x == b.x && other.z == b.z && other.y == b.y - 1) {
                                    below = other;
                                    break;
                                }
                            }
                        }
                        if (below != null && below.stateIndex >= 0 && below.stateIndex < palette.size()) {
                            BlockState belowState = palette.get(below.stateIndex);
                            if (belowState != null && isDoorBlock(belowState.id)) {
                                targetMeta |= 8;
                            }
                        }
                    }

                    // Map lava safely to MAGMA
                    int targetId = (state.id == BlockID.LAVA || state.id == BlockID.STILL_LAVA) ? BlockID.MAGMA : state.id;

                    if (actualLevel != null) {
                        actualLevel.setBlock(new Vector3(px, py, pz), Block.get(targetId, targetMeta), false, false);
                    } else {
                        level.setBlockAt(px, py, pz, targetId, targetMeta);
                    }

                    placedRecords.add(new PlacedBlockRecord(px, py, pz, targetId, targetMeta));

                    if (targetId == BlockID.CHEST || targetId == BlockID.TRAPPED_CHEST || targetId == BlockID.BARREL) {
                        LootPopulator.populateChest(level, px, py, pz);
                    }
                }
            }
        }

        // 3. Ultra-fast subchunk block update batching using UpdateSubChunkBlocksPacket!
        if (actualLevel != null && !placedRecords.isEmpty()) {
            Map<SubChunkKey, List<PlacedBlockRecord>> subChunkMap = new HashMap<>();
            for (PlacedBlockRecord rec : placedRecords) {
                int scx = rec.x >> 4;
                int scy = rec.y >> 4;
                int scz = rec.z >> 4;
                SubChunkKey key = new SubChunkKey(scx, scy, scz);
                subChunkMap.computeIfAbsent(key, k -> new ArrayList<>()).add(rec);
            }

            for (Map.Entry<SubChunkKey, List<PlacedBlockRecord>> entry : subChunkMap.entrySet()) {
                SubChunkKey key = entry.getKey();
                List<PlacedBlockRecord> recList = entry.getValue();

                Map<?, Player> players = actualLevel.getChunkPlayers(key.cx, key.cz);
                if (players == null || players.isEmpty()) continue;

                Map<Integer, List<Player>> playersByProtocol = new HashMap<>();
                for (Player player : players.values()) {
                    if (player != null && player.isOnline()) {
                        playersByProtocol.computeIfAbsent(player.protocol, p -> new ArrayList<>()).add(player);
                    }
                }

                for (Map.Entry<Integer, List<Player>> protoEntry : playersByProtocol.entrySet()) {
                    int protocol = protoEntry.getKey();
                    List<Player> targetPlayers = protoEntry.getValue();

                    UpdateSubChunkBlocksPacket packet = new UpdateSubChunkBlocksPacket();
                    packet.position = new BlockVector3(key.cx << 4, key.cy << 4, key.cz << 4);

                    for (PlacedBlockRecord rec : recList) {
                        int runtimeId = GlobalBlockPalette.getOrCreateRuntimeId(protocol, rec.id, rec.meta);
                        packet.standardBlocks.add(new BlockChangeEntry(
                                new BlockVector3(rec.x & 0x0f, rec.y & 0x0f, rec.z & 0x0f),
                                runtimeId,
                                0,
                                0,
                                BlockChangeEntry.MessageType.NONE
                        ));
                    }

                    for (Player player : targetPlayers) {
                        player.dataPacket(packet);
                    }
                }
            }
        }

        MobPopulator.spawnStructureMobs(level, category, startX, startY, startZ);
    }
}
