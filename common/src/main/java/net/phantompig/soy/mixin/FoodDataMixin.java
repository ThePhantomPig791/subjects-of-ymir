package net.phantompig.soy.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Shadow public abstract void setFoodLevel(int foodLevel);

    @Shadow public abstract void setExhaustion(float exhaustionLevel);

    @Shadow public abstract void setSaturation(float saturationLevel);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void soy$tick(Player player, CallbackInfo ci) {
        if (!(player instanceof SoyPlayerExtension ext) || ext.getTitanInstance().getProgress() == 0) return;
        this.setFoodLevel(20);
        this.setExhaustion(6);
        this.setSaturation(0);
        ci.cancel();
    }
}
