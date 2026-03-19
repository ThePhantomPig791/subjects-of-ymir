package net.phantompig.soy.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.phantompig.soy.SubjectsOfYmir;

public class SoyItemTags {
    public static final TagKey<Item> CAN_BE_USED_TO_SELF_STAB = create("can_be_used_to_self_stab");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, SubjectsOfYmir.rsrc(name));
    }

    public static void init() {}
}
