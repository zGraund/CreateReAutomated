package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.Util;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class ModLangGen {
    private final BiConsumer<String, String> consumer;

    public ModLangGen(BiConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    public void generate() {
        // Blocks
        tooltip("block", "ore_node", "Ore Node");
        summary("block", "ore_node", "A _mysterious_ node too hard to extract by hand");

        // Items
        tooltip("item", "drill_head", "A drill head for an extractor");
        summary("item", "drill_head", "This drill can be used in an _Extractor_ to excavate an _Ore_ _Node_");

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
        consumer.accept("generator.createreautomated.ore_node_test", "Node Ore test world");
    }

    private void add(String prefix, String suffix, String value) {
        consumer.accept(prefix + "." + CreateReAutomated.MOD_ID + "." + suffix, value);
    }

    private void recipe(String type, String name) {
        consumer.accept(CreateReAutomated.MOD_ID + ".recipe." + type, name);
    }

    private void config(String suffix, String value) {
        consumer.accept(CreateReAutomated.MOD_ID + ".configuration." + suffix, value);
    }

    private void tooltip(String prefix, @Nonnull DeferredHolder<?, ?> holder, String tooltip) {
        consumer.accept(Util.makeDescriptionId(prefix, holder.getId()) + ".tooltip", tooltip);
    }

    private void tooltip(String prefix, String id, String tooltip) {
        add(prefix, id + ".tooltip", tooltip);
    }

    private void summary(String prefix, @Nonnull DeferredHolder<?, ?> holder, String summary) {
        consumer.accept(Util.makeDescriptionId(prefix, holder.getId()) + ".tooltip.summary", summary);
    }

    private void summary(String prefix, String id, String summary) {
        add(prefix, id + ".tooltip.summary", summary);
    }
}
