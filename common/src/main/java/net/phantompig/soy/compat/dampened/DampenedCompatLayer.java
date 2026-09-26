package net.phantompig.soy.compat.dampened;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.compat.SoyCompatLayer;
import net.threetag.palladium.compat.curiostinkets.CuriosTrinketsUtil;

import java.util.List;
import java.util.Objects;

public class DampenedCompatLayer implements SoyCompatLayer {
    public static final ResourceLocation DAMPENING_CUFFS_ITEM_ID = new ResourceLocation("dampened:dampening_cuffs");
    public static final List<ResourceLocation> HANDCUFFS = List.of(
            DAMPENING_CUFFS_ITEM_ID,
            new ResourceLocation("dampened:gold_cuffs"),
            new ResourceLocation("dampened:magic_cuffs")
    );
    public static final List<ResourceLocation> EFFECT_IDS = List.of(
            new ResourceLocation("dampened:dark_matter"),
            new ResourceLocation("dampened:dampen")
    );

    @Override
    public String getName() {
        return "Dampened (Addonpack)";
    }

    @Override
    public boolean shouldDisableShifterAbilities(LivingEntity entity) {
        return entity.getActiveEffects().stream().map(inst -> BuiltInRegistries.MOB_EFFECT.getKey(inst.getEffect())).filter(Objects::nonNull).anyMatch(EFFECT_IDS::contains);
    }

    @Override
    public boolean shouldDisableBite(LivingEntity entity) {
        for (ItemStack stack : CuriosTrinketsUtil.getInstance().getItemsInSlot(entity, "bracelet")) {
            if (HANDCUFFS.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()))) return true;
        }
        return false;
    }
}
