package net.phantompig.soy.mixin;

import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityAccess;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityAccess, CommandSource {
    @Shadow public abstract EntityType<?> getType();

    @Inject(method = "canBeCollidedWith", at = @At("RETURN"), cancellable = true)
    public void soy$canBeCollidedWith(CallbackInfoReturnable<Boolean> cir) {
        soy$returnTrueIfTitan(cir);
    }

    @Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
    public void soy$fireImmune(CallbackInfoReturnable<Boolean> cir) {
        soy$returnTrueIfTitan(cir);
    }

    @Inject(method = "createHoverEvent", at = @At("HEAD"), cancellable = true)
    public void soy$createHoverEvent(CallbackInfoReturnable<HoverEvent> cir) {
        if ((Object) this instanceof SoyPlayerExtension extension && extension.getTitanInstance().titan != null && extension.getTitanInstance().getProgress() > 0) {
            cir.setReturnValue(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(this.getType(), Util.NIL_UUID, extension.getTitanInstance().titan.getName())));
        }
    }

    @Unique
    public void soy$returnTrueIfTitan(CallbackInfoReturnable<Boolean> cir) {
        if (soy$isTitan()) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    public boolean soy$isTitan() {
        return (Object) this instanceof SoyPlayerExtension extension && extension.getTitanInstance().getProgress() > 0;
    }
}
