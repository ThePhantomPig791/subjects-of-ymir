package net.phantompig.soy.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.phantompig.soy.combat.ClientCombatSystem;
import net.phantompig.soy.property.SoyProperties;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public LocalPlayer player;

    @Unique
    public ClientCombatSystem soy$combatSystem;
    @Unique
    private void soy$reinstateCombatSystem() {
        this.soy$combatSystem = new ClientCombatSystem(this.player);
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    public void soy$startAttack(CallbackInfoReturnable<Boolean> ci) {
        if (this.soy$combatSystem == null || this.soy$combatSystem.player == null) soy$reinstateCombatSystem();
        if (this.player != null && SoyProperties.PROGRESS.get(this.player) > 0) {
            this.soy$combatSystem.attack();
            if (this.player.attackStrengthTicker >= 0) player.setYBodyRot(player.getYHeadRot());
            ci.cancel();
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    public void soy$continueAttack(boolean leftClick, CallbackInfo ci) {
        soy$cancelCallbackIfTitan(ci);
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    public void soy$startUseItemUseItemOn(CallbackInfo ci) {
        soy$cancelCallbackIfTitan(ci);
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;interactAt(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/EntityHitResult;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    public void soy$startUseItemInteractAt(CallbackInfo ci) {
        soy$cancelCallbackIfTitan(ci);
    }

    @Unique
    public void soy$cancelCallbackIfTitan(CallbackInfo ci) {
        if (this.player != null && SoyProperties.PROGRESS.get(this.player) > 0) {
            ci.cancel();
        }
    }
}
