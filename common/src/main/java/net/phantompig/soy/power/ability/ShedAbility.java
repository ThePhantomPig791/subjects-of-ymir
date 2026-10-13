package net.phantompig.soy.power.ability;

import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.titan.armor.ArmorSystemHolder;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.property.*;

public class ShedAbility extends Ability {
    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && !entity.level().isClientSide() && entity instanceof ArmorSystemHolder arh) {
            arh.soy$getArmorSystem().shed();
        }
    }
}
