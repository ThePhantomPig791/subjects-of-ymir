package net.phantompig.soy.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.TitanInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements SoyPlayerExtension {

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
    }
}