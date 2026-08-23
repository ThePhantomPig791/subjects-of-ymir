package net.phantompig.soy.power.ability;

import net.minecraft.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.network.TitanAttackAnimationMessage;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.EntityUtil;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;
import net.threetag.palladium.util.property.SyncType;

import java.util.UUID;

public class GrabbingAbility extends Ability {
    public static final PalladiumProperty<Integer> MAX_TIME = new IntegerProperty("max_time").configurable("How long it takes for the raycast to start (this should equal how long it takes for the animation to get to the point where the fist hits the ground)");

    public static final PalladiumProperty<Integer> TIMER = new IntegerProperty("timer").sync(SyncType.NONE);


    public GrabbingAbility() {
        this.withProperty(MAX_TIME, 5);
    }

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(TIMER, -1);
    }

    @Override
    public void firstTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        stopGrabbing(entity);
        if (enabled && SoyConfig.Server.allowGrabbing()) {
            entry.setUniqueProperty(TIMER, 0);
            if (entity instanceof ServerPlayer sp) {
                String animationId;
                if (entity.getXRot() > 30) {
                    animationId = "grab_ground";
                } else {
                    animationId = "grab_air";
                }
                SoyNetwork.NETWORK.sendToPlayer(sp, new TitanAttackAnimationMessage(sp, animationId));
            }

            Vec3 soundPos = entity.getEyePosition().add(entity.getLookAngle().scale(getGrabReach(entity)));
            PlayerUtil.playSoundToAll(entity.level(), soundPos.x, soundPos.y, soundPos.z, 16, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 2, 0.7f + (float) (0.05 * Math.random()));
        }
    }

    public void raycastGrab(LivingEntity entity) {
        if (!SoyConfig.Server.allowGrabbing()) return;
        HitResult hit = EntityUtil.rayTraceWithEntities(entity, getGrabReach(entity), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE);
        if (hit instanceof EntityHitResult eHit && eHit.getEntity() instanceof LivingEntity livingTarget && canGrab(entity, livingTarget)) {
            startGrabbing(entity, livingTarget);
        }
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        int timer = entry.getProperty(TIMER);
        if (timer == -1) {
            UUID grabbing = getGrabbing(entity);
            if (grabbing != null) {
                LivingEntity grabbed = null;
                for (Entity e : entity.level().getEntities(entity, entity.getBoundingBox().inflate(40))) {
                    if (e instanceof LivingEntity living && grabbing.equals(e.getUUID())) {
                        grabbed = living;
                        break;
                    }
                }
                if (grabbed == null) return;
                grabbed.setPos(entity.position().add(0, entity.getBbHeight() * 0.6f, 0));
            }
        } else {
            if (timer == entry.getProperty(MAX_TIME)) {
                entry.setUniqueProperty(TIMER, -1);
                raycastGrab(entity);
            } else {
                entry.setUniqueProperty(TIMER, timer + 1);
            }
        }
    }

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        stopGrabbing(entity, entity.isCrouching());
    }

    public static boolean canGrab(LivingEntity grabber, LivingEntity held) {
        return held.getBbHeight() <= grabber.getBbHeight() * 0.25;
    }
    public static void startGrabbing(LivingEntity grabber, LivingEntity grabbed) {
        if (grabber.level() instanceof ServerLevel) {
            SoyProperties.GRABBED.set(grabbed, true);
            SoyProperties.GRABBING.set(grabber, grabbed.getUUID().toString());
        }
    }
    public static void stopGrabbing(LivingEntity entity) {
        stopGrabbing(entity, false);
    }
    public static void stopGrabbing(LivingEntity entity, boolean shouldThrow) {
        UUID grabbing = getGrabbing(entity);
        if (grabbing != null && entity.level() instanceof ServerLevel level) {
            if (level.getEntity(grabbing) instanceof LivingEntity grabbedEntity) {
                grabbedEntity.resetFallDistance();
                Vec3 offset = entity.getLookAngle();
                offset = new Vec3(-offset.z, 0, offset.x).normalize().scale(entity.getBbWidth()).add(0, entity.getBbHeight() * 0.5, 0);
                grabbedEntity.setPos(entity.position().add(offset));

                if (shouldThrow && entity instanceof SoyPlayerExtension ext && ext.soy$getTitanInstance().titan != null) {
                    entity.swing(entity.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
                    grabbedEntity.setDeltaMovement(entity.getLookAngle().scale(1 + Math.pow(ext.soy$getTitanInstance().titan.stats.extraDamage, 0.75)).add(entity.getDeltaMovement()));
                    if (grabbedEntity instanceof ServerPlayer p) {
                        p.connection.send(new ClientboundSetEntityMotionPacket(p));
                    }

                    Vec3 soundPos = entity.getEyePosition().add(entity.getLookAngle().scale(getGrabReach(entity)));
                    PlayerUtil.playSoundToAll(entity.level(), soundPos.x, soundPos.y, soundPos.z, 16, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 2, 0.9f + (float) (1 / (1 + Math.exp(-0.05 * entity.getBbHeight()))));
                    PlayerUtil.spawnParticleForAll(
                            entity.level(), 32, ParticleTypes.CLOUD, false,
                            soundPos.x, soundPos.y, soundPos.z,
                            0.3f, 0.1f, 0.3f,
                            0.2f, 6
                    );
                }

                SoyProperties.GRABBED.set(grabbedEntity, false);
                SoyProperties.GRABBING.set(entity, "");

                PlayerUtil.playSoundToAll(entity.level(), entity.getX(), entity.getY(), entity.getZ(), 16, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.5f, 0.5f + (float) (0.05 * Math.random()));
            }
        }
    }

    public static UUID getGrabbing(LivingEntity entity) {
        String p = SoyProperties.GRABBING.get(entity);
        if (p.isEmpty()) return Util.NIL_UUID;
        return UUID.fromString(p);
    }

    public static LivingEntity getGrabber(LivingEntity held) {
        LivingEntity grabber = null;
        for (Entity e : held.level().getEntities(held, held.getBoundingBox().inflate(40))) {
            if (e instanceof LivingEntity living && GrabbingAbility.getGrabbing(living).equals(held.getUUID())) {
                grabber = living;
                break;
            }
        }
        return grabber;
    }

    public static boolean isGrabbingSomething(LivingEntity entity) {
        if (entity == null) return false;
        return !SoyProperties.GRABBING.get(entity).isEmpty();
    }

    public static double getGrabReach(LivingEntity entity) {
        return entity.getBbHeight() - entity.getBbHeight() * 0.8 * Math.cos(Math.max(0, 0.8 * entity.getXRot()));
    }

    @Override
    public String getDocumentationDescription() {
        return "When enabled, allows the GrabEntity ability to grab other entities. When disabled, stops grabbing.";
    }
}
