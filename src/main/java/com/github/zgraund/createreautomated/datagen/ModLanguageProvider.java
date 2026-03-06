package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.item.ModItems;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
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
        OreNodeBlock.getAllNodes().forEach(nodeBlock ->
                add(nodeBlock, nameFromId(BuiltInRegistries.BLOCK.getKey(nodeBlock)))
        );
        add("block", "ore_node.tooltip", "Ore Node");
        add("block", "ore_node.tooltip.summary", "A _mysterious_ node too hard to extract by hand");

        simpleBlock(ModBlocks.EXTRACTOR);
        tooltip("block", ModBlocks.EXTRACTOR, "Node Ore Extractor");
        summary("block", ModBlocks.EXTRACTOR, "A _powerful_, but _hard_ _to_ _power_, machine that can extract _Ore Nodes_");

        ModItems.getAllDrills().forEach(drill -> addItem(drill, nameFromId(drill.getId())));
        add("item", "drill_head.tooltip", "A drill head for an extractor");
        add("item", "drill_head.tooltip.summary", "This drill can be used in an _Extractor_ to excavate an _Ore_ _Node_");

        add("itemGroup", "base", "Create Re-Automated");

        config("title", "Create Re-Automated Configs");
        config("section." + CreateReAutomated.MOD_ID + ".common.toml", "Create Re-Automated Configs");
        config("section." + CreateReAutomated.MOD_ID + ".common.toml.title", "Create Re-Automated Configs");
        config("debugOreNode", "Debug Ore Node");
        config("debugExtractor", "Debug Extractor");
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

    private void config(String suffix, String value) {
        add(CreateReAutomated.MOD_ID + ".configuration." + suffix, value);
    }

    private void tooltip(String prefix, @Nonnull DeferredHolder<?, ?> holder, String tooltip) {
        add(Util.makeDescriptionId(prefix, holder.getId()) + ".tooltip", tooltip);
    }

    private void summary(String prefix, @Nonnull DeferredHolder<?, ?> holder, String summary) {
        add(Util.makeDescriptionId(prefix, holder.getId()) + ".tooltip.summary", summary);
    }
}
