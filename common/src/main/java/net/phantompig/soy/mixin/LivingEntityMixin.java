package net.phantompig.soy.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void setHealth(float health);

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void soy$isPushable(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof SoyPlayerExtension extension && extension.getTitanInstance().getProgress() > 0) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void soy$checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof SoyPlayerExtension ext && ext.getTitanInstance().titan != null && ext.getTitanInstance().getProgress() > 0) {
            ext.getTitanInstance().titan.unshiftWithAdverseEffects((LivingEntity) (Object) this);
            this.setHealth(1.0F);
            cir.setReturnValue(true);
        }
    }
}
