# 🌲 VanillaGeneratorLumi

**VanillaGeneratorLumi** is a high-performance Overworld terrain & structure generation system tailored for **Lumi** and **Nukkit** servers (Minecraft Bedrock Edition 1.20+).

---

## ⚡ Key Highlights

| Feature | Description |
| :--- | :--- |
| **🌐 Zero World Lock-In** | Dynamic event-driven populator. Switch server cores freely without locking `level.dat`. |
| **🗺️ 6D Multi-Noise Climate** | Authentic 1.18+ / 1.20+ climate model: Temperature, Humidity, Continentalness, Erosion, Weirdness, Offset. |
| **🏔️ 3D Density Heightmap** | Full vertical world height from `Y = -64` to `Y = 320` with mountain peaks reaching `Y = 250+`. |
| **🧱 Surface System** | Authentic terracotta color banding, red sand, calcite, powder snow, ice caps, and sandstone layers. |
| **🕳️ 1.18+ 3D Caves & Caverns** | 3D Cheese caves and Spaghetti tunnel systems carving down to `Y = -58` with deep lava lakes. |
| **📦 Dual NBT Engine** | Parses both Java NBT (`blocks`) and Bedrock NBT (`block_indices` / `.mcstructure`). |
| **🚀 SubChunk Packet Batching** | Uses `UpdateSubChunkBlocksPacket` to batch 1,000+ blocks into 1 packet per subchunk. |
| **⚡ SplittableRandom Engine** | Thread-safe, lock-free random generation for noise, caves, trees, and ores. |

---

## 🏔 Generators & Populators

| Generator / Populator | Target Biomes / Zone | Description |
| :--- | :--- | :--- |
| **`OverworldGenerator`** | Overworld (`Y = -64..320`) | 3D Multi-Noise heightmap, mountain ridges, and direct biome binary chunk writing. |
| **`SurfaceSystem`** | All Terrain | Authentic surface layers: Badlands terracotta bands, Red Sand, Calcite, Snow blocks, Sandstone. |
| **`CaveGenerator`** | Subterranean (`Y = -58..160`) | Carves natural 3D cave tunnels, large caverns, flow noise, and deep subterranean lava lakes. |
| **`OrePopulator`** | Subterranean & Mountains | 1.18+ Ore distribution: Diamonds at bedrock (`Y = -64..16`), mountain Iron (`Y = 232`), Badlands Gold (`Y = 256`). |
| **`CherryGrovePopulator`** | Cherry Grove | Sakura trees, pink leaf canopy, ground flower petals, and balanced mob spawns. |
| **`BambooPopulator`** | Jungle Biomes | Bamboo stalks, podzol, and dense jungle vegetation. |
| **`OceanPopulator`** | Oceans & Rivers | Kelp, seagrass, sea pickles, and vibrant coral reefs. |
| **`LootPopulator`** | Chest Entities | Spawns Disc 5, Echo Shards, Enchanted Golden Apples, Netherite Scraps, & Swift Sneak books. |
| **`MobPopulator`** | Structures | Spawns Pillagers in outposts, Evokers/Vindicators in Mansions, & Villagers in Igloos. |

---

## 🏛 Natural Structures & Assemblies

| Category | Command Key | Key Components | Spawning & Generation Details |
| :--- | :--- | :--- | :--- |
| **🏛 Ancient City** | `ancient_city` | `city_center`, `city`, `walls`, `structures` | Deep underground (`Y = -51`), reinforced deepslate center, wool corridors, redstone secrets. |
| **🏰 Woodland Mansion** | `mansion` | `entrance`, `1x1_*`, `1x2_*`, `2x2_*`, `wall_*` | 3-story grand entrance hall with surrounding rooms, secret chambers, and windows. |
| **🛸 End City & Ship** | `endcity` | `fat_tower`, `tower_*`, `bridge_*`, `ship` | Branching towers with Purpur bridges, and floating End Ship with Elytra & Dragon Head. |
| **🏡 Villages** | `village` | Plains, Desert, Savanna, Taiga, Snowy | Full settlements with town centers, bells, villager workstations, houses, and golems. |
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
