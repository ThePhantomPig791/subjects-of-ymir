package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.util.RenderingUtil;
import net.threetag.palladium.client.screen.power.TreePowerTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TreePowerTab.class)
public abstract class TreePowerTabMixin {
    @WrapOperation(method = "drawContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"))
    public void soy$drawDisplayIcon(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight, Operation<Void> original, @Local(ordinal = 4) int i, @Local(ordinal = 5) int j, @Local(ordinal = 8) int m, @Local(ordinal = 9) int n) {
        if (RenderingUtil.shouldRenderTitanGui()) {
            width = height = 100;
            textureWidth = textureHeight = 100;
            x = (i % 100) + 100 * m;
            y = (j % 100) + 100 * n;
        }
        original.call(instance, atlasLocation, x, y, uOffset, vOffset, width, height, textureWidth, textureHeight);
    }
}
