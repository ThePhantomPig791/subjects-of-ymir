package net.phantompig.soy.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.phantompig.soy.titan.TitanInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements SoyPlayerExtension {
    @Unique
    @NotNull
    private TitanInstance soy$titanInstance = new TitanInstance((Player) (Object) this);

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void soy$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        CompoundTag soyTag = compound.contains("Titan", Tag.TAG_COMPOUND) ? compound.getCompound("Titan") : new CompoundTag();
        soy$titanInstance = TitanInstance.fromTag((Player) (Object) this, soyTag);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void soy$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.put("Titan", this.soy$titanInstance.toTag());
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



    @ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 1), argsOnly = true)
    public float soy$modifyDamageTaken(float amount, DamageSource source) {
        if (soy$blocksDamageSource((LivingEntity) (Object) this, source)) {
            return amount * soy$getDamageReductionPercentage();
        }
        return amount;
    }

    @Unique
    private static boolean soy$blocksDamageSource(LivingEntity entity, DamageSource source) {
        if (!AbilityUtil.isTypeEnabled(entity, SoyAbilities.BLOCK.get())) return false;
        Entity directEntity = source.getDirectEntity();
        if (directEntity == null) return false;
        boolean pierce = directEntity instanceof Arrow proj && proj.getPierceLevel() > 0;
        if (source.is(DamageTypeTags.BYPASSES_SHIELD) || pierce) return false;
        Vec3 sourcePos = source.getSourcePosition();
        if (sourcePos == null) return false;
        Vec3 flatLookVec = entity.getLookAngle().multiply(1, 0, 1).normalize();
        Vec3 vecTo = sourcePos.vectorTo(entity.position()).multiply(1, 0, 1).normalize();
        return vecTo.dot(flatLookVec) < 0.0;
    }

    @Unique
    private static float soy$getDamageReductionPercentage() {
        return 0.2f;
    }
}