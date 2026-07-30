package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.combat.ClientCombatHolder;
import net.phantompig.soy.item.OdmHandleItem;
import net.phantompig.soy.property.SoyProperties;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Unique
    private static final ResourceLocation soy$ICONS = SubjectsOfYmir.rsrc("textures/gui/icons.png");

    @Unique
    private int soy$maxStageTimer = 0;

    @Shadow @Final
    private Minecraft minecraft;

    @Shadow protected int screenHeight;

    @Shadow protected int screenWidth;

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;attackIndicator()Lnet/minecraft/client/OptionInstance;"))
    public void soy$renderStageAdvanceIcon(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.minecraft instanceof ClientCombatHolder com && SoyProperties.PROGRESS.get(this.minecraft.player) > 0) {
            if (this.minecraft.player == null || this.minecraft.player.attackStrengthTicker < 0) return;

            int nextStageTimer = com.soy$getCombatSystem().nextStageTimer;
            if (nextStageTimer > soy$maxStageTimer) soy$maxStageTimer = nextStageTimer;
            if (nextStageTimer == 0) {
                soy$maxStageTimer = 0;
                return;
            }

            int j = this.screenHeight / 2 - 7 + 16, k = this.screenWidth / 2 - 8;
            float d = (float) nextStageTimer / soy$maxStageTimer;
            guiGraphics.innerBlit(soy$ICONS, k, k + 16, j, j + 16, 0, 0, 16 / 256f, 0, 16 / 256f, d, d, d, d);
        }
    }

    @ModifyExpressionValue(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I", opcode = Opcodes.GETFIELD))
    public int soy$removeHungerBar(int original) {
        if (SoyProperties.PROGRESS.get(this.minecraft.player) > 0) {
            return -1;
        } else return original;
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0))
    public void soy$renderDoubleCrosshair(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, Operation<Void> original) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCrouching()) {
            boolean renderOriginal = true;
            final InteractionHand rightHand = Minecraft.getInstance().player.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            final InteractionHand leftHand = Minecraft.getInstance().player.getMainArm() == HumanoidArm.LEFT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (Minecraft.getInstance().player.getItemInHand(rightHand).getItem() instanceof OdmHandleItem) {
                renderOriginal = false;
                original.call(instance, soy$ICONS, x + soy$getSplitHookOffset(), y, 15, 32, 15, 15);
            }
            if (Minecraft.getInstance().player.getItemInHand(leftHand).getItem() instanceof OdmHandleItem) {
                renderOriginal = false;
                original.call(instance, soy$ICONS, x - soy$getSplitHookOffset(), y, 0, 32, 15, 15);
            }
            if (renderOriginal) {
                original.call(instance, atlasLocation, x, y, uOffset, vOffset, uWidth, vHeight);
            }
        } else {
            original.call(instance, atlasLocation, x, y, uOffset, vOffset, uWidth, vHeight);
        }
    }

    @Unique
    private int soy$getSplitHookOffset() {
        return this.screenWidth / 8;
    }
}
