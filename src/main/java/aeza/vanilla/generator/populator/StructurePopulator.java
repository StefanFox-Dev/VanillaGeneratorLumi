package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import aeza.vanilla.generator.structure.LootPopulator;
import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.Random;
import java.util.SplittableRandom;

public class StructurePopulator extends Populator {

    public StructurePopulator() {
        StructureManager.init();
    }

    private int findGroundY(FullChunk chunk, int x, int z) {
        for (int y = 255; y > 0; y--) {
            int id = chunk.getBlockId(x, y, z);
            if (id == BlockID.WATER || id == BlockID.STILL_WATER) {
                return 0; // Water surface is NOT valid ground for land structures!
            }
            if (id == BlockID.GRASS || id == BlockID.DIRT || id == BlockID.STONE || id == BlockID.SAND || id == BlockID.PODZOL || id == BlockID.DEEPSLATE || id == BlockID.SNOW_LAYER) {
                return y + 1;
            }
        }
        return 0;
    }

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        Random javaRand = new Random(random.nextLong());
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        int surfaceY = findGroundY(chunk, 8, 8);
        int biome = chunk.getBiomeId(8, 8);
        String worldName = chunk.getProvider() != null && chunk.getProvider().getLevel() != null ? chunk.getProvider().getLevel().getName() : "survival";

        // 1. Village settlement generation (Full village of 10-14 houses & structures)
        if (surfaceY >= 62 && ((chunkX & 31) == 12) && ((chunkZ & 31) == 20)) {
            String villageType = getVillageCategory(biome);
            if (villageType != null) {
                NBTStructure center = StructureManager.getRandomStructure(villageType + "/town_centers", javaRand);
                if (center == null) {
                    center = StructureManager.getRandomStructure(villageType + "/houses", javaRand);
                }

                if (center != null) {
                    center.place(world, villageType, baseX, surfaceY, baseZ);
                    StructureManager.registerGeneratedStructure(worldName, villageType, baseX, surfaceY, baseZ);

                    int[][] offsets = new int[][] {
                        {-18, -18}, {0, -20}, {18, -18},
                        {-22, 0},             {22, 0},
                        {-18, 18},  {0, 22},  {18, 18},
                        {-34, -10}, {34, 10}, {-10, -34}, {10, 34}
                    };

                    for (int[] off : offsets) {
                        int hx = baseX + off[0];
                        int hz = baseZ + off[1];

                        int localX = Math.max(0, Math.min(15, hx - baseX));
                        int localZ = Math.max(0, Math.min(15, hz - baseZ));
                        int hy = findGroundY(chunk, localX, localZ);
                        if (hy < 60) hy = surfaceY;

                        NBTStructure building = StructureManager.getRandomStructure(villageType + "/houses", javaRand);
                        if (building == null) {
                            building = StructureManager.getRandomStructure(villageType, javaRand);
                        }

                        if (building != null) {
                            building.place(world, villageType, hx, hy, hz);
                        }
                    }
                    return;
                }
            }
        }

        // 2. Underground Ancient City Generation (~every 32 chunks deep underground)
        if (((chunkX & 31) == 16) && ((chunkZ & 31) == 16)) {
            int ancientY = 22; // Deep underground in deepslate layer
            NBTStructure center = StructureManager.getRandomStructure("ancient_city/city_center", javaRand);
            if (center == null) center = StructureManager.getRandomStructure("ancient_city", javaRand);

            if (center != null) {
                center.place(world, "ancient_city", baseX, ancientY, baseZ);

                int[][] offsets = new int[][] {
                    {-24, -24}, {0, -28}, {24, -24},
                    {-28, 0},             {28, 0},
                    {-24, 24},  {0, 28},  {24, 24}
                };

                for (int[] off : offsets) {
                    NBTStructure sub = StructureManager.getRandomStructure("ancient_city/city", javaRand);
                    if (sub == null) sub = StructureManager.getRandomStructure("ancient_city/structures", javaRand);
                    if (sub == null) sub = StructureManager.getRandomStructure("ancient_city/walls", javaRand);
                    if (sub != null) {
                        sub.place(world, "ancient_city", baseX + off[0], ancientY, baseZ + off[1]);
                    }
                }

                // Populate Ancient City chests with Echo Shards and Disc 5
                for (int dx = -30; dx <= 30; dx += 2) {
                    for (int dy = 0; dy <= 20; dy++) {
                        for (int dz = -30; dz <= 30; dz += 2) {
                            int bx = baseX + dx;
                            int by = ancientY + dy;
                            int bz = baseZ + dz;
                            int id = world.getBlockIdAt(bx, by, bz);
                            if (id == BlockID.CHEST || id == BlockID.TRAPPED_CHEST || id == BlockID.BARREL) {
                                LootPopulator.populateAncientCityChest(world, bx, by, bz);
                            }
                        }
                    }
                }

                StructureManager.registerGeneratedStructure(worldName, "ancient_city", baseX, ancientY, baseZ);
                return;
            }
        }

        // 3. Woodland Mansion (~every 32 chunks in dark forest / roofed forest)
        if (surfaceY >= 62 && ((chunkX & 31) == 28) && ((chunkZ & 31) == 28)) {
            NBTStructure entrance = StructureManager.getRandomStructure("mansion/entrance", javaRand);
            if (entrance == null) entrance = StructureManager.getRandomStructure("mansion", javaRand);

            if (entrance != null) {
                entrance.place(world, "mansion", baseX, surfaceY, baseZ);

                int[][] roomOffsets = new int[][] {
                    {-16, 0}, {16, 0}, {-16, 16}, {16, 16},
                    {-32, 0}, {32, 0}, {-32, 16}, {32, 16},
                    {-16, -16}, {16, -16}, {0, -16}, {0, 16}
                };

                for (int[] off : roomOffsets) {
                    NBTStructure room = StructureManager.getRandomStructure("mansion", javaRand);
                    if (room != null) {
                        room.place(world, "mansion", baseX + off[0], surfaceY, baseZ + off[1]);
                        NBTStructure room2 = StructureManager.getRandomStructure("mansion", javaRand);
                        if (room2 != null) {
                            room2.place(world, "mansion", baseX + off[0], surfaceY + 7, baseZ + off[1]);
                        }
                    }
                }
                StructureManager.registerGeneratedStructure(worldName, "mansion", baseX, surfaceY, baseZ);
                return;
            }
        }

        // 4. Igloo (~every 20 chunks in cold / snow biomes)
        if (surfaceY >= 62 && (biome == BiomeIds.ICE_PLAINS || biome == BiomeIds.COLD_TAIGA) && ((chunkX & 19) == 9) && ((chunkZ & 19) == 9)) {
            NBTStructure top = StructureManager.getRandomStructure("igloo/igloo_top_trapdoor", javaRand);
            if (top == null) top = StructureManager.getRandomStructure("igloo/igloo_top_no_trapdoor", javaRand);
            if (top == null) top = StructureManager.getRandomStructure("igloo", javaRand);

            if (top != null) {
                top.place(world, "igloo", baseX + 4, surfaceY, baseZ + 4);

                // Secret basement ladder shaft down
                NBTStructure mid = StructureManager.getRandomStructure("igloo/igloo_middle", javaRand);
                if (mid != null) {
                    for (int depth = 3; depth <= 12; depth += 2) {
                        mid.place(world, "igloo", baseX + 4, surfaceY - depth, baseZ + 4);
                    }
                }

                // Secret underground laboratory room
                NBTStructure bottom = StructureManager.getRandomStructure("igloo/igloo_bottom", javaRand);
                if (bottom != null) {
                    bottom.place(world, "igloo", baseX + 2, surfaceY - 14, baseZ + 2);
                }

                StructureManager.registerGeneratedStructure(worldName, "igloo", baseX + 4, surfaceY, baseZ + 4);
                return;
            }
        }

        // 5. Ruined Portal (~every 18 chunks)
        if (surfaceY >= 62 && ((chunkX & 17) == 4) && ((chunkZ & 17) == 10)) {
            NBTStructure portal = StructureManager.getRandomStructure("ruined_portal", javaRand);
            if (portal != null) {
                portal.place(world, "ruined_portal", baseX + 4, surfaceY - 1, baseZ + 4);
                StructureManager.registerGeneratedStructure(worldName, "ruined_portal", baseX + 4, surfaceY - 1, baseZ + 4);
                return;
            }
        }

        // 6. Pillager Outpost (~every 24 chunks)
        if (surfaceY >= 62 && ((chunkX & 23) == 7) && ((chunkZ & 23) == 15)) {
            NBTStructure outpost = StructureManager.getRandomStructure("pillageroutpost", javaRand);
            if (outpost != null) {
                outpost.place(world, "pillageroutpost", baseX + 2, surfaceY, baseZ + 2);
                StructureManager.registerGeneratedStructure(worldName, "pillageroutpost", baseX + 2, surfaceY, baseZ + 2);
                return;
            }
        }

        // 7. Shipwreck / Ruins (~every 16 chunks in oceans)
        if ((biome == BiomeIds.OCEAN || biome == BiomeIds.DEEP_OCEAN || biome == BiomeIds.FROZEN_OCEAN) && ((chunkX & 15) == 6) && ((chunkZ & 15) == 10)) {
            NBTStructure shipwreck = StructureManager.getRandomStructure("shipwreck", javaRand);
            if (shipwreck == null) {
                shipwreck = StructureManager.getRandomStructure("ruin", javaRand);
            }
            if (shipwreck != null) {
                int oceanY = chunk.getHighestBlockAt(8, 8);
                shipwreck.place(world, "shipwreck", baseX + 2, Math.max(35, oceanY - 2), baseZ + 2);
                StructureManager.registerGeneratedStructure(worldName, "shipwreck", baseX + 2, Math.max(35, oceanY - 2), baseZ + 2);
            }
        }
    }

    private String getVillageCategory(int biome) {
        return switch (biome) {
            case BiomeIds.PLAINS, BiomeIds.SUNFLOWER_PLAINS -> "village/plains";
            case BiomeIds.DESERT, BiomeIds.DESERT_HILLS, BiomeIds.DESERT_MUTATED -> "village/desert";
            case BiomeIds.SAVANNA, BiomeIds.SAVANNA_PLATEAU, BiomeIds.SAVANNA_MUTATED -> "village/savanna";
            case BiomeIds.TAIGA, BiomeIds.COLD_TAIGA, BiomeIds.MEGA_TAIGA -> "village/taiga";
            case BiomeIds.ICE_PLAINS, BiomeIds.ICE_PLAINS_SPIKES -> "village/snowy";
            default -> null;
        };
    }
}
