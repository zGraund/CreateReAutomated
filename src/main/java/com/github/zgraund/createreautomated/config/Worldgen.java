package com.github.zgraund.createreautomated.config;

import com.mojang.serialization.Codec;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
public class Worldgen extends ConfigBase {
    public final ConfigBool enabled =
            b(true, "enabled", "Whether Create Re-Automated should generate nodes in the world.");

    @Override
    public void registerAll(@Nonnull ModConfigSpec.Builder builder) {
        for (NodeGroup group : NodeGroup.values())
            group.register(this);
        super.registerAll(builder);
    }

    @Override
    public String getName() {
        return "worldgen";
    }

    public enum NodeGroup implements StringRepresentable {
        COPPER(3, 30, -16, 64),
        ZINC(3, 50, -64, 64),
        IRON(3, 50, -64, 64),
        GOLD(3, 40, -64, 32),
        DIAMOND(3, 30, -64, 16),
        NETHER_GOLD(3, 60, 10, 118),
        NETHER_QUARTZ(3, 50, 7, 117),
        ANCIENT_DEBRIS(1, 300, 8, 24);

        public static final Codec<NodeGroup> CODEC = StringRepresentable.fromEnum(NodeGroup::values);

        private final NodeConfig config;

        NodeGroup(int faces, int tries, int minY, int maxY) {
            this.config = new NodeConfig(this, faces, tries, minY, maxY);
        }

        public NodeConfig getConfig() {
            return config;
        }

        public void register(@Nonnull Worldgen worldgen) {
            worldgen.nested(1, this::getConfig, getFormattedName() + " nodes specific configs.");
        }

        public String getFormattedName() {
            return Arrays.stream(getSerializedName().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public static class NodeConfig extends ConfigBase {
        public final NodeGroup group;
        public final ConfigInt requiredFaces;
        public final ConfigInt tries;
        public final ConfigInt minY;
        public final ConfigInt maxY;

        private NodeConfig(NodeGroup group, int faces, int tries, int minY, int maxY) {
            this.group = group;
            this.requiredFaces = i(faces, 0, 6, "faces");
            this.tries = i(tries, 0, 512, "tries");
            this.minY = i(minY, "fromY");
            this.maxY = i(maxY, "toY");
        }

        @Override
        public String getName() {
            return group.getSerializedName();
        }
    }
}
