package net.phantompig.soy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.item.OdmAttachableAddonArmorItem;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.power.ability.BerserkAbility;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.titan.TitanInstance;
import net.phantompig.soy.util.ShapeUtil;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladiumcore.util.Platform;
import org.joml.Vector3f;
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

    @Shadow public abstract void remove(RemovalReason reason);

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void soy$isPushable(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof SoyPlayerExtension extension && extension.soy$getTitanInstance().getProgress() > 0) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    public void soy$hurtNape(LivingEntity entity, DamageSource damageSource, float damageAmount, Operation<Void> original) {
        if (entity instanceof SoyPlayerExtension ext && ext.soy$getTitanInstance().getProgress() > 0) {
            TitanInstance.NapeProtection napeProtection = ext.soy$getTitanInstance().getNapeProtectionType();
            if (napeProtection == TitanInstance.NapeProtection.ARMORED && damageAmount > 6) {
                PlayerUtil.playSoundToAll(entity.level(), entity.getX(), entity.getEyeY(), entity.getZ(), 32, SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.6f, 1.6f + (float) (0.1 * Math.random()));
                PlayerUtil.playSoundToAll(entity.level(), entity.getX(), entity.getEyeY(), entity.getZ(), 32, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.6f, 1.4f + (float) (0.1 * Math.random()));
                PlayerUtil.playSoundToAll(entity.level(), entity.getX(), entity.getEyeY(), entity.getZ(), 32, SoundEvents.METAL_PLACE, SoundSource.PLAYERS, 0.6f, 1.2f + (float) (0.1 * Math.random()));
            }
            if (napeProtection == TitanInstance.NapeProtection.NONE) {
                Vec3 sourcePos = damageSource.getSourcePosition();
                if (sourcePos != null) {
                    Vec3 napePos = entity.position().add(0, entity.getBbHeight() * 0.7, 0);
                    Vec3 vecToSource = sourcePos.subtract(napePos).normalize();
                    if (vecToSource.multiply(1, 12, 1).lengthSqr() < 28) {
                        vecToSource = vecToSource.normalize();
                        Vec3 bodyFacing = this.calculateViewVector(0, entity.getYRot());
                        if (bodyFacing.dot(vecToSource) < -0.2) {
                            damageAmount += 30;
                            if (damageSource.getDirectEntity() != null) {
                                damageAmount += (float) damageSource.getDirectEntity().getDeltaMovement().length();
                            }
                            PlayerUtil.playSoundToAll(
                                    entity.level(),
                                    napePos.x, napePos.y, napePos.z,
                                    32,
                                    SoySounds.TITAN_KILL.get(), SoundSource.PLAYERS,
                                    2, 1.6f + (float) (0.1 * Math.random())
                            );
                            PlayerUtil.playSoundToAll(
                                    entity.level(),
                                    napePos.x, napePos.y, napePos.z,
                                    32,
                                    SoySounds.TITAN_KILL.get(), SoundSource.PLAYERS,
                                    2, 0.8f + (float) (0.1 * Math.random())
                            );
                            PlayerUtil.spawnParticleForAll(
                                    entity.level(), 32,
                                    new DustParticleOptions(new Vector3f(0.6f, 0.05f, 0), 4),
                                    false,
                                    napePos.x, napePos.y, napePos.z,
                                    1.5f, 0.4f, 1.5f,
                                    0, 30
                            );
                        }
                        if (!Platform.isProduction()) {
                            ShapeUtil.highlightVector(entity.level(), napePos, bodyFacing.scale(10));
                            ShapeUtil.highlightVector(entity.level(), napePos, vecToSource.scale(10));
                        }
                    }
                }
            }
        }
        original.call(entity, damageSource, damageAmount);
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void soy$checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof SoyPlayerExtension ext && ext.soy$getTitanInstance().titan != null) {
            if (ext.soy$getTitanInstance().getProgress() > 0) {
                if (AbilityUtil.isTypeEnabled(ext.soy$getTitanInstance().entity, SoyAbilities.BERSERK.get())) {
                    AbilityUtil.getEnabledInstances(ext.soy$getTitanInstance().entity, SoyAbilities.BERSERK.get()).forEach(ability -> {
                        ability.setUniqueProperty(BerserkAbility.TIMER, ability.getProperty(BerserkAbility.MAX_TIME));
                    });
                    this.setHealth(20);
                    cir.setReturnValue(true);
                    return;
                }
                ext.soy$getTitanInstance().titan.unshiftWithAdverseEffects(ext.soy$getTitanInstance().entity);
                this.setHealth(8);
                cir.setReturnValue(true);
            } else if (AbilityUtil.isEnabled(ext.soy$getTitanInstance().entity, SubjectsOfYmir.SHIFTER_POWER, "failsafe_unlock") && ext.soy$getTitanInstance().getStamina() >= 0.7f * ext.soy$getTitanInstance().getMaxStamina()) {
                ext.soy$getTitanInstance().setCharge(30);
                ext.soy$getTitanInstance().setStamina(20);
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
