package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.registry.ModTags;
import net.minecraft.Util;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ModLangGen {
    private final BiConsumer<String, String> consumer;

    public ModLangGen(BiConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    public void generate() {
        // Blocks tooltip
        summary("block", "ore_node", "A _mysterious_ node too hard to extract by hand");
        summary("block", "infinite_node", "A node that emanates _powerful_ energy, it could be extracted for a very long time.");

        // Items tooltip
        summary("item", "drill_head", "This drill can be used in an _Extractor_ to excavate an _Ore_ _Node_");

        // Recipes
        recipe("extracting", "Extracting");

        // Misc
        add("itemGroup", "base", "Create Re-Automated");

        // Block tags
        tag(ModTags.Blocks.ORE_NODES);
        tag(ModTags.Blocks.DIAMOND_NODES);
        tag(ModTags.Blocks.GOLD_NODES);
        tag(ModTags.Blocks.IRON_NODES);
        tag(ModTags.Blocks.COPPER_NODES);

        // Item tags
        tag(ModTags.Items.DRILLS);
        tag(ModTags.Items.DRILL_TIER_1);
        tag(ModTags.Items.DRILL_TIER_2);
        tag(ModTags.Items.DRILL_TIER_3);
        tag(ModTags.Items.AT_MOST_TIER_1);
        tag(ModTags.Items.AT_MOST_TIER_2);
        tag(ModTags.Items.AT_MOST_TIER_3);
        tag(ModTags.Items.AT_LEAST_TIER_1);
        tag(ModTags.Items.AT_LEAST_TIER_2);
        tag(ModTags.Items.AT_LEAST_TIER_3);

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

    private void summary(String prefix, @Nonnull DeferredHolder<?, ?> holder, String summary) {
        consumer.accept(Util.makeDescriptionId(prefix, holder.getId()) + ".tooltip.summary", summary);
    }

    private void summary(String prefix, String id, String summary) {
        add(prefix, id + ".tooltip.summary", summary);
    }

    private void condition(String prefix, int number, String id, String summary) {
        add(prefix, id + ".tooltip.condition" + number, summary);
    }

    private void behaviour(String prefix, int number, String id, String summary) {
        add(prefix, id + ".tooltip.behaviour" + number, summary);
    }

    private void tag(@Nonnull TagKey<?> tag) {
        String[] splits = tag.location().getPath().split("/");
        String name = splits[splits.length - 1];
        String toHuman = Arrays.stream(name.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        tag(tag, toHuman);
    }

    private void tag(TagKey<?> tag, String translation) {
        consumer.accept(Tags.getTagTranslationKey(tag), translation);
    }
}
