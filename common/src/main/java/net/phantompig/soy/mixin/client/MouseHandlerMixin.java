package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladiumcore.util.Platform;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyExpressionValue(method = "turnPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;smoothCamera:Z", opcode = Opcodes.GETFIELD))
    public boolean soy$checkSmoothCamera(boolean original) {
        return original || (this.minecraft.player != null && SoyProperties.PROGRESS.get(this.minecraft.player) > 0 && SoyProperties.ATTACK_TIME.get(this.minecraft.player) > 10);
    }

    @ModifyVariable(method = "turnPlayer", at = @At(value = "STORE"), ordinal = 3)
    public double soy$adjustSmoothCameraFactor(double h, @Local(ordinal = 2) double g) {
        if (SoyProperties.PROGRESS.get(this.minecraft.player) == 0) return h;
        int magicNumber = Platform.isForge() ? 25 : 1; // don't ask
        return g * 160 / SoyProperties.ATTACK_TIME.get(this.minecraft.player) / magicNumber;
    }
}
