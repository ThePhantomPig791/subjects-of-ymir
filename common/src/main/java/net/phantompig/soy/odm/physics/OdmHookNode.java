package net.phantompig.soy.odm.physics;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.odm.OdmServerLevel;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OdmHookNode extends OdmNode {
    public OdmHookNode(OdmServerLevel odmLevel, UUID uuid, @Nullable Player owner, Vec3 position, Vec3 velocity) {
        super(odmLevel, uuid, owner, position, velocity);
        this.type = "hook";
    }
    public OdmHookNode(OdmServerLevel odmLevel, @Nullable Player owner, Vec3 position, Vec3 velocity) {
        this(odmLevel, UUID.randomUUID(), owner, position, velocity);
    }
    public OdmHookNode(OdmNode node) {
        this(node.odmLevel, node.uuid, node.owner, node.position, node.velocity);
    }

    public void tick() {
        super.tick();

        PlayerUtil.spawnParticleForAll(
                this.odmLevel.level,
                32,
                ParticleTypes.HEART,
                true,
                this.position.x,
                this.position.y,
                this.position.z,
                0,
                0,
                0,
                0,
                1
        );
    }

    @Override
    public void applySpringPhysics() {

    }

    @Override
    public void onRemove() {
        OdmNode next = this.lastNode;
        while (next != null) {
            OdmNode after = next.lastNode;
            next.remove();
            next = after;
        }
    }

    public static OdmHookNode fromTag(OdmServerLevel level, CompoundTag tag) {
        return new OdmHookNode(OdmNode.fromTag(level, tag));
    }
}
