Create ReAutomated 0.3.0
------------------------------------------------------

#### New Content

- New extractor type: Advanced Extractor
    - Like the base Extractor but also requires a fluid to function.
- New recipe type advanced_extracting.
- New node: Ancient Debris

#### Changes

- Updated Extractor crafting.

#### Configs

- Server:
    - Added option to set all nodes infinite.
    - Added option to modify both Extractors stats (Stress / Speed required).
    - Added Extracting recipe global modifiers to change various recipe values without a datapack.
- Common:
    - Expanded worldgen configs to allow for per-node configuration.

#### Art Changes

- New Extractor texture and model.

Create ReAutomated 0.2.0
------------------------------------------------------

> ### ⚠️WARNING:
>
> #### This update made significant changes to the worldgen config, check the changelog below before updating.

#### Configs

- Client:
    - Added option to disable Node particles.
- Common > Worldgen
    - Added per dimension settings to change node spawn quantity and height range
    - Added option to change number of faces that need to touch the respective ore for a node to spawn

#### Fixes

- Nodes are now visible in maps

#### For Modpack/Datapack authors

- Renamed feature `ore_node` -> `encased_ore`
- Added optional field `facesOverride` to `encased_ore`, when omitted the global `requiredFaces` config will be used

------------------------------------------------------
Create ReAutomated 0.1.1
------------------------------------------------------

#### Improvements

- Extractor:
    - Added direct belt input support
    - Added arm interaction point support
- Added configuration to disable drill durability consumption

#### Fixes

- Added missing raw zinc recipe from bits

--------------------------------------------------------
Create ReAutomated 0.1.0
--------------------------------------------------------

#### Initial release
