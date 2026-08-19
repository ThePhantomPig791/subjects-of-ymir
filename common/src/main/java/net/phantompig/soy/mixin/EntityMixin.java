package net.phantompig.soy.mixin;

import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.AABB;
import net.phantompig.soy.network.ScreenShakeMessage;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.sound.SoySounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityAccess, CommandSource {
    @Shadow public abstract EntityType<?> getType();
    @Shadow private Level level;
    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);
    @Shadow public int tickCount;
    @Shadow public abstract float distanceTo(Entity entity);
    @Shadow public abstract UUID getUUID();
    @Shadow public abstract AABB getBoundingBox();

    @Inject(method = "canBeCollidedWith", at = @At("RETURN"), cancellable = true)
    public void soy$canBeCollidedWith(CallbackInfoReturnable<Boolean> cir) {
        soy$returnTrueIfTitan(cir);
    }

    @Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
    public void soy$fireImmune(CallbackInfoReturnable<Boolean> cir) {
        soy$returnTrueIfTitan(cir);
    }

    @Inject(method = "createHoverEvent", at = @At("HEAD"), cancellable = true)
    public void soy$createHoverEvent(CallbackInfoReturnable<HoverEvent> cir) {
        if ((Object) this instanceof SoyPlayerExtension extension && extension.getTitanInstance().titan != null && extension.getTitanInstance().getProgress() > 0) {
            cir.setReturnValue(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(this.getType(), Util.NIL_UUID, extension.getTitanInstance().titan.getName())));
        }
    }

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    public void soy$playStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (this.soy$isTitan()) {
            if (this.tickCount % 3 == 0) {
                float scale = (float) this.getBoundingBox().getYsize();
                scale = scale / (scale + 20);
                this.playSound(SoySounds.TITAN_STEP.get(), 1.5f + scale, 0.5f + scale + (float) (0.05 * Math.random()));

                if (!this.level.isClientSide()) {
                    final float finalScale = scale;
                    this.level.getEntities((Entity) (Object) this, this.getBoundingBox().inflate(18)).forEach(e -> {
                        if (e instanceof ServerPlayer sp) {
                            final float strength = finalScale / this.distanceTo(sp);
                            SoyNetwork.NETWORK.sendToPlayer(sp, new ScreenShakeMessage(200 + (int) (300 * strength), 0.02f + 0.1f * strength));
                        }
                    });
                }

                ci.cancel();
            }
        }
    }

    @Unique
    public void soy$returnTrueIfTitan(CallbackInfoReturnable<Boolean> cir) {
        if (soy$isTitan()) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    public boolean soy$isTitan() {
        return (Object) this instanceof SoyPlayerExtension extension && extension.getTitanInstance().getProgress() > 0;
    }
}
