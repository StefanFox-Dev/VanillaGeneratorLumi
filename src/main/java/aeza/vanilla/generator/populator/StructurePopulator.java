package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import aeza.vanilla.generator.structure.LootPopulator;
import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class StructurePopulator extends Populator {

    private static final boolean[] SOLID_GROUND_BLOCKS = new boolean[1024];

    static {
        SOLID_GROUND_BLOCKS[BlockID.GRASS] = true;
        SOLID_GROUND_BLOCKS[BlockID.DIRT] = true;
        SOLID_GROUND_BLOCKS[BlockID.STONE] = true;
        SOLID_GROUND_BLOCKS[BlockID.SAND] = true;
        SOLID_GROUND_BLOCKS[BlockID.PODZOL] = true;
        SOLID_GROUND_BLOCKS[BlockID.DEEPSLATE] = true;
        SOLID_GROUND_BLOCKS[BlockID.SNOW_LAYER] = true;
    }

    private static boolean isSolidGround(int id) {
        return id >= 0 && id < 1024 && SOLID_GROUND_BLOCKS[id];
    }

    public StructurePopulator() {
        StructureManager.init();
    }

    private int findGroundY(FullChunk chunk, int x, int z) {
        for (int y = 319; y > -50; y--) {
            int id = chunk.getBlockId(x, y, z);
            if (id == BlockID.WATER || id == BlockID.STILL_WATER) {
                return 0; // Water surface is NOT valid ground for land structures!
            }
            if (isSolidGround(id)) {
                return y + 1;
            }
        }
        return 0;
    }

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        int surfaceY = findGroundY(chunk, 8, 8);
        int biome = chunk.getBiomeId(8, 8);
        String worldName = chunk.getProvider() != null && chunk.getProvider().getLevel() != null ? chunk.getProvider().getLevel().getName() : "survival";

        // 1. Village settlement generation (Full village of 10-14 houses & structures)
        if (surfaceY >= 62 && ((chunkX & 31) == 12) && ((chunkZ & 31) == 20)) {
            String villageType = getVillageCategory(biome);
            if (villageType != null) {
                NBTStructure center = StructureManager.getRandomStructure(villageType + "/town_centers", random);
                if (center == null) {
                    center = StructureManager.getRandomStructure(villageType + "/houses", random);
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

                        NBTStructure building = StructureManager.getRandomStructure(villageType + "/houses", random);
                        if (building == null) {
                            building = StructureManager.getRandomStructure(villageType, random);
                        }

                        if (building != null) {
                            building.place(world, villageType, hx, hy, hz);
                        }
                    }
                    return;
                }
            }
        }

        // 2. Underground Ancient City Generation (~every 32 chunks at Y = -51 deep underground in deepslate layer right above Bedrock -64)
        if (((chunkX & 31) == 16) && ((chunkZ & 31) == 16)) {
            AncientCityPopulator.generateAncientCity(world, random, baseX, -51, baseZ, worldName);
            return;
        }

        // 3. Woodland Mansion (~every 32 chunks in dark forest / roofed forest)
        if (surfaceY >= 62 && ((chunkX & 31) == 28) && ((chunkZ & 31) == 28)) {
            MansionPopulator.generateMansion(world, random, baseX, surfaceY, baseZ, worldName);
            return;
        }

        // 4. Trail Ruins (~every 22 chunks in Taigas, Birches & Old Growth Forests)
        if (surfaceY >= 62 && (biome == BiomeIds.TAIGA || biome == BiomeIds.MEGA_TAIGA || biome == BiomeIds.BIRCH_FOREST) && ((chunkX & 21) == 5) && ((chunkZ & 21) == 13)) {
            NBTStructure trailRuins = StructureManager.getRandomStructure("trail_ruins", random);
            if (trailRuins != null) {
                trailRuins.place(world, "trail_ruins", baseX + 2, Math.max(50, surfaceY - 6), baseZ + 2);
                StructureManager.registerGeneratedStructure(worldName, "trail_ruins", baseX + 2, Math.max(50, surfaceY - 6), baseZ + 2);
                return;
            }
        }

        // 5. Igloo (~every 20 chunks in cold / snow biomes)
        if (surfaceY >= 62 && (biome == BiomeIds.ICE_PLAINS || biome == BiomeIds.COLD_TAIGA) && ((chunkX & 19) == 9) && ((chunkZ & 19) == 9)) {
            NBTStructure top = StructureManager.getRandomStructure("igloo/igloo_top_trapdoor", random);
            if (top == null) top = StructureManager.getRandomStructure("igloo/igloo_top_no_trapdoor", random);
            if (top == null) top = StructureManager.getRandomStructure("igloo", random);

            if (top != null) {
                top.place(world, "igloo", baseX + 4, surfaceY, baseZ + 4);

                // Secret basement ladder shaft down
                NBTStructure mid = StructureManager.getRandomStructure("igloo/igloo_middle", random);
                if (mid != null) {
                    for (int depth = 3; depth <= 12; depth += 2) {
                        mid.place(world, "igloo", baseX + 4, surfaceY - depth, baseZ + 4);
                    }
                }

                // Secret underground laboratory room
                NBTStructure bottom = StructureManager.getRandomStructure("igloo/igloo_bottom", random);
                if (bottom != null) {
                    bottom.place(world, "igloo", baseX + 2, surfaceY - 14, baseZ + 2);
                }

                StructureManager.registerGeneratedStructure(worldName, "igloo", baseX + 4, surfaceY, baseZ + 4);
                return;
            }
        }

        // 6. Ruined Portal (~every 18 chunks)
        if (surfaceY >= 62 && ((chunkX & 17) == 4) && ((chunkZ & 17) == 10)) {
            NBTStructure portal = StructureManager.getRandomStructure("ruined_portal", random);
            if (portal != null) {
                portal.place(world, "ruined_portal", baseX + 4, surfaceY - 1, baseZ + 4);
                StructureManager.registerGeneratedStructure(worldName, "ruined_portal", baseX + 4, surfaceY - 1, baseZ + 4);
                return;
            }
        }

        // 7. Pillager Outpost (~every 28 chunks)
        if (surfaceY >= 62 && ((chunkX & 27) == 7) && ((chunkZ & 27) == 15)) {
            NBTStructure watchtower = StructureManager.getStructure("pillageroutpost/watchtower");
            if (watchtower == null) watchtower = StructureManager.getStructure("pillageroutpost/watchtower_overgrown");

            if (watchtower != null) {
                watchtower.place(world, "pillageroutpost_main", baseX + 2, surfaceY, baseZ + 2);

                // Surrounding outpost features
                NBTStructure tent = StructureManager.getRandomStructure("pillageroutpost/feature_tent", random);
                if (tent != null) tent.place(world, "pillageroutpost_piece", baseX + 16, surfaceY, baseZ + 2);

                NBTStructure cage = StructureManager.getRandomStructure("pillageroutpost/feature_cage", random);
                if (cage != null) cage.place(world, "pillageroutpost_piece", baseX - 12, surfaceY, baseZ + 2);

                NBTStructure targets = StructureManager.getStructure("pillageroutpost/feature_targets");
                if (targets != null) targets.place(world, "pillageroutpost_piece", baseX + 2, surfaceY, baseZ + 16);

                StructureManager.registerGeneratedStructure(worldName, "pillageroutpost", baseX + 2, surfaceY, baseZ + 2);
                return;
            }
        }

        // 8. Overworld Fossils (~every 28 chunks underground in Deserts & Swamps)
        if ((biome == BiomeIds.DESERT || biome == BiomeIds.SWAMPLAND) && ((chunkX & 27) == 3) && ((chunkZ & 27) == 19)) {
            NBTStructure fossil = StructureManager.getRandomStructure("fossils", random);
            if (fossil != null) {
                int fossilY = -30 + random.nextInt(25);
                fossil.place(world, "fossils", baseX + 2, fossilY, baseZ + 2);
                StructureManager.registerGeneratedStructure(worldName, "fossils", baseX + 2, fossilY, baseZ + 2);
                return;
            }
        }

        // 9. Coral Crust / Ocean Ruins / Shipwreck (~every 16 chunks in oceans)
        if ((biome == BiomeIds.OCEAN || biome == BiomeIds.DEEP_OCEAN || biome == BiomeIds.FROZEN_OCEAN) && ((chunkX & 15) == 6) && ((chunkZ & 15) == 10)) {
            NBTStructure coral = StructureManager.getRandomStructure("coralcrust", random);
            if (coral == null) coral = StructureManager.getRandomStructure("shipwreck", random);
            if (coral == null) coral = StructureManager.getRandomStructure("ruin", random);

            if (coral != null) {
                int oceanY = chunk.getHighestBlockAt(8, 8);
                coral.place(world, "shipwreck", baseX + 2, Math.max(35, oceanY - 2), baseZ + 2);
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
