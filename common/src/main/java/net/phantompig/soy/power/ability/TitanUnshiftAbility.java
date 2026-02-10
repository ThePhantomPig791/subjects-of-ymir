package net.phantompig.soy.power.ability;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.icon.ItemIcon;
import net.threetag.palladium.util.property.BooleanProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladiumcore.event.EventResult;
import net.threetag.palladiumcore.event.LivingEntityEvents;

public class TitanUnshiftAbility extends Ability implements LivingEntityEvents.Death {
    public static final PalladiumProperty<Boolean> SPAWN_SKELETON = new BooleanProperty("spawn_skeleton").configurable("If true, leaves behind a decaying titan body");

    public TitanUnshiftAbility() {
        this.withProperty(SPAWN_SKELETON, true);

        this.withProperty(ICON, new ItemIcon(Items.ROTTEN_FLESH));
        this.withProperty(HIDDEN_IN_BAR, false);

        LivingEntityEvents.DEATH.register(this);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        var titanInstance = ((SoyPlayerExtension) entity).getTitanInstance();
        if (titanInstance.titan == null) return;

        if (enabled || titanInstance.forceUnshift) {
            titanInstance.setProgress(0);
            titanInstance.forceUnshift = false;

            titanInstance.resetScale();
            titanInstance.titan.unshift(entity);
        }
    }

    @Override
    public EventResult livingEntityDeath(LivingEntity entity, DamageSource damageSource) {
        if (!(entity instanceof SoyPlayerExtension soy)) return EventResult.pass();
        var titanInstance = soy.getTitanInstance();
        if (titanInstance.titan == null) return EventResult.pass();
        titanInstance.forceUnshift = true;
        return EventResult.cancel();
    }
}
