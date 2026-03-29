package net.phantompig.soy.power.ability;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.entity.SoyDamageSources;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AnimationTimer;
import net.threetag.palladium.util.property.*;

public class BiteAbility extends Ability implements AnimationTimer {
    public static final PalladiumProperty<Integer> TIME = new IntegerProperty("time").configurable("The time the ability needs to be active for in order to succeed and deal damage");
    public static final PalladiumProperty<Integer> AMOUNT = new IntegerProperty("amount").configurable("The amount of damage to deal. Set to -1 to automatically use the entity's minimum damage threshold");
    public static final PalladiumProperty<String> DAMAGE_TYPE = new StringProperty("damage_type").configurable("The damage source to use. Accepts \"bite\" or \"stab\"");

    public static final PalladiumProperty<Integer> TIMER = new IntegerProperty("timer").sync(SyncType.NONE);
    public static final PalladiumProperty<Integer> PREV_TIMER = new IntegerProperty("prev_timer").sync(SyncType.NONE);

    public BiteAbility() {
        this.withProperty(TIME, 10);
        this.withProperty(AMOUNT, -1);
        this.withProperty(DAMAGE_TYPE, "bite");
    }

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(TIMER, 0);
        manager.register(PREV_TIMER, 0);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance instance, IPowerHolder holder, boolean enabled) {
        int timer = instance.getProperty(TIMER);
        instance.setUniqueProperty(PREV_TIMER, timer);

        if (enabled && timer < instance.getProperty(TIME)) {
            instance.setUniqueProperty(TIMER, timer + 1);
        } else if (!enabled && timer > 0) {
            instance.setUniqueProperty(TIMER, timer - 1);
        }
    }

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled) {
            if (entry.getProperty(TIMER).equals(entry.getProperty(TIME)) && !entity.isCrouching()) {
                DamageSource source = switch (entry.getProperty(DAMAGE_TYPE)) {
                    case "stab" -> SoyDamageSources.selfStab(entity.level(), null, entity, null);
                    default -> SoyDamageSources.selfBite(entity.level(), null, entity, null);
                };
                if (entity instanceof SoyPlayerExtension ext) {
                    var amount = entry.getProperty(AMOUNT);
                    entity.hurt(source, amount == -1 ? ext.getTitanInstance().getDamageThreshold() : amount);
                    ext.getTitanInstance().canShiftTicks = 60;
                }
            }
        }
    }

    @Override
    public float getAnimationValue(AbilityInstance instance, float partialTick) {
        return Mth.lerp(partialTick, instance.getProperty(PREV_TIMER), instance.getProperty(TIMER)) / (float) instance.getProperty(TIME);
    }

    @Override
    public float getAnimationTimer(AbilityInstance instance, float partialTick, boolean maxedOut) {
        if (maxedOut) {
            return instance.getProperty(TIME);
        }
        return instance.getProperty(TIMER);
    }

    @Override
    public String getDocumentationDescription() {
        return "When held for the specified number of ticks and then released, the specified damage will be dealt to the entity. Linked to a corresponding animation.";
    }
}
