package net.phantompig.soy.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.HumanoidArm;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.item.OdmHandleItem;
import net.phantompig.soy.network.OdmHandlePressMessage;
import net.phantompig.soy.network.SoyNetwork;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void soy$click(InputConstants.Key key, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null) {
            boolean rightHanded = Minecraft.getInstance().player.getMainArm() == HumanoidArm.RIGHT;
            if ((rightHanded ? Minecraft.getInstance().player.getMainHandItem() : Minecraft.getInstance().player.getOffhandItem()).getItem() instanceof OdmHandleItem) {
                if (key.getValue() == SoyConfig.Client.getRightHookKeycode()) {
                    SoyNetwork.NETWORK.sendToServer(new OdmHandlePressMessage(true));
                    ci.cancel();
                }
            }
            if ((!rightHanded ? Minecraft.getInstance().player.getMainHandItem() : Minecraft.getInstance().player.getOffhandItem()).getItem() instanceof OdmHandleItem) {
                if (key.getValue() == SoyConfig.Client.getLeftHookKeycode()) {
                    SoyNetwork.NETWORK.sendToServer(new OdmHandlePressMessage(false));
                    ci.cancel();
                }
            }
        }
    }
}