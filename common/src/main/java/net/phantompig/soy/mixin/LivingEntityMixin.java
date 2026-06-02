package net.phantompig.soy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.item.OdmAttachableAddonArmorItem;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.power.ability.BerserkAbility;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.threetag.palladium.power.ability.AbilityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void soy$isPushable(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof SoyPlayerExtension extension && extension.getTitanInstance().getProgress() > 0) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void soy$checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof SoyPlayerExtension ext && ext.getTitanInstance().titan != null) {
            if (ext.getTitanInstance().getProgress() > 0) {
                if (AbilityUtil.isTypeEnabled(ext.getTitanInstance().entity, SoyAbilities.BERSERK.get())) {
                    AbilityUtil.getEnabledInstances(ext.getTitanInstance().entity, SoyAbilities.BERSERK.get()).forEach(ability -> {
                        ability.setUniqueProperty(BerserkAbility.TIMER, ability.getProperty(BerserkAbility.MAX_TIME));
                    });
                    this.setHealth(20);
                    cir.setReturnValue(true);
                    return;
                }
                ext.getTitanInstance().titan.unshiftWithAdverseEffects(ext.getTitanInstance().entity);
                this.setHealth(8);
                cir.setReturnValue(true);
            } else if (AbilityUtil.isEnabled(ext.getTitanInstance().entity, SubjectsOfYmir.SHIFTER_POWER, "failsafe_unlock") && ext.getTitanInstance().getStamina() >= 0.7f * ext.getTitanInstance().getMaxStamina()) {
                ext.getTitanInstance().setCharge(30);
                ext.getTitanInstance().setStamina(20);
                this.setHealth(16);
                cir.setReturnValue(true);
            }
        }
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "CONSTANT", args = "floatValue=0.91", ordinal = 1))
    private float soy$reduceFrictionConstant(float original) {
        if (this.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof OdmAttachableAddonArmorItem) {
            return 0.96f;
        }
        return original;
    }
}
