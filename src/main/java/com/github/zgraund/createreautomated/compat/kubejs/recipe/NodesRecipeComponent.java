package com.github.zgraund.createreautomated.compat.kubejs.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import dev.latvian.mods.kubejs.holder.HolderWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public class NodesRecipeComponent extends SimpleRecipeComponent<HolderSet<Block>> {
    public static final RecipeComponentType<HolderSet<Block>> TYPE =
            RecipeComponentType.unit(CreateReAutomated.asResource("node"), NodesRecipeComponent::new);

    public NodesRecipeComponent(RecipeComponentType<?> type) {
        super(type, RegistryCodecs.homogeneousList(Registries.BLOCK), HolderWrapper.HOLDER_SET);
    }

    @Override
    public HolderSet<Block> wrap(RecipeScriptContext cx, Object from) {
        return HolderWrapper.wrapSimpleSet(BuiltInRegistries.BLOCK, from);
    }
}
