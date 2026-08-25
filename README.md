# 🌲 VanillaGeneratorLumi

**VanillaGeneratorLumi** is a high-performance Overworld terrain & structure generation system tailored for **Lumi** and **Nukkit** servers (Minecraft Bedrock Edition 1.20+).

---

## ⚡ Key Highlights

| Feature | Description |
| :--- | :--- |
| **🌐 Zero World Lock-In** | Dynamic event-driven populator. Switch server cores freely without locking `level.dat`. |
| **📦 Dual NBT Engine** | Parses both Java NBT (`blocks`) and Bedrock NBT (`block_indices` / `.mcstructure`). |
| **🚀 SubChunk Packet Batching** | Uses `UpdateSubChunkBlocksPacket` to batch 1,000+ blocks into 1 packet per subchunk. |
| **🏗 Auto-Foundation** | Village houses & outposts extend dirt foundations downwards to prevent floating structures. |
| **⚡ SplittableRandom Engine** | Thread-safe, lock-free random generation for noise, caves, trees, and ores. |

---

## 🏔 Generators & Populators

| Generator / Populator | Target Biomes / Zone | Description |
| :--- | :--- | :--- |
| **`OverworldGenerator`** | Overworld | 3D Perlin/Simplex height noise, mountain ridges, and direct biome binary chunk writing. |
| **`GroundGenerator`** | All Terrain | Authentic ground layers: Sand ➔ Sandstone ➔ Stone in deserts; Dirt ➔ Stone in plains. |
| **`CaveGenerator`** | Subterranean (`Y=5..55`) | Carves natural cave tunnels, caverns, flow noise, and lava pools. |
| **`CherryGrovePopulator`** | Cherry Grove | Sakura trees, pink leaf canopy, ground flower petals, and balanced mob spawns (8%). |
| **`BambooPopulator`** | Jungle Biomes | Bamboo stalks, podzol, and dense jungle vegetation (6%). |
| **`OceanPopulator`** | Oceans & Rivers | Kelp, seagrass, sea pickles, and vibrant coral reefs. |
| **`LootPopulator`** | Chest Entities | Spawns Disc 5, Echo Shards, Enchanted Golden Apples, Netherite Scraps, & Sculk Catalysts. |
| **`MobPopulator`** | Structures | Spawns Pillagers in outposts, Evokers/Vindicators in Mansions, & Villagers in Igloos. |

---

## 🏛 Natural Structures & Assemblies

| Category | Command Key | Key Components | Spawning & Generation Details |
| :--- | :--- | :--- | :--- |
| **🏛 Ancient City** | `ancient_city` | `city_center`, `city`, `walls`, `structures` | Deep underground (`Y=22`), reinforced deepslate center, wool corridors, redstone secrets. |
| **🏰 Nether Bastion** | `bastion` | `treasure`, `units`, `ramparts`, `bridge`, `hoglin_stable` | Multi-unit basalt fortress with treasure bridges and Piglin/Hoglin spawns. |
| **🏡 Villages** | `village` | Plains, Desert, Savanna, Taiga, Snowy | Full 10–14 building settlements (churches, blacksmiths, armorers, farms, wells). |
| **🏰 Woodland Mansion** | `mansion` | `entrance`, `1x1_*`, `1x2_*`, `2x2_*`, `wall_*` | 2-story grand entrance hall with surrounding rooms, secret chambers, and windows. |
| **❄️ Igloo Laboratory** | `igloo` | `igloo_top`, `igloo_middle`, `igloo_bottom` | Surface snow dome with bed & furnace, secret ladder shaft, and underground potion lab. |
| **🏹 Pillager Outpost** | `pillageroutpost` | Watchtower & cages | Dark oak outpost towers with target range and cages. |
| **🌋 Ruined Portal** | `ruined_portal` | Obsidian frames & netherrack | Nether portal ruins with magma blocks and loot chests. |
| **🚢 Shipwreck & Ruins** | `shipwreck`, `ruin` | Sunken hulls & ocean ruins | Underwater ship hulls and stone ocean ruins. |
| **🦴 Fossils** | `fossils` | Bone structures | Giant underground dinosaur skeletons. |

---

## 📋 System Requirements

| Component | Minimum Requirement |
| :--- | :--- |
| **Java Runtime** | Azul Zulu OpenJDK 21 LTS (`21.0.11`+) or Java 21+ |
| **Server Core** | Lumi / Nukkit (Bedrock 1.20+) |
| **Build Tool** | Gradle 8.x (Wrapper included) |

---

## 🚀 Building & Installation

```bash
# Build plugin JAR with Gradle
./gradlew shadowJar   # Linux / macOS
gradlew.bat shadowJar # Windows
```

1. Compiled JAR location: `build/libs/VanillaGeneratorLumi.jar`.
2. Copy `VanillaGeneratorLumi.jar` to your server's `plugins/` directory.
3. Restart the server to initialize generators and commands.

---

## 🎮 Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/stset <category>` | Spawns a full multi-part structure at player position | `vanillagenerator.command.stset` |
| `/structure` | Shows detailed structure generation & biome info | `vanillagenerator.command.structure` |

#lumi-plugin
