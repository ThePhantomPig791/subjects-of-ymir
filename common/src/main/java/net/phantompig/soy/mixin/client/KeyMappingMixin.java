package net.phantompig.soy.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.client.SoyKeyMappings;
import net.phantompig.soy.item.OdmHandleItem;
import net.phantompig.soy.network.OdmHandlePressMessage;
import net.phantompig.soy.network.SoyNetwork;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Final
    @Shadow
    private static Map<InputConstants.Key, KeyMapping> MAP;

    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void soy$click(InputConstants.Key key, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null) {
            KeyMapping keyMapping = MAP.get(key);

            if (keyMapping == null) return;

            if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof OdmHandleItem) {
                if (!keyMapping.equals(SoyKeyMappings.MAIN_HOOK) && SoyKeyMappings.MAIN_HOOK.same(keyMapping)) {
                    SoyNetwork.NETWORK.sendToServer(new OdmHandlePressMessage(true));
                    ci.cancel();
                }
            }
            if (Minecraft.getInstance().player.getOffhandItem().getItem() instanceof OdmHandleItem) {
                if (!keyMapping.equals(SoyKeyMappings.OFF_HOOK) && SoyKeyMappings.OFF_HOOK.same(keyMapping)) {
                    SoyNetwork.NETWORK.sendToServer(new OdmHandlePressMessage(false));
                    ci.cancel();
                }
            }
        }
    }
}