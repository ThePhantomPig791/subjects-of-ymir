package net.phantompig.soy.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.phantompig.soy.item.SoyItems;
import org.jetbrains.annotations.NotNull;

public class ThrownBladeEntity extends ThrowableItemProjectile {
    public ThrownBladeEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @NotNull
    @Override
    protected Item getDefaultItem() {
        return SoyItems.BLADE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        float speedSqr = (float) result.getEntity().getDeltaMovement().add(this.getDeltaMovement().reverse()).lengthSqr();
        result.getEntity().hurt(SoyDamageSources.slice(this.level(), this, this.getOwner()), 6 + 16 * speedSqr / (speedSqr + 6));
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), Math.random() > 0.25 ? this.getItem() : Items.IRON_NUGGET.getDefaultInstance()));
        this.discard();
    }

    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }
}
