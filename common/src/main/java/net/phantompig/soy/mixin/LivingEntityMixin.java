package net.phantompig.soy.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.power.ability.AbilityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean addEffect(MobEffectInstance effectInstance);

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void soy$isPushable(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof SoyPlayerExtension extension && extension.getTitanInstance().getProgress() > 0) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void soy$checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof SoyPlayerExtension ext && ext.getTitanInstance().titan != null) {
            if (ext.getTitanInstance().getProgress() > 0) {
                ext.getTitanInstance().titan.unshiftWithAdverseEffects(ext.getTitanInstance().entity);
                this.setHealth(8);
                cir.setReturnValue(true);
            } else if (AbilityUtil.isEnabled(ext.getTitanInstance().entity, SubjectsOfYmir.SHIFTER_POWER, "failsafe_unlock") && ext.getTitanInstance().getStamina() >= 0.7f * ext.getTitanInstance().getMaxStamina()) {
                ext.getTitanInstance().setCharge(30);
                ext.getTitanInstance().setStamina(20);
                this.setHealth(16);
                cir.setReturnValue(true);
            }
        }
    }
}
