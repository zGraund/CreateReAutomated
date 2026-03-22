package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.registry.ModItems;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class ModSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {
    GeneratedRecipe DIAMOND = create(
            "unbaked_diamond_from_bits",
            builder ->
                    builder.require(ModItems.DIAMOND_BIT)
                           .transitionTo(ModItems.INCOMPLETE_DIAMOND)
                           .addOutput(ModItems.UNBAKED_DIAMOND, 100)
                           .loops(8)
                           .addStep(DeployerApplicationRecipe::new, deployer -> deployer.require(ModItems.DIAMOND_BIT))
    );

    public ModSequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }
}
