package net.phantompig.soy.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.entity.SoyDamageSources;
import org.jetbrains.annotations.NotNull;

public class BladeItem extends Item {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public BladeItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 3, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @NotNull
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.hurt(SoyDamageSources.selfStab(attacker.level(), attacker, target, target.position()), 3);
        return super.hurtEnemy(stack, target, attacker);
    }
}
