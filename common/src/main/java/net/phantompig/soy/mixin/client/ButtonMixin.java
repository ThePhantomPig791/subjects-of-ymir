package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.util.RenderingUtil;
import net.threetag.palladium.client.screen.components.TextWithIconButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {TextWithIconButton.class, AbstractButton.class})
public abstract class ButtonMixin {
    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitNineSliced(Lnet/minecraft/resources/ResourceLocation;IIIIIIIIII)V"))
    public void soy$renderWidget(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int width, int height, int sliceWidth, int sliceHeight, int uWidth, int vHeight, int textureX, int textureY, Operation<Void> original) {
        original.call(instance, RenderingUtil.vanillaWidgetsAtlas(atlasLocation), x, y, width, height, sliceWidth, sliceHeight, uWidth, vHeight, textureX, textureY);
    }
}
