package net.phantompig.soy.power.ability;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.property.*;

public class ClimbingAbility extends Ability {
    public static final PalladiumProperty<Double> STRENGTH = new DoubleProperty("strength").configurable("The strength for each jump off the wall.");

    public static final PalladiumProperty<Boolean> WAS_CROUCHING = new BooleanProperty("was_crouching").sync(SyncType.NONE);

    public ClimbingAbility() {
        this.withProperty(STRENGTH, 0.1);
    }

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(WAS_CROUCHING, false);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled) {
            if (entity.isCrouching()) {
                if (entity.horizontalCollision) {
                    if (!entry.getProperty(WAS_CROUCHING)) entry.setUniqueProperty(WAS_CROUCHING, true);
                    setMovement(entity, Vec3.ZERO);
                }
            } else if (entry.getProperty(WAS_CROUCHING)) {
                entry.setUniqueProperty(WAS_CROUCHING, false);
                setMovement(entity, entity.getLookAngle().lerp(new Vec3(0, 1, 0), 0.5).scale(entry.getProperty(STRENGTH)));
                PlayerUtil.playSoundToAll(entity.level(), entity.getX(), entity.getEyeY(), entity.getZ(), 32, SoundEvents.BAT_TAKEOFF, SoundSource.PLAYERS, 2, (float) (0.7 + 0.2 * Math.random()));
            }
        }
    }

    private static void setMovement(Entity entity, Vec3 vec) {
        entity.setDeltaMovement(vec);
        if (entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }

    @Override
    public String getDocumentationDescription() {
        return "Allows the entity to cling to walls by crouching then climb up incrementally by uncrouching.";
    }
}
