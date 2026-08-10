package net.phantompig.soy.power.ability;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SoyConfig;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AnimationTimer;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;
import net.threetag.palladium.util.property.SyncType;

public class BlockAbility extends Ability implements AnimationTimer {
    private static final int MAX_TIMER = 8;

    public static final PalladiumProperty<Integer> TIMER = new IntegerProperty("timer").sync(SyncType.NONE);
    public static final PalladiumProperty<Integer> PREV_TIMER = new IntegerProperty("prev_timer").sync(SyncType.NONE);

    public BlockAbility() {}

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(TIMER, 0);
        manager.register(PREV_TIMER, 0);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance instance, IPowerHolder holder, boolean enabled) {
        int timer = instance.getProperty(TIMER);
        instance.setUniqueProperty(PREV_TIMER, timer);

        if (enabled && timer < MAX_TIMER) {
            instance.setUniqueProperty(TIMER, timer + 1);
        } else if (!enabled && timer > 0) {
            instance.setUniqueProperty(TIMER, timer - 1);
        }
    }

    @Override
    public float getAnimationValue(AbilityInstance instance, float partialTick) {
        return Mth.lerp(partialTick, instance.getProperty(PREV_TIMER), instance.getProperty(TIMER)) / (float) MAX_TIMER;
    }

    @Override
    public float getAnimationTimer(AbilityInstance instance, float partialTick, boolean maxedOut) {
        if (maxedOut) {
            return MAX_TIMER;
        }
        return instance.getProperty(TIMER);
    }

    @Override
    public String getDocumentationDescription() {
        return "Blocks 80% of incoming damage. Linked to an animation. If an attack is blocked within [5, 10] ticks of activation, half of the blocked damage will be returned to the attacker.";
    }

    public static boolean shouldBlock(LivingEntity entity, DamageSource source, float amount, AbilityInstance entry) {
        Entity directEntity = source.getDirectEntity();
        if (directEntity == null) return false;
        boolean pierce = directEntity instanceof Arrow proj && proj.getPierceLevel() > 0;
        if (source.is(DamageTypeTags.BYPASSES_SHIELD) || pierce) return false;
        Vec3 sourcePos = source.getSourcePosition();
        if (sourcePos == null) return false;
        Vec3 flatLookVec = entity.getLookAngle().multiply(1, 0, 1).normalize();
        Vec3 vecTo = sourcePos.vectorTo(entity.position()).multiply(1, 0, 1).normalize();
        if (vecTo.dot(flatLookVec) < 0.0) {
            // parry
            if (entry.getProperty(TIMER) >= 5 && entry.getProperty(TIMER) <= 10) {
                var attacker = source.getEntity();
                if (attacker != null) {
                    attacker.hurt(source, amount * SoyConfig.Server.getBlockPercentage() * 0.5f);
                }
            }
            return true;
        } else return false;
    }
}
