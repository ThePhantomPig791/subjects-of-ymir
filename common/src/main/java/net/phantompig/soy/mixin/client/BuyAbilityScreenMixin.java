package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.phantompig.soy.util.RenderingUtil;
import net.threetag.palladium.client.screen.power.BuyAbilityScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BuyAbilityScreen.class)
public abstract class BuyAbilityScreenMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    public void soy$render(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, Operation<Void> original) {
        original.call(instance, RenderingUtil.windowAtlas(atlasLocation), x, y, uOffset, vOffset, uWidth, vHeight);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)I"))
    public int soy$renderText(GuiGraphics instance, Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow, Operation<Integer> original) {
        if (RenderingUtil.shouldRenderTitanGui()) {
            color = 15132390;
        }
        return original.call(instance, font, text, x, y, color, dropShadow);
    }
}
