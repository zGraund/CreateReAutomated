package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModItems;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, CreateReAutomated.MOD_ID, "en_us");
    }

    public static String nameFromId(@Nonnull ResourceLocation id) {
        return Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    @Override
    protected void addTranslations() {
        // Blocks
        ModBlocks.getAllNodes().forEach(nodeBlock ->
                addBlock(nodeBlock.block(), nameFromId(nodeBlock.block().getId()))
        );
        add("block", "ore_node.tooltip", "Ore Node");
        add("block", "ore_node.tooltip.summary", "A _mysterious_ node too hard to extract by hand");

        simpleBlock(ModBlocks.EXTRACTOR);

        // Items
        ModItems.getAllDrills().forEach(drill -> addItem(drill, nameFromId(drill.getId())));
        tooltip("item", "drill_head", "A drill head for an extractor");
        summary("item", "drill_head", "This drill can be used in an _Extractor_ to excavate an _Ore_ _Node_");
        simpleItem(ModItems.COPPER_BIT);
        simpleItem(ModItems.IRON_BIT);
        simpleItem(ModItems.GOLD_BIT);
        simpleItem(ModItems.DIAMOND_BIT);

        // Recipes
        recipe("extracting", "Extracting");

        // Misc
        add("itemGroup", "base", "Create Re-Automated");

        // Configs
        config("title", "Create Re-Automated Configurations");
        config("section." + CreateReAutomated.MOD_ID + ".client.toml", "Client Configurations");
        config("section." + CreateReAutomated.MOD_ID + ".client.toml.title", "Create Re-Automated Client Configurations");
        config("section." + CreateReAutomated.MOD_ID + ".common.toml", "Common Configurations");
        config("section." + CreateReAutomated.MOD_ID + ".common.toml.title", "Create Re-Automated Common Configurations");
        config("debugOreNode", "Debug Ore Node");
        config("debugExtractor", "Debug Extractor");
        config("worldGen", "Enable World Generation");

        // TODO: remove
        // Worldgen for testing
        add("generator.createreautomated.ore_node_test", "Node Ore test world");
    }

    private void add(String prefix, String suffix, String value) {
        add(prefix + "." + CreateReAutomated.MOD_ID + "." + suffix, value);
    }

    private void simpleBlock(DeferredBlock<?> block) {
        addBlock(block, nameFromId(block.getId()));
    }

    private void simpleItem(DeferredItem<?> item) {
        addItem(item, nameFromId(item.getId()));
    }

    private void recipe(String type, String name) {
        add(CreateReAutomated.MOD_ID + ".recipe." + type, name);
    }

    private void config(String suffix, String value) {
        add(CreateReAutomated.MOD_ID + ".configuration." + suffix, value);
    }

    private void tooltip(String prefix, @Nonnull DeferredHolder<?, ?> holder, String tooltip) {
        add(Util.makeDescriptionId(prefix, holder.getId()) + ".tooltip", tooltip);
    }

    private void tooltip(String prefix, String id, String tooltip) {
        add(prefix, id + ".tooltip", tooltip);
    }

    private void summary(String prefix, @Nonnull DeferredHolder<?, ?> holder, String summary) {
        add(Util.makeDescriptionId(prefix, holder.getId()) + ".tooltip.summary", summary);
    }

    private void summary(String prefix, String id, String summary) {
        add(prefix, id + ".tooltip.summary", summary);
    }
}
