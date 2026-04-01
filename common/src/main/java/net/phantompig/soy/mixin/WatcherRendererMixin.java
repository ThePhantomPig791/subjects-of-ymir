package net.phantompig.soy.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import net.phantompig.soy.item.SoyItems;
import net.threetag.palladium.client.renderer.WatcherRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatcherRenderer.class)
public abstract class WatcherRendererMixin {
    @Shadow private int ticksTilOccurrence;

    @Shadow public abstract float getVisibility(float partialTick);

    @Inject(method = "isAprilFools", at = @At("RETURN"), cancellable = true, remap = false)
    private static void soy$isAprilFools(CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(SoyItems.INJECTION.get())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "clientLevelTick", at = @At("HEAD"))
    private void soy$clientLevelTick(Minecraft minecraft, ClientLevel clientLevel, CallbackInfo ci) {
        if (this.getVisibility(1) == 0 && this.ticksTilOccurrence > 100 && Minecraft.getInstance().player != null && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(SoyItems.INJECTION.get())) {
            this.ticksTilOccurrence = 100;
        }
    }
}
