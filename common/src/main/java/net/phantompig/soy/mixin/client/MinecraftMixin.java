package net.phantompig.soy.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.phantompig.soy.combat.ClientCombatHolder;
import net.phantompig.soy.combat.ClientCombatSystem;
import net.phantompig.soy.item.BladeHandleItem;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.power.ability.AbilityUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements ClientCombatHolder {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow @Nullable public ClientLevel level;
    @Unique
    public ClientCombatSystem soy$combatSystem;

    @Override
    @Unique
    public ClientCombatSystem soy$getCombatSystem() {
        if (this.soy$combatSystem == null || this.soy$combatSystem.player == null) soy$reinstateCombatSystem();
        return this.soy$combatSystem;
    }

    @Unique
    private void soy$reinstateCombatSystem() {
        this.soy$combatSystem = new ClientCombatSystem(this.player);
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    public void soy$startAttack(CallbackInfoReturnable<Boolean> ci) {
        if (this.soy$combatSystem == null || this.soy$combatSystem.player == null) soy$reinstateCombatSystem();
        if (this.player != null && SoyProperties.PROGRESS.get(this.player) > 0 && !this.player.isSpectator() && AbilityUtil.getEnabledInstances(this.player, SoyAbilities.BLOCK.get()).isEmpty()) {
            this.soy$combatSystem.attack();
            if (this.player.attackStrengthTicker >= 0) player.setYBodyRot(player.getYHeadRot());
            ci.cancel();
        }
        if (this.player != null && this.player.getMainHandItem().getItem() instanceof BladeHandleItem && this.player.getOffhandItem().getItem() instanceof BladeHandleItem) {
            ci.cancel();
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    public void soy$continueAttack(boolean leftClick, CallbackInfo ci) {
        soy$cancelCallbackIfTitan(ci);
        if (this.soy$combatSystem != null && !ci.isCancelled()) {
            if (leftClick) {
                if (!this.soy$combatSystem.heldLeftClick) {
                    this.soy$combatSystem.startHeldAttack();
                }
            } else if (this.soy$combatSystem.heldLeftClick) {
                this.soy$combatSystem.stopHeldAttack();
            }
            if (this.soy$combatSystem.heldLeftClick) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    public void soy$startUseItemUseItemOn(CallbackInfo ci) {
        soy$cancelCallbackIfTitan(ci);
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;interactAt(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/EntityHitResult;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    public void soy$startUseItemInteractAt(CallbackInfo ci) {
        soy$cancelCallbackIfTitan(ci);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void soy$tick(CallbackInfo ci) {
        this.soy$getCombatSystem().tick();
        if (this.level == null && this.soy$combatSystem.heldLeftClick) this.soy$combatSystem.heldLeftClick = false;
    }

    @Unique
    public void soy$cancelCallbackIfTitan(CallbackInfo ci) {
        if (this.player != null && SoyProperties.PROGRESS.get(this.player) > 0) {
            ci.cancel();
        }
    }
}
