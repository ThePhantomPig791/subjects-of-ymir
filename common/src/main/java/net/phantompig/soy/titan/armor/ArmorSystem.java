package net.phantompig.soy.titan.armor;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.titan.TitanInstance;
import net.threetag.palladium.util.PlayerUtil;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ArmorSystem {
    public static final UUID SPEED_ATTRIBUTE_UUID = UUID.fromString("6bd13f40-4322-4b8a-94ab-1feef87e8174");
    public static final UUID ARMOR_ATTRIBUTE_UUID = UUID.fromString("6bd13f40-1970-4b8a-94ab-1feef87e8174");

    public final LivingEntity entity;

    public float integrityUpper, integrityLower, integrityNape;

    public ArmorSystem(LivingEntity entity) {
        this.entity = entity;
    }

    public void shift() {
        if (getTitanInstance().titan != null && getTitanInstance().titan.stats.hasArmor) {
            integrityUpper = 1;
            integrityLower = 1;
            integrityNape = 1;
            setUpper(2);
            setLower(2);
            setNape(2);
        }
    }

    public void unshift() {
        integrityUpper = 0;
        integrityLower = 0;
        integrityNape = 0;
        setUpper(0);
        setLower(0);
        setNape(0);
        if (entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(SPEED_ATTRIBUTE_UUID) != null) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_ATTRIBUTE_UUID);
        }
        if (entity.getAttribute(Attributes.ARMOR).getModifier(ARMOR_ATTRIBUTE_UUID) != null) {
            entity.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_ATTRIBUTE_UUID);
        }
    }

    public void hurt(DamageSource damageSource, AtomicReference<Float> damage) {
        float amount = damage.get();
        Vec3 sourcePos = damageSource.getSourcePosition();
        if (sourcePos != null) {
            if (hasNapeArmor() && getTitanInstance().hitsNape(damageSource)) {
                amount = damageNape(amount);
                if (amount == 0) return;
            }
            double heightRelative = (sourcePos.y -  this.entity.getY()) / this.entity.getBbHeight();
            if (heightRelative < 0.5) {
                if (hasLowerArmor()) {
                    amount = damageLower(amount);
                    if (amount == 0) return;
                }
            } else {
                if (hasUpperArmor()) {
                    amount = damageUpper(amount);
                    if (amount == 0) return;
                }
            }
            if (amount > 0) {
                damage.set(amount);
            }
        }
    }

    public float damageUpper(float amount) {
        integrityUpper -= amount / 100;
        if (integrityUpper <= 0) {
            float toReturn = -integrityUpper;
            integrityUpper = 1;
            setUpper(getUpper() - 1);
            breakArmorFx();
            return toReturn;
        }
        return 0;
    }
    public float damageLower(float amount) {
        integrityLower -= amount / 100;
        if (integrityLower <= 0) {
            float toReturn = -integrityLower;
            integrityLower = 1;
            setLower(getLower() - 1);
            breakArmorFx();
            return toReturn;
        }
        return 0;
    }
    public float damageNape(float amount) {
        integrityNape -= amount / 100;
        if (integrityNape <= 0) {
            float toReturn = -integrityNape;
            integrityNape = 1;
            setNape(getNape() - 1);
            breakArmorFx();
            return toReturn;
        }
        return 0;
    }

    public void shed() {
        boolean removedSomething = true;
        if (getLower() == 2) setLower(getLower() - 1);
        else if (getUpper() == 2) setUpper(getUpper() - 1);
        else if (hasLowerArmor()) setLower(getLower() - 1);
        else if (hasUpperArmor()) setUpper(getUpper() - 1);
        else removedSomething = false;

        if (removedSomething) breakArmorFx();
    }

    public int getUpper() {
        return SoyProperties.ARMOR_UPPER.get(entity);
    }
    public int getLower() {
        return SoyProperties.ARMOR_LOWER.get(entity);
    }
    public int getNape() {
        return SoyProperties.ARMOR_NAPE.get(entity);
    }

    public boolean hasUpperArmor() {
        return getUpper() > 0;
    }
    public boolean hasLowerArmor() {
        return getLower() > 0;
    }
    public boolean hasNapeArmor() {
        return getNape() > 0;
    }

    public void setUpper(int value) {
        SoyProperties.ARMOR_UPPER.set(entity, value);
        updateSpeed(getSpeedIncrease(value, getLower()));
        updateArmor(getArmorIncrease(value, getLower()));
    }
    public void setLower(int value) {
        SoyProperties.ARMOR_LOWER.set(entity, value);
        updateSpeed(getSpeedIncrease(getUpper(), value));
        updateArmor(getArmorIncrease(getUpper(), value));
    }
    public void setNape(int value) {
        SoyProperties.ARMOR_NAPE.set(entity, value);
    }

    public double getSpeedIncrease(int upper, int lower) {
        return 0.03 * (1 - (0.75 * lower / 2 + 0.25 * upper / 2));
    }
    public double getArmorIncrease(int upper, int lower) {
        return -30 * (1 - (0.8 * (upper + integrityUpper - 1) / 2 + 0.2 * (lower * integrityLower - 1) / 2));
    }

    public void updateSpeed(double value) {
        if (entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(SPEED_ATTRIBUTE_UUID) != null) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_ATTRIBUTE_UUID);
        }
        entity.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(SPEED_ATTRIBUTE_UUID, "shed titan armor", value, AttributeModifier.Operation.ADDITION));
    }
    public void updateArmor(double value) {
        if (entity.getAttribute(Attributes.ARMOR).getModifier(ARMOR_ATTRIBUTE_UUID) != null) {
            entity.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_ATTRIBUTE_UUID);
        }
        entity.getAttribute(Attributes.ARMOR).addPermanentModifier(new AttributeModifier(ARMOR_ATTRIBUTE_UUID, "shed titan armor", value, AttributeModifier.Operation.ADDITION));
    }

    public void breakArmorFx() {
        PlayerUtil.playSoundToAll(entity.level(),
                entity.getX(),
                entity.getY() + entity.getBoundingBox().getYsize() / 2,
                entity.getZ(),
                48,
                SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,
                SoundSource.PLAYERS,
                0.7f,
                (float) (0.1 * Math.random() + 1 + (1 - integrityUpper) / 2 + (1 - integrityLower) / 2)
        );
        PlayerUtil.spawnParticleForAll(entity.level(),
                64,
                (ParticleOptions) SoyParticles.ARMOR_DEBRIS.get(),
                false,
                entity.getX(),
                entity.getY() + entity.getEyeHeight() / 2,
                entity.getZ(),
                1.5f, 3, 1.5f,
                0.6f, 15
        );
    }

    public TitanInstance getTitanInstance() {
        if (entity instanceof SoyPlayerExtension ext) {
            return ext.soy$getTitanInstance();
        }
        return null;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("IntegrityUpper", integrityUpper);
        tag.putFloat("IntegrityLower", integrityLower);
        tag.putFloat("IntegrityNape", integrityNape);
        return tag;
    }
    public static ArmorSystem fromTag(LivingEntity entity, CompoundTag tag) {
        ArmorSystem a = new ArmorSystem(entity);
        a.integrityUpper = tag.contains("IntegrityUpper") ? tag.getFloat("IntegrityUpper") : 0;
        a.integrityLower = tag.contains("IntegrityLower") ? tag.getFloat("IntegrityLower") : 0;
        a.integrityNape = tag.contains("IntegrityNape") ? tag.getFloat("IntegrityNape") : 0;
        return a;
    }

    public static void copyPropertiesToEntity(LivingEntity from, LivingEntity to) {
        SoyProperties.ARMOR_UPPER.set(to, SoyProperties.ARMOR_UPPER.get(from));
        SoyProperties.ARMOR_LOWER.set(to, SoyProperties.ARMOR_LOWER.get(from));
        SoyProperties.ARMOR_NAPE.set(to, SoyProperties.ARMOR_NAPE.get(from));
    }
    public static void copyTo(ArmorSystem from, ArmorSystem to) {
        copyPropertiesToEntity(from.entity, to.entity);
        to.integrityUpper = from.integrityUpper;
        to.integrityLower = from.integrityLower;
        to.integrityNape = from.integrityNape;
    }
}
