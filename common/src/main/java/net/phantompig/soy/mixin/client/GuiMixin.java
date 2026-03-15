package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.combat.ClientCombatHolder;
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
}
