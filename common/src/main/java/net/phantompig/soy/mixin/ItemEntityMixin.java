package net.phantompig.soy.mixin;

import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements TraceableEntity {
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    public void soy$playerTouch(Player player, CallbackInfo ci) {
        if (player instanceof SoyPlayerExtension ext && ext.getTitanInstance().getProgress() > 0) ci.cancel();
    }
}
