package net.phantompig.soy.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.item.OdmAttachableAddonArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Inject(method = "hurtAndBreak", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
    public void soy$itemBreak(int amount, LivingEntity entity, Consumer<LivingEntity> onBroken, CallbackInfo ci) {
        if (this.getItem() instanceof OdmAttachableAddonArmorItem odm) {
            odm.getComponents((ItemStack) (Object) this).forEach(stack -> {
                ItemEntity itemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stack);
                entity.level().addFreshEntity(itemEntity);
            });
        }
    }
}
