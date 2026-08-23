package net.phantompig.soy.power.ability;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;
import net.threetag.palladium.util.property.SyncType;

public class BerserkAbility extends Ability {
    public static final PalladiumProperty<Integer> MAX_TIME = new IntegerProperty("max_time").configurable("The maximum time that the berserk state will last for before unshifting");

    public static final PalladiumProperty<Integer> TIMER = new IntegerProperty("timer").sync(SyncType.EVERYONE);

    public BerserkAbility() {
        this.withProperty(MAX_TIME, 600);
    }

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(TIMER, -1);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && entity instanceof SoyPlayerExtension ext && ext.soy$getTitanInstance().titan != null) {
            int timer = entry.getProperty(TIMER);
            final int maxTimer = entry.getProperty(MAX_TIME);
            if (timer == maxTimer) { // first tick
                entity.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_BOOST,
                        maxTimer,
                        1,
                        false,
                        false
                ));
                entity.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        maxTimer,
                        2,
                        false,
                        false
                ));
                entity.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        (int) (maxTimer * 0.8f),
                        2,
                        false,
                        false
                ));
                entity.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION,
                        maxTimer,
                        4,
                        false,
                        false
                ));
                entity.addEffect(new MobEffectInstance(
                        MobEffects.FIRE_RESISTANCE,
                        maxTimer,
                        0,
                        false,
                        false
                ));
                entity.level().explode(
                        entity,
                        entity.getX(),
                        entity.getY() + entity.getEyeHeight() * 0.5f,
                        entity.getZ(),
                        7,
                        true,
                        Level.ExplosionInteraction.BLOCK
                );
                entry.setUniqueProperty(TIMER, timer - 1);
            } else if (timer == 0) { // last tick
                entity.level().explode(
                        entity,
                        entity.getX(),
                        entity.getY() + entity.getEyeHeight() * 0.5f,
                        entity.getZ(),
                        8,
                        true,
                        Level.ExplosionInteraction.BLOCK
                );
                ext.soy$getTitanInstance().titan.unshiftWithAdverseEffects(entity, false, false);
                entry.setUniqueProperty(TIMER, -1);
            } else if (timer > 0) { // every tick
                entry.setUniqueProperty(TIMER, timer - 1);
                ext.soy$getTitanInstance().exhaustSafe(1);
            }
        }
    }

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        entry.setUniqueProperty(TIMER, -1);
    }
}
