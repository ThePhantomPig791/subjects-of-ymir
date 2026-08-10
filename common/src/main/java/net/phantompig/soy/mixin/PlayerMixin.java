package net.phantompig.soy.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.power.ability.BlockAbility;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.titan.TitanInstance;
import net.phantompig.soy.titan.hardening.HardeningSystem;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements SoyPlayerExtension, HardeningSystemHolder {
    @Unique
    @NotNull
    private TitanInstance soy$titanInstance = new TitanInstance((Player) (Object) this);

    @Unique
    @NotNull
    private HardeningSystem soy$hardeningSystem = new HardeningSystem((Player) (Object) this);

    private PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void soy$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        CompoundTag soyTag = compound.contains("Titan", Tag.TAG_COMPOUND) ? compound.getCompound("Titan") : new CompoundTag();
        soy$titanInstance = TitanInstance.fromTag((Player) (Object) this, soyTag);
        CompoundTag hardeningTag = compound.contains("Hardening", Tag.TAG_COMPOUND) ? compound.getCompound("Hardening") : new CompoundTag();
        soy$hardeningSystem = HardeningSystem.fromTag((Player) (Object) this, hardeningTag);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void soy$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.put("Titan", this.soy$titanInstance.toTag());
        compound.put("Hardening", this.soy$hardeningSystem.toTag());
    }

    @Override
    @NotNull
    public TitanInstance getTitanInstance() {
        return soy$titanInstance;
    }

    @Override
    public void setTitanInstance(TitanInstance instance) {
        soy$titanInstance = instance;
        instance.updateProperties();
    }

    @Override
    @NotNull
    public HardeningSystem soy$getHardeningSystem() {
        return this.soy$hardeningSystem;
    }


    @Inject(method = "causeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z"))
    public void soy$causeFallDamage(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (this.soy$titanInstance.titan != null && this.soy$titanInstance.getProgress() > 0) {
            this.soy$titanInstance.titan.onFall((LivingEntity) (Object) this, fallDistance);
        }
    }


    @Inject(method = "getAttackStrengthScale", at = @At(value = "HEAD"), cancellable = true)
    public void soy$getAttackStrengthScale(float adjustTicks, CallbackInfoReturnable<Float> cir) {
        if (SoyProperties.PROGRESS.get(this) > 0) {
            final int max = 2 * SoyProperties.ATTACK_TIME.get(this);
            cir.setReturnValue(Mth.clamp((((LivingEntity) (Object) this).attackStrengthTicker + (float) max) / max, 0.0F, 1.0F));
        }
    }


    @ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 1), argsOnly = true)
    public float soy$modifyDamageTaken(float amount, DamageSource source) {
        if (soy$blocksDamageSource((LivingEntity) (Object) this, source, amount)) {
            return amount * (1 - SoyConfig.Server.getBlockPercentage());
        }
        return amount;
    }

    @Unique
    private static boolean soy$blocksDamageSource(LivingEntity entity, DamageSource source, float amount) {
        if (!AbilityUtil.isTypeEnabled(entity, SoyAbilities.BLOCK.get())) return false;
        for (AbilityInstance inst : AbilityUtil.getEnabledInstances(entity, SoyAbilities.BLOCK.get())) {
            if (BlockAbility.shouldBlock(entity, source, amount, inst)) return true;
        }
        return false;
    }
}