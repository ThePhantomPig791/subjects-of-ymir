package net.phantompig.soy.power.ability;

import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.EntityUtil;

import java.util.UUID;

public class GrabbingAbility extends Ability {
    public GrabbingAbility() {

    }

    @Override
    public void firstTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && SoyConfig.Server.allowGrabbing()) {
            GrabbingAbility.stopGrabbing(entity);
            HitResult hit = EntityUtil.rayTraceWithEntities(entity, entity.getBbHeight() * 1.2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE);
            if (hit instanceof EntityHitResult eHit && eHit.getEntity() instanceof LivingEntity living && living.getBbHeight() <= entity.getBbHeight() * 0.25) {
                GrabbingAbility.startGrabbing(entity, living);
            }
        } else {
            stopGrabbing(entity);
        }
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        UUID grabbing = getGrabbing(entity);
        if (grabbing != null && entity.level() instanceof ServerLevel level) {
            if (level.getEntity(grabbing) instanceof LivingEntity grabbingEntity) {
                grabbingEntity.setPos(entity.position().add(0, entity.getBbHeight() * 0.6f, 0));
            }
        }
    }

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        stopGrabbing(entity);
    }

    public static void startGrabbing(LivingEntity grabber, LivingEntity grabbed) {
        if (grabber.level() instanceof ServerLevel) {
            SoyProperties.GRABBED.set(grabbed, true);
            SoyProperties.GRABBING.set(grabber, grabbed.getUUID().toString());
        }
    }
    public static void stopGrabbing(LivingEntity entity) {
        UUID grabbing = getGrabbing(entity);
        if (grabbing != null && entity.level() instanceof ServerLevel level) {
            if (level.getEntity(grabbing) instanceof LivingEntity grabbedEntity) {
                grabbedEntity.resetFallDistance();
                Vec3 offset = entity.getLookAngle();
                offset = new Vec3(-offset.z, 0, -offset.x).normalize().scale(entity.getBbWidth()).add(0, entity.getBbHeight() * 0.5, 0);
                grabbedEntity.setPos(entity.position().add(offset));

                SoyProperties.GRABBED.set(grabbedEntity, false);
                SoyProperties.GRABBING.set(entity, "");
            }
        }
    }

    public static UUID getGrabbing(LivingEntity entity) {
        String p = SoyProperties.GRABBING.get(entity);
        if (p.isEmpty()) return Util.NIL_UUID;
        return UUID.fromString(p);
    }

    @Override
    public String getDocumentationDescription() {
        return "When enabled, allows the GrabEntity ability to grab other entities. When disabled, stops grabbing.";
    }
}
