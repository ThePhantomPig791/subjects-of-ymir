package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.phantompig.soy.property.SoyProperties;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "turnPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;smoothCamera:Z", opcode = Opcodes.GETFIELD))
    public boolean soy$checkSmoothCamera(Options instance) {
        return this.minecraft.options.smoothCamera || (this.minecraft.player != null && SoyProperties.PROGRESS.get(this.minecraft.player) > 0 && SoyProperties.ATTACK_TIME.get(this.minecraft.player) > 10);
    }

    @ModifyVariable(method = "turnPlayer", at = @At(value = "STORE"), ordinal = 3)
    public double soy$adjustSmoothCameraFactor(double h, @Local(ordinal = 2) double g) {
        if (SoyProperties.PROGRESS.get(this.minecraft.player) == 0) return h;
        return g * 160 / SoyProperties.ATTACK_TIME.get(this.minecraft.player);
    }
}
