package net.phantompig.soy.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void soy$isPushable(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof SoyPlayerExtension extension && extension.getTitanInstance().getProgress() > 0) {
            cir.setReturnValue(false);
        }
    }
}
