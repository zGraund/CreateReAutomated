# Create Re-Automated

A Create addon for Minecraft 1.21.1 (NeoForge) that adds ore nodes and an extraction system. Partial port
of [Create Automated](https://github.com/kotakotik22/Create-Automated) by kotakotik22.

**Requires:** Create 6.0.9+, NeoForge 21.1.219+

## Overview

Ore nodes are special blocks that generate naturally in the world. They cannot be mined by hand and require an Extractor
with a drill to yield resources. Extracted bits are then processed into raw materials through Create's machines.

## Blocks

### Extractor

A two-block-tall kinetic machine that extracts resources from ore nodes. Connects to a rotational source via a shaft on
the top face (Y axis). Acts as a small cogwheel.

- Minimum speed: 32 RPM (Medium)
- Stress impact: 64 SU (configurable, doubles per speed tier increase)
- Inventories: 1 drill slot, 6 output slots
- The target node must be placed **2 blocks below** the Extractor's lower half (one block of air in between)

**Interaction:**

- Right-click with a drill: insert the drill
- Right-click empty-handed: collect output items
- Shift + right-click empty-handed: remove the drill

Drill insertion and output extraction can be automated via hoppers, chutes, or any item transport. The drill slot only
accepts insertion; the output slots only allow extraction.

### Ore Nodes

Ore nodes generate naturally and have a limited number of extractions before turning into their base rock (cobblestone,
cobbled deepslate, or netherrack). They emit light (level 10, decreasing as they deplete) and show visual wear across 11
depletion stages.

| Node                   | Extractions | Base Rock         |
|------------------------|-------------|-------------------|
| Copper Node            | 240         | Cobblestone       |
| Zinc Node              | 200         | Cobblestone       |
| Iron Node              | 240         | Cobblestone       |
| Gold Node              | 180         | Cobblestone       |
| Diamond Node           | 108         | Cobblestone       |
| Deepslate Copper Node  | 300         | Cobbled Deepslate |
| Deepslate Zinc Node    | 250         | Cobbled Deepslate |
| Deepslate Iron Node    | 300         | Cobbled Deepslate |
| Deepslate Gold Node    | 225         | Cobbled Deepslate |
| Deepslate Diamond Node | 135         | Cobbled Deepslate |
| Nether Gold Node       | 180         | Netherrack        |

Deepslate variants provide roughly 25% more extractions than their stone counterparts.

### Infinite Nodes

Infinite nodes never deplete. They exist for copper, zinc, iron, gold, and diamond. Obtainable only through Mechanical
Crafting.

## Items

### Drills

Drills are placed inside the Extractor to perform extraction. Each drill has a tier that determines which nodes it can
work on, and durability that depletes over use.

| Drill           | Tier | Durability |
|-----------------|------|------------|
| Iron Drill      | 1    | 1500       |
| Diamond Drill   | 2    | 4000       |
| Netherite Drill | 3    | 8000       |

### Bits

Raw resources obtained from extraction. Must be processed further into usable materials.

- Copper Bit, Zinc Bit, Iron Bit, Gold Bit, Diamond Bit

### Node Fragment

A rare byproduct (1% chance per extraction) from any extracting recipe. Used to craft the Stabilizer and infinite nodes.

### Stabilizer

Allows breaking an unstable ore node (one with `stable=false`) with a pickaxe. Hold right-click on the node for 4
seconds to stabilize it, consuming the Stabilizer.

## Extraction Recipes

Every extraction has a 1% chance to also drop a Node Fragment.

| Node                         | Iron Drill (T1)                 | Diamond Drill (T2)                 | Netherite Drill (T3)               |
|------------------------------|---------------------------------|------------------------------------|------------------------------------|
| Copper Stone (240 ext.)      | 8 bits, 1920 total, **213 raw** | 8 bits, 1920 total, **213 raw**    | 8 bits, 1920 total, **213 raw**    |
| Copper Deepslate (300 ext.)  | 8 bits, 2400 total, **266 raw** | 8 bits, 2400 total, **266 raw**    | 8 bits, 2400 total, **266 raw**    |
| Zinc Stone (200 ext.)        | 8 bits, 1600 total, **177 raw** | 8 bits, 1600 total, **177 raw**    | 8 bits, 1600 total, **177 raw**    |
| Zinc Deepslate (250 ext.)    | 8 bits, 2000 total, **222 raw** | 8 bits, 2000 total, **222 raw**    | 8 bits, 2000 total, **222 raw**    |
| Iron Stone (240 ext.)        | 6 bits, 1440 total, **160 raw** | 6 bits, 1440 total, **160 raw**    | 6 bits, 1440 total, **160 raw**    |
| Iron Deepslate (300 ext.)    | 6 bits, 1800 total, **200 raw** | 6 bits, 1800 total, **200 raw**    | 6 bits, 1800 total, **200 raw**    |
| Gold Stone (180 ext.)        | --                              | 4 bits, 720 total, **80 raw**      | 4 bits, 720 total, **80 raw**      |
| Gold Deepslate (225 ext.)    | --                              | 4 bits, 900 total, **100 raw**     | 4 bits, 900 total, **100 raw**     |
| Nether Gold (180 ext.)       | --                              | 6 bits, 1080 total, **120 raw**    | 6 bits, 1080 total, **120 raw**    |
| Diamond Stone (108 ext.)     | --                              | 2 bits, 216 total, **24 diamonds** | 4 bits, 432 total, **48 diamonds** |
| Diamond Deepslate (135 ext.) | --                              | 2 bits, 270 total, **30 diamonds** | 4 bits, 540 total, **60 diamonds** |

Columns read as: bits per extraction, total bits from the node, final material after processing (9 bits = 1 raw ore).
Diamond requires Sequenced Assembly + superheated compacting instead of simple compacting.

Copper, zinc, and iron work with any drill (Tier 1+). Gold and nether gold require Tier 2+. Diamond has two separate
recipes: Tier 2 only (slower, fewer bits) and Tier 3 (faster, double output).

## Crafting Recipes

### Extractor

```
ICI
IOI
IEI
```

I = Brass Ingot, C = Brass Casing, O = Cogwheel, E = Electron Tube

### Iron Drill

```
 A
III
 I
```

A = Andesite Alloy, I = Iron Ingot

### Diamond Drill

```
 P
DID
 D
```

P = Precision Mechanism, I = Iron Drill, D = Diamond

### Netherite Drill

Smithing Table: Diamond Drill + Netherite Ingot (no template).

### Stabilizer

```
FFF
FSF
FFF
```

F = Node Fragment, S = Nether Star

### Infinite Nodes (Mechanical Crafting, 5x5)

```
 AAA
ABDBA
ADCDA
ABDBA
 AAA
```

A = Node Fragment, B = Ore Node (stone or deepslate variant), C = Heavy Core, D = Echo Shard. Cannot be mirrored.

## Processing Bits

### Compacting (Basin)

9 bits compact into 1 raw ore:

| Input         | Output     | Heat   |
|---------------|------------|--------|
| 9x Copper Bit | Raw Copper | None   |
| 9x Iron Bit   | Raw Iron   | None   |
| 9x Gold Bit   | Raw Gold   | Heated |

### Diamond Production

1. **Sequenced Assembly:** 1 Diamond Bit + 8 Diamond Bit (via Deployer, 8 loops) = 1 Unbaked Diamond
2. **Superheated Compacting:** 1 Unbaked Diamond = 1 Diamond

Total: 9 Diamond Bit = 1 Diamond.

## World Generation

Ore nodes generate naturally in the Overworld (stone and deepslate variants) and in the Nether (Nether Gold Node). The
generation algorithm clusters nodes together, avoiding isolated placement. World generation can be disabled in the
common config.

## Configuration

### Client

| Option                  | Default | Description                                                                  |
|-------------------------|---------|------------------------------------------------------------------------------|
| `debugOreNodeOverlay`   | false   | Right-clicking a node with an empty hand shows remaining extractions in chat |
| `debugExtractorOverlay` | false   | Shows Extractor debug info when wearing Engineer's Goggles                   |

### Common

| Option     | Default | Description                              |
|------------|---------|------------------------------------------|
| `worldGen` | true    | Enable/disable ore node world generation |

### Server

| Option            | Default | Description                                            |
|-------------------|---------|--------------------------------------------------------|
| `extractorImpact` | 64      | Extractor stress impact in SU (doubles per speed tier) |

Node extraction limits are individually configurable under `nodeValues.extraction` in the server config.

## Mod Compatibility

- **JEI:** Extracting recipe category with animated Extractor display.
- **KubeJS:** Builders for custom ore nodes, drills, and extracting recipes.
- **Ponder:** Built-in tutorial scene for the Extractor.

## For Addon Developers

The `Extractable` interface allows any block to work with the Extractor. `OreNodeBlockIndex` provides registration for
custom ore nodes and their yield values. `DrillPartialIndex` maps drill items to their partial models for rendering.
`ExtractingRecipeGen` is the base class for data-generating extracting recipes.

### Tags

**Blocks:**

- `createreautomated:ore_nodes` -- all nodes
- `createreautomated:ore_nodes/{copper,zinc,iron,gold,diamond}_nodes` -- per material

**Items:**

- `createreautomated:drills` -- all drills
- `createreautomated:drills/tier_{1,2,3}` -- exact tier
- `createreautomated:drills/at_least_tier_{1,2,3}` -- minimum tier
- `createreautomated:drills/at_most_tier_{1,2,3}` -- maximum tier
