package net.phantompig.soy.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.entity.SoyDamageSources;

public class BladeItem extends Item {
    public BladeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.hurt(SoyDamageSources.selfStab(attacker.level(), attacker), 3);
        return super.hurtEnemy(stack, target, attacker);
    }
}
