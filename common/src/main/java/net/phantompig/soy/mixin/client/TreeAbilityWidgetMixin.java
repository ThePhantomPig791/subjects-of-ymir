package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.util.RenderingUtil;
import net.threetag.palladium.client.screen.power.TreeAbilityWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TreeAbilityWidget.class)
public abstract class TreeAbilityWidgetMixin {
    @WrapOperation(method = {"drawDisplayIcon", "drawIcon", "drawHover"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    public void soy$drawDisplayIcon(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, Operation<Void> original) {
        original.call(instance, RenderingUtil.widgetsAtlas(atlasLocation), x, y, uOffset, vOffset, uWidth, vHeight);
    }

    @WrapOperation(method = "drawHover", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitNineSliced(Lnet/minecraft/resources/ResourceLocation;IIIIIIIII)V"))
    public void soy$drawHoverNineSliced(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int width, int height, int sliceSize, int uOffset, int vOffset, int textureWidth, int textureHeight, Operation<Void> original) {
        original.call(instance, RenderingUtil.widgetsAtlas(atlasLocation), x, y, width, height, sliceSize, uOffset, vOffset, textureWidth, textureHeight);
    }
}
