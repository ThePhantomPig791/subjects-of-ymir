package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.combat.ClientCombatHolder;
import net.phantompig.soy.item.OdmHandleItem;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.util.RenderingUtil;
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

    @Shadow
    private int screenHeight;

    @Shadow
    private int screenWidth;

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
                original.call(instance, atlasLocation, x + soy$getSplitHookOffset(), y, 7, 0, 7, 15);
            }
            if (Minecraft.getInstance().player.getItemInHand(leftHand).getItem() instanceof OdmHandleItem) {
                renderOriginal = false;
                original.call(instance, atlasLocation, x - soy$getSplitHookOffset(), y, 0, 0, 8, 15);
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

    @WrapOperation(method = {"renderHeart", "renderPlayerHealth", "renderExperienceBar"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    public void soy$drawTitanIcons(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, Operation<Void> original) {
        original.call(instance, RenderingUtil.vanillaIconsAtlas(atlasLocation), x, y, uOffset, vOffset, uWidth, vHeight);
    }

    @WrapOperation(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", ordinal = 4))
    public int soy$renderExperienceText(GuiGraphics instance, Font font, String text, int x, int y, int color, boolean dropShadow, Operation<Integer> original) {
        if (RenderingUtil.isTitan()) {
            color = 2454487;
        }
        return original.call(instance, font, text, x, y, color, dropShadow);
    }

    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;screenWidth:I", ordinal = 0))
    public int soy$centerHearts(Gui instance, Operation<Integer> original) {
        return original.call(instance) + (RenderingUtil.isTitan() ? 93 : 0);
    }
    @WrapOperation(method = "renderExperienceBar", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;screenHeight:I", ordinal = 1))
    public int soy$moveXpNumberDown(Gui instance, Operation<Integer> original) {
        return original.call(instance) + (RenderingUtil.isTitan() ? 4 : 0);
    }
    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;screenWidth:I", ordinal = 1))
    public int soy$centerBubbles(Gui instance, Operation<Integer> original) {
        return original.call(instance) - (RenderingUtil.isTitan() ? 109 : 0);
    }
    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVisibleVehicleHeartRows(I)I"))
    public int soy$moveBubblesUp(Gui instance, int vehicleHealth, Operation<Integer> original) {
        int extra = 0;
        if (RenderingUtil.isTitan() && Minecraft.getInstance().player != null) {
            extra += (int) (Minecraft.getInstance().player.getMaxHealth() / 20);
            extra += Minecraft.getInstance().player.getArmorValue() / 10;
        }
        return original.call(instance, vehicleHealth) + extra;
    }
}
