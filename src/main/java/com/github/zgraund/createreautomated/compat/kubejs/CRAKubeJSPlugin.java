package com.github.zgraund.createreautomated.compat.kubejs;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.compat.kubejs.block.OreNodeBlockBuilderJS;
import com.github.zgraund.createreautomated.compat.kubejs.item.DrillItemBuilderJS;
import com.github.zgraund.createreautomated.compat.kubejs.recipe.NodesRecipeComponent;
import com.github.zgraund.createreautomated.registry.ModBlockEntities;
import com.github.zgraund.createreautomated.registry.ModRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaProvider;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CRAKubeJSPlugin implements KubeJSPlugin {
    public static final Set<Block> VALID_BLOCKS_FOR_BE = new HashSet<>();

    public static void registerItemTooltipModifier(Item item) {
        TooltipModifier modifier = new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        TooltipModifier.REGISTRY.register(item, modifier);
    }

    @Override
    public void init() {
        if (!ModList.get().isLoaded("kubejs_create"))
            throw new IllegalStateException(CreateReAutomated.NAME + " requires addon KubeJS Create when KubeJS is installed.");

        ModList.get().getModContainerById(CreateReAutomated.MOD_ID).ifPresent(container -> {
                    IEventBus eventBus = container.getEventBus();
                    if (eventBus == null)
                        return;
                    eventBus.addListener(EventPriority.LOWEST, (BlockEntityTypeAddBlocksEvent event) ->
                            event.modify(ModBlockEntities.ORE_NODE_BE.getKey(), VALID_BLOCKS_FOR_BE.toArray(Block[]::new))
                    );
                    eventBus.addListener(this::gatherData);
                }
        );
    }

    @Override
    public void registerBuilderTypes(@Nonnull BuilderTypeRegistry registry) {
        registry.of(Registries.BLOCK, reg ->
                reg.add(CreateReAutomated.asResource("ore_node"), OreNodeBlockBuilderJS.class, OreNodeBlockBuilderJS::new)
        );
        registry.of(Registries.ITEM, reg ->
                reg.add(CreateReAutomated.asResource("drill"), DrillItemBuilderJS.class, DrillItemBuilderJS::new)
        );
    }

    @Override
    public void registerRecipeComponents(@Nonnull RecipeComponentTypeRegistry registry) {
        registry.register(NodesRecipeComponent.TYPE);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void gatherData(@Nonnull GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), new RecipeSchemaProvider(CreateReAutomated.MOD_ID + " Recipe Schemas", event) {
            @Override
            public void add(HolderLookup.Provider lookup) {
                add(ModRecipeTypes.EXTRACTING.getId(), builder -> {
                    NumberComponent.IntRange nonNegativeInt = NumberComponent.intRange(0, Integer.MAX_VALUE);
                    builder.parent(Create.asResource("base/processing_with_time"));
                    builder.mappings(ModRecipeTypes.EXTRACTING.getId().getPath());
                    builder.keys(List.of(
                            NodesRecipeComponent.TYPE.inputKey("node"),
                            nonNegativeInt.otherKey("durabilityCost").optional(1),
                            nonNegativeInt.otherKey("extractionQuantity").optional(1)
                    ));
                    builder.mergeData(true, true, true, true);
                });
            }
        });
    }
}
