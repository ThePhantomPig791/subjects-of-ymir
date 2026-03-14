package net.phantompig.soy.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.phantompig.soy.SubjectsOfYmir;

public class SoyBlockTags {
    public static final TagKey<Block> IRON_BAMBOO_CAN_ROOT_THROUGH = create("iron_bamboo_can_root_through");
    public static final TagKey<Block> HARDENING_CAN_REPLACE = create("hardening_can_replace");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, SubjectsOfYmir.rsrc(name));
    }

    public static void init() {}
}
