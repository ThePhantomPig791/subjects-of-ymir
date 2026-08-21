package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.util.RenderingUtil;
import net.threetag.palladium.client.screen.power.PowerTabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowerTabType.class)
public abstract class PowerTabTypeMixin {
    @WrapOperation(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    public void soy$drawDisplayIcon(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, Operation<Void> original) {
        original.call(instance, RenderingUtil.tabsAtlas(atlasLocation), x, y, uOffset, vOffset, uWidth, vHeight);
    }
}
