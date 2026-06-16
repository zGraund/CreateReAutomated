package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.api.datagen.AdvancedExtractingRecipeGen;
import com.github.zgraund.createreautomated.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public final class ModAdvancedExtractingRecipeGen extends AdvancedExtractingRecipeGen {
    // TODO: remove test recipe
    GeneratedRecipe NETHERITE_SCRAPS = create("scraps", builder ->
            builder.require(ModItems.NETHERITE_DRILL)
                   .require(new SizedFluidIngredient(FluidIngredient.of(Fluids.LAVA, Fluids.WATER), 50))
                   .nodes(Blocks.ANCIENT_DEBRIS)
                   .secAtMaxSpeed(20)
                   .output(Items.NETHERITE_SCRAP)
                   .fragments(1, 0.02f)
    );

    public ModAdvancedExtractingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateReAutomated.MOD_ID);
    }
}
