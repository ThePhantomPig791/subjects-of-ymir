package net.phantompig.soy.mixin;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.threetag.palladium.power.ability.AbilityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements Attackable {
    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true)
    public float soy$modifyDamageTaken(float amount, DamageSource source) {
        if (soy$blocksDamageSource((LivingEntity) (Object) this, source)) {
            return amount * soy$getDamageReductionPercentage();
        }
        return amount;
    }

    @Unique
    private static boolean soy$blocksDamageSource(LivingEntity entity, DamageSource source) {
        if (!AbilityUtil.isTypeEnabled(entity, SoyAbilities.BLOCK.get())) return false;
        Entity directEntity = source.getDirectEntity();
        if (directEntity == null) return false;
        boolean pierce = directEntity instanceof Arrow proj && proj.getPierceLevel() > 0;
        if (source.is(DamageTypeTags.BYPASSES_SHIELD) || pierce) return false;
        Vec3 sourcePos = source.getSourcePosition();
        if (sourcePos == null) return false;
        Vec3 flatLookVec = entity.getLookAngle().multiply(1, 0, 1).normalize();
        Vec3 vecTo = sourcePos.vectorTo(entity.position()).multiply(1, 0, 1).normalize();
        return vecTo.dot(flatLookVec) < 0.0;
    }

    @Unique
    private static float soy$getDamageReductionPercentage() {
        return 0.2f;
    }
}
