package net.phantompig.soy.entity;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.titan.TitanInstance;
import net.threetag.palladium.util.PlayerUtil;

import java.util.List;

public class TitanCorpseEntity extends LivingEntity {
    public TitanInstance titanInstance = new TitanInstance(this);

    public TitanCorpseEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return List.of();
    }
    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }
    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {}

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (titanInstance.isCorpse) {
            int decay = titanInstance.getDecay();
            if (decay >= TitanInstance.MAX_CORPSE_DECAY) {
                this.discard();
                return;
            }
            titanInstance.setDecay(decay + 1);

            PlayerUtil.spawnParticleForAll(
                    this.level(),
                    64,
                    (SimpleParticleType) SoyParticles.LARGE_STEAM.get(),
                    true,
                    this.getX(),
                    this.getY() + this.getBoundingBox().getYsize() / 2,
                    this.getZ(),
                    (float) (Math.random() * this.getBoundingBox().getXsize() / 3),
                    (float) (Math.random() * this.getBoundingBox().getYsize() / 2),
                    (float) (Math.random() * this.getBoundingBox().getZsize() / 3),
                    0.1f,
                    10
            );
            PlayerUtil.spawnParticleForAll(
                    this.level(),
                    64,
                    (SimpleParticleType) SoyParticles.EMBER.get(),
                    true,
                    this.getX(),
                    this.getY() + this.getBoundingBox().getYsize() / 2,
                    this.getZ(),
                    (float) (Math.random() * this.getBoundingBox().getXsize() / 2),
                    (float) (Math.random() * this.getBoundingBox().getYsize() / 2),
                    (float) (Math.random() * this.getBoundingBox().getZsize() / 2),
                    0.3f,
                    1
            );
            PlayerUtil.playSoundToAll(this.level(), this.getX(), this.getY(), this.getZ(), 64, SoySounds.STEAM.get(), SoundSource.NEUTRAL, 1.5f, 0.6f);
            PlayerUtil.playSoundToAll(this.level(), this.getX(), this.getY(), this.getZ(), 64, SoySounds.STEAM.get(), SoundSource.NEUTRAL, 1f, 0.8f);
        }
    }


    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        CompoundTag soyTag = compound.contains("Titan", Tag.TAG_COMPOUND) ? compound.getCompound("Titan") : new CompoundTag();
        this.titanInstance = TitanInstance.fromTag(this, soyTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Titan", this.titanInstance.toTag());
    }
}
