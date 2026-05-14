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
