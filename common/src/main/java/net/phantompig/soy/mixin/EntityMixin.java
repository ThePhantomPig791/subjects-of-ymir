package net.phantompig.soy.mixin;

import net.minecraft.commands.CommandSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityAccess, CommandSource {
    @Inject(method = "canBeCollidedWith", at = @At("RETURN"), cancellable = true)
    public void soy$canBeCollidedWith(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof SoyPlayerExtension extension && extension.getTitanInstance().getProgress() > 0) {
            cir.setReturnValue(true);
        }
    }
}
