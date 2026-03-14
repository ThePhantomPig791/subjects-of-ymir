package net.phantompig.soy.titan.hardening;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.phantompig.soy.block.SoyBlockTags;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.entity.PalladiumAttributes;
import net.threetag.palladium.util.Easing;
import net.threetag.palladium.util.PlayerUtil;
import org.joml.Vector3f;

import java.util.UUID;

public class HardeningSystem {
    public static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("fd958f4e-6501-4d8b-b9ec-3cbb0516006b");
    public static final UUID ARMOR_UUID = UUID.fromString("333ce3f0-4cfa-4a56-afb4-8e358a427841");
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("4cb44384-80e0-4b7a-8449-db2c0565e7f6");
    public static final UUID JUMP_POWER_UUID = UUID.fromString("3aef4384-1202-4b7a-8449-db2c0565e7f6");

    public final LivingEntity entity;

    public int attackTimeIncrease = 0;

    public HardeningSystem(LivingEntity entity) {
        this.entity = entity;
    }

    public enum HardeningTypes {
        ALL,
        HAND,
        LEGS,
        NAPE
    }

    public float getAllHardening() {
        return SoyProperties.HARDENING_ALL.get(this.entity) / 255f;
    }
    public void setAllHardening(float percentage) {
        SoyProperties.HARDENING_ALL.set(this.entity, (int) (percentage * 255));
        changeMovementSpeed(percentage == 0 ? 0 : -Easing.outExpo(percentage));
        changeArmor(20 * percentage);
        changeAttackDamage(4 * percentage);
        changeJumpPower(-5 * percentage);
        attackTimeIncrease = (int) (percentage * 10);
    }

    public void changeMovementSpeed(double amount) {
        this.entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_SPEED_UUID);
        this.entity.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(MOVEMENT_SPEED_UUID, "titan hardening movement speed", amount, AttributeModifier.Operation.ADDITION));
    }
    public void changeArmor(double amount) {
        this.entity.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_UUID);
        this.entity.getAttribute(Attributes.ARMOR).addPermanentModifier(new AttributeModifier(ARMOR_UUID, "titan hardening armor", amount, AttributeModifier.Operation.ADDITION));
    }
    public void changeAttackDamage(double amount) {
        this.entity.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(ATTACK_DAMAGE_UUID);
        this.entity.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(ATTACK_DAMAGE_UUID, "titan hardening attack damage", amount, AttributeModifier.Operation.ADDITION));
    }
    public void changeJumpPower(double amount) {
        this.entity.getAttribute(PalladiumAttributes.JUMP_POWER.get()).removeModifier(JUMP_POWER_UUID);
        this.entity.getAttribute(PalladiumAttributes.JUMP_POWER.get()).addPermanentModifier(new AttributeModifier(JUMP_POWER_UUID, "titan hardening jump power", amount, AttributeModifier.Operation.ADDITION));
    }


    public void placePhysicalHardening(float percentage, boolean infiniteRange) {
        /*percentage /= percentage + 1;
        percentage += 0.5f;
        var pos = this.entity.blockPosition().offset(
                (int) (3 * this.entity.getBoundingBox().getXsize() * percentage * (Math.random() - 0.5)),
                (int) (2 * this.entity.getBoundingBox().getYsize() * percentage * (Math.random() - 0.2)),
                (int) (3 * this.entity.getBoundingBox().getZsize() * percentage * (Math.random() - 0.5))
        );
        if (
                entity.level().getBlockState(pos).is(SoyBlockTags.HARDENING_CAN_REPLACE)
                        && ShapeUtil.inOval(pos.getCenter().subtract(entity.position()), (float) (1.5 * percentage * this.entity.getBoundingBox().getXsize()), 1, 2, 1)
                        && !entity.getBoundingBox().intersects(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)
        ) {
            entity.level().setBlock(pos, SoyBlocks.HARDENING_BLOCK.get().defaultBlockState(), 2);
        }*/


        double yaw = Math.random() * Math.PI * 2;
        double pitch = (1.3 * Math.random() - 0.7) * Math.PI * percentage / (percentage + 5) * (1 - percentage / (1.5 * percentage + 20));
        double range;
        if (infiniteRange) range = Math.pow(percentage, 0.4);
        else range = (percentage / (percentage + 0.1)) * (0.2 * percentage / (percentage + 1) + 1.5);
        double yRange = range * this.entity.getBoundingBox().getYsize() * 0.85;
        range *= this.entity.getBoundingBox().getXsize();
        var pos = this.entity.blockPosition().offset(
                (int) (range * Math.cos(yaw) * Math.cos(pitch)),
                (int) (yRange * Math.sin(pitch) - (1 + Math.abs(entity.getDeltaMovement().y()) * 5)),
                (int) (range * Math.sin(yaw) * Math.cos(pitch))
        );

        if (entity.level().getBlockState(pos).is(SoyBlockTags.HARDENING_CAN_REPLACE) && !entity.getBoundingBox().intersects(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)) {
            entity.level().setBlock(pos, SoyBlocks.HARDENING_BLOCK.get().defaultBlockState(), 2);
            PlayerUtil.playSoundToAll(entity.level(),
                    pos.getCenter().x,
                    pos.getCenter().y,
                    pos.getCenter().z,
                    48,
                    SoySounds.SHORT_HARDEN.get(),
                    SoundSource.BLOCKS,
                    0.7f,
                    (float) (0.1 * Math.random() + 1.4)
            );
            PlayerUtil.spawnParticleForAll(
                    entity.level(),
                    48,
                    new DustParticleOptions(new Vector3f(0.8f, 0.95f, 1), 2),
                    false,
                    pos.getCenter().x,
                    pos.getCenter().y,
                    pos.getCenter().z,
                    1,
                    1,
                    1,
                    2,
                    8
            );
        }
    }


    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("AttackTimeIncrease", attackTimeIncrease);
        return tag;
    }
    public static HardeningSystem fromTag(LivingEntity entity, CompoundTag tag) {
        HardeningSystem h = new HardeningSystem(entity);
        h.attackTimeIncrease = tag.getInt("AttackTimeIncrease");
        return h;
    }


    public static void copyPropertiesToEntity(LivingEntity from, LivingEntity to) {
        SoyProperties.HARDENING_ALL.set(to, SoyProperties.HARDENING_ALL.get(from));
    }

    public static void copyTo(HardeningSystem from, HardeningSystem to) {
        copyPropertiesToEntity(from.entity, to.entity);
        to.attackTimeIncrease = from.attackTimeIncrease;
    }
}
