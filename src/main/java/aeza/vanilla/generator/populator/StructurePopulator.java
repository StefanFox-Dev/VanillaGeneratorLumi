package aeza.vanilla.generator.populator;

import aeza.vanilla.generator.biomegrid.BiomeIds;
import aeza.vanilla.generator.structure.NBTStructure;
import aeza.vanilla.generator.structure.StructureManager;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

import java.util.SplittableRandom;

public class StructurePopulator extends Populator {

    @Override
    public void populate(ChunkManager world, SplittableRandom random, int chunkX, int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        int biome = chunk.getBiomeId(8, 8);
        int surfaceY = findGroundY(chunk, 8, 8);

        String worldName = "world";
        if (chunk.getProvider() != null && chunk.getProvider().getLevel() != null) {
            worldName = chunk.getProvider().getLevel().getName();
        }

        // 1. Overworld Villages (1 per 32x32 chunk region)
        if (surfaceY >= 62 && isStructureChunk(chunkX, chunkZ, 32, 10387312L)) {
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
                        {-16, -16}, {0, -18}, {16, -16},
                        {-18, 0},             {18, 0},
                        {-16, 16},  {0, 18},  {16, 16}
                    };

                    for (int[] off : offsets) {
                        int hx = baseX + off[0];
                        int hz = baseZ + off[1];

                        int localX = Math.max(0, Math.min(15, hx - baseX));
                        int localZ = Math.max(0, Math.min(15, hz - baseZ));
                        int hy = findGroundY(chunk, localX, localZ);
                        if (hy < 60) hy = surfaceY;

                        NBTStructure building = StructureManager.getRandomStructure(villageType + "/houses", random);
                        if (building != null) {
                            building.place(world, villageType + "_piece", hx, hy, hz);
                        }
                    }
                    return;
                }
            }
        }

        // 2. Underground Ancient City Generation (1 per 64x64 chunk region at Y = -51)
        if (isStructureChunk(chunkX, chunkZ, 64, 200887312L)) {
            AncientCityPopulator.generateAncientCity(world, random, baseX, -51, baseZ, worldName);
            return;
        }

        // 3. Woodland Mansion (1 per 80x80 chunk region in Dark Forest)
        if (surfaceY >= 62 && biome == BiomeIds.ROOFED_FOREST && isStructureChunk(chunkX, chunkZ, 80, 10387313L)) {
            MansionPopulator.generateMansion(world, random, baseX, surfaceY, baseZ, worldName);
            return;
        }

        // 4. Pillager Outpost (1 per 64x64 chunk region in open land biomes)
        if (surfaceY >= 62 && isOutpostBiome(biome) && isStructureChunk(chunkX, chunkZ, 64, 165745295L)) {
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

        // 5. Ruined Portal (1 per 32x32 chunk region)
        if (surfaceY >= 62 && isStructureChunk(chunkX, chunkZ, 32, 40552149L)) {
            NBTStructure portal = StructureManager.getRandomStructure("ruined_portal", random);
            if (portal != null) {
                portal.place(world, "ruined_portal", baseX + 4, surfaceY - 1, baseZ + 4);
                StructureManager.registerGeneratedStructure(worldName, "ruined_portal", baseX + 4, surfaceY - 1, baseZ + 4);
                return;
            }
        }

        // 6. Igloo (1 per 32x32 chunk region in cold/snowy biomes)
        if (surfaceY >= 62 && (biome == BiomeIds.ICE_PLAINS || biome == BiomeIds.COLD_TAIGA) && isStructureChunk(chunkX, chunkZ, 32, 14357617L)) {
            NBTStructure top = StructureManager.getRandomStructure("igloo/igloo_top_trapdoor", random);
            if (top == null) top = StructureManager.getRandomStructure("igloo", random);

            if (top != null) {
                top.place(world, "igloo", baseX + 4, surfaceY, baseZ + 4);

                NBTStructure bottom = StructureManager.getRandomStructure("igloo/igloo_bottom", random);
                if (bottom != null) {
                    bottom.place(world, "igloo_piece", baseX + 2, surfaceY - 14, baseZ + 2);
                }

                StructureManager.registerGeneratedStructure(worldName, "igloo", baseX + 4, surfaceY, baseZ + 4);
                return;
            }
        }

        // 7. Trail Ruins (1 per 36x36 chunk region in Taigas & Old Growth)
        if (surfaceY >= 62 && (biome == BiomeIds.TAIGA || biome == BiomeIds.MEGA_TAIGA || biome == BiomeIds.BIRCH_FOREST) && isStructureChunk(chunkX, chunkZ, 36, 83462881L)) {
            NBTStructure trailRuins = StructureManager.getRandomStructure("trail_ruins", random);
            if (trailRuins != null) {
                trailRuins.place(world, "trail_ruins", baseX + 2, Math.max(50, surfaceY - 6), baseZ + 2);
                StructureManager.registerGeneratedStructure(worldName, "trail_ruins", baseX + 2, Math.max(50, surfaceY - 6), baseZ + 2);
                return;
            }
        }

        // 8. Ocean Shipwrecks / Ocean Ruins (1 per 24x24 chunk region in oceans)
        if ((biome == BiomeIds.OCEAN || biome == BiomeIds.DEEP_OCEAN || biome == BiomeIds.FROZEN_OCEAN) && isStructureChunk(chunkX, chunkZ, 24, 71593411L)) {
            NBTStructure shipwreck = StructureManager.getRandomStructure("shipwreck", random);
            if (shipwreck == null) shipwreck = StructureManager.getRandomStructure("ruin", random);

            if (shipwreck != null) {
                int oceanY = chunk.getHighestBlockAt(8, 8);
                shipwreck.place(world, "shipwreck", baseX + 2, Math.max(35, oceanY - 2), baseZ + 2);
                StructureManager.registerGeneratedStructure(worldName, "shipwreck", baseX + 2, Math.max(35, oceanY - 2), baseZ + 2);
            }
        }
    }

    private boolean isStructureChunk(int chunkX, int chunkZ, int regionSize, long salt) {
        int rx = Math.floorDiv(chunkX, regionSize);
        int rz = Math.floorDiv(chunkZ, regionSize);
        long seed = (long) rx * 341873128712L + (long) rz * 132897987541L + salt;
        SplittableRandom r = new SplittableRandom(seed);
        int targetX = rx * regionSize + r.nextInt(Math.max(1, regionSize - 4));
        int targetZ = rz * regionSize + r.nextInt(Math.max(1, regionSize - 4));
        return chunkX == targetX && chunkZ == targetZ;
    }

    private boolean isOutpostBiome(int biome) {
        return biome == BiomeIds.PLAINS || biome == BiomeIds.SUNFLOWER_PLAINS
                || biome == BiomeIds.DESERT || biome == BiomeIds.SAVANNA
                || biome == BiomeIds.TAIGA || biome == BiomeIds.SNOWY_SLOPES || biome == BiomeIds.ICE_PLAINS;
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

    private int findGroundY(FullChunk chunk, int localX, int localZ) {
        for (int y = 250; y >= 60; y--) {
            int id = chunk.getBlockId(localX, y, localZ);
            if (id != BlockID.AIR && id != BlockID.LEAVES && id != BlockID.LEAVES2 && id != BlockID.OAK_LOG && id != BlockID.ACACIA_LOG && id != BlockID.TALL_GRASS) {
                return y;
            }
        }
        return 0;
    }
}
