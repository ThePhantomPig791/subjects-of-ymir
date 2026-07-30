package net.phantompig.soy.fabric.mixin.client;

import dev.emi.trinkets.SurvivalTrinketSlot;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SurvivalTrinketSlot.class)
public abstract class SurvivalTrinketSlotMixin {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true, remap = false, order = 2000)
    public void soy$mayPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof SoyPlayerExtension ext && ext.getTitanInstance().getProgress() > 0) cir.setReturnValue(false);
    }
}
