# Create Re-Automated

A Create addon for Minecraft 1.21.1 (NeoForge). Partial port
of [Create Automated](https://github.com/kotakotik22/Create-Automated) by kotakotik22.

Adds ore nodes to the world from which resources can be extracted using an Extractor machine with drills.

## For Modpack Creators

### Custom Recipes via Datapack

Extracting recipes can be customized with JSON datapacks. Example:

```json
{
  "type": "createreautomated:extracting",
  "ingredients": [
    {
      "tag": "createreautomated:drills/at_least_tier_1"
    }
  ],
  "node": "#createreautomated:ore_nodes/iron_nodes",
  "processing_time": 25600,
  "results": [
    {
      "count": 3,
      "id": "createreautomated:iron_bit"
    },
    {
      "chance": 0.01,
      "id": "createreautomated:node_fragment"
    }
  ]
}
```

**Field types** (from `ExtractingRecipeParams` and its superclass `ProcessingRecipeParams`):

| Field                | Type                     | Description                                       |
|----------------------|--------------------------|---------------------------------------------------|
| `type`               | `ResourceLocation`       | Must be `createreautomated:extracting`            |
| `ingredients`        | `List<Ingredient>`       | Drill requirement (use item tags for tiers)       |
| `node`               | `HolderSet<Block>`       | Target node blocks (tag or direct reference)      |
| `processing_time`    | `int`                    | Processing time in ticks                          |
| `results`            | `List<ProcessingOutput>` | Output items with optional `chance`               |
| `extractionQuantity` | `int`                    | Items extracted per operation (default: 1)        |
| `durabilityCost`     | `int`                    | Drill durability cost per extraction (default: 1) |

### KubeJS Integration

Requires the `kubejs_create` addon.

**Extracting recipe** (in `server_scripts`):

```js
ServerEvents.recipes(event => {
    // output, input (drill), nodes, time, durability cost, extraction amount

    event.recipes.createreautomated.extracting("5x diamond", "kubejs:test_drill", "kubejs:test_node")
                                   .processingTime(25000)
                                   .durabilityCost(1000)
                                   .extractionQuantity(100)
})
```

**Custom ore node and drill** (in `startup_scripts`):

```js
//To add a custom node
StartupEvents.registry("block", event => {
    event.create("test_node","createreautomated:ore_node")
    .withCommonLoot()       // uses the default ore node loot table
    .yield(100)             // max extractions before depletion
    .copyPropertiesFrom("createreautomated:diamond_node") // copies block properties
    .baseStone("minecraft:cobblestone") // block left after depletion
})

//To add a custom drill
StartupEvents.registry("item",event=>{
    event.create("test_drill","createreautomated:drill")
    .withPartial("createreautomated:partial/diamond_drill") // render model
    .tag(["createreautomated:drills","createreautomated:drills/tier_1"]) // tier tags
})
```

See the `compat/kubejs` package for more details.

## For Mod Creators

Custom ore nodes and drills can be registered via the Java API in the `api` package:

- **`OreNodeBlockIndex`** - Register custom ore node blocks and their yield values.
- **`DrillPartialIndex`** - Register custom drill items and their partial models for rendering.
- **`Extractable`** - Interface for blocks to customize extraction behavior.
- **`ExtractingRecipeGen`** - Base class for data-generating extracting recipes.