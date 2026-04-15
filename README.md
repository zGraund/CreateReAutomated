# Create ReAutomated

A Create addon for Minecraft 1.21.1 (NeoForge). Partial port
of [Create Automated](https://github.com/kotakotik22/Create-Automated) by kotakotik22.

Adds ore nodes to the world from which resources can be extracted using an Extractor.

## For Modpack Creators

### Custom Recipes via Datapack

Extracting recipes can be customized with JSON datapacks.

Example:

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
  "extractionQuantity": 2,
  "durabilityCost": 5,
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

**Field types** (from `ExtractingRecipeParams` and Create `ProcessingRecipeParams`):

| Field                | Type                     | Description                                        |
|----------------------|--------------------------|----------------------------------------------------|
| `type`               | `ResourceLocation`       | Must be `createreautomated:extracting`             |
| `ingredients`        | `List<Ingredient>`       | The Drill (can be only 1)                          |
| `node`               | `HolderSet<Block>`       | Target node blocks (tag, block, or list of blocks) |
| `processing_time`    | `int`                    | Processing time in ticks                           |
| `extractionQuantity` | `int`                    | Items extracted per operation (default: 1)         |
| `durabilityCost`     | `int`                    | Drill durability cost per extraction (default: 1)  |
| `results`            | `List<ProcessingOutput>` | Output items with optional `chance`                |

### KubeJS Integration

Requires the `kubejs_create` addon.

**Extracting recipe** (in `server_scripts`):

```js
ServerEvents.recipes(event => {
    //                                          output        drill           nodes
    event.recipes.createreautomated.extracting("5x diamond", "kubejs:drill", "kubejs:node")
                                   .processingTime(25600)
                                   .extractionQuantity(2)
                                   .durabilityCost(5)
})
```

**Custom ore node and drill** (in `startup_scripts`):

```js
// Add a custom node
StartupEvents.registry("block", event => {
    event.create("test_node", "createreautomated:ore_node")
         // ... other kubejs block builder methods
         .copyPropertiesFrom("createreautomated:diamond_node") // (optional) copy properties from another node
         .withCommonLoot() // uses the default ore node loot table
         .yield(100) // max extractions before depletion
         .baseStone("minecraft:cobblestone") // block left after depletion
})

// Add a custom drill
StartupEvents.registry("item", event => {
    event.create("drill", "createreautomated:drill")
         // ... other kubejs item builder methods
         .withPartial("createreautomated:partial/diamond_drill") // render model
})
```

See the `compat/kubejs` package for more details.

## For Mod Creators

Custom ore nodes and drills can be registered via the Java API in the `api` package:

- **`Extractable`** - Interface for custom nodes to customize extraction behavior (required).
- **`OreNodeBlockIndex`** - Register custom ore node blocks and their yield values.
- **`DrillPartialIndex`** - Register custom drill items and their partial models for rendering.
- **`ExtractingRecipeGen`** - Base class for data-generating extracting recipes.
