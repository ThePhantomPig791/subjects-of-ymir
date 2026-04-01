package net.phantompig.soy.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.NotNull;

public class BazookaItem extends GasHoldingItem {
    public BazookaItem(Properties properties, int max) {
        super(properties, max);
    }

    @NotNull
    @Override
    public ItemStack getDefaultInstance() {
        var stack = super.getDefaultInstance();
        set(stack, this.max);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        boolean infinite = EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.INFINITY_ARROWS) || player.isCreative();
        if (!level.isClientSide() && get(stack) > 0 || infinite) {
            Vec3 pos = player.getEyePosition().add(player.getLookAngle().scale(0.25));
            ItemStack toFire = player.getItemInHand(usedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

            recoil(level, fireBlock(level, player, toFire, pos), pos, 2);

            if (!infinite) {
                toFire.shrink(1);
                set(stack, Math.max(0, get(stack) - 1));
            }

            fx(level, pos);
            return InteractionResultHolder.consume(stack);
        }
        return super.use(level, player, usedHand);
    }

    private FallingBlockEntity fireBlock(Level level, Player player, ItemStack source, Vec3 pos) {
        if (source.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            FallingBlockEntity entity = new FallingBlockEntity(level, pos.x, pos.y, pos.z, block.defaultBlockState());
            Vec3 dv = player.getLookAngle().scale(2);
            entity.setDeltaMovement(dv);
            level.addFreshEntity(entity);
            return entity;
            /*for (Player p : level.getNearbyPlayers(TargetingConditions.DEFAULT, null, AABB.ofSize(pos, 64, 64, 64))) {
                if (p instanceof ServerPlayer sp) sp.connection.send(new ClientboundSetEntityMotionPacket(entity));
                //if (p instanceof ServerPlayer sp) sp.connection.send(new ClientboundAddEntityPacket(entity));
            }*/
        }
        return null;
    }


    private void recoil(Level level, FallingBlockEntity exclude, Vec3 pos, float strength) {
        level.getEntities(exclude, AABB.ofSize(pos, 5, 5, 5)).forEach(e -> {
            e.addDeltaMovement(pos
                    .subtract(e.getEyePosition())
                    .normalize()
                    .reverse()
                    .scale(Math.min(2, strength / pos.distanceTo(e.getEyePosition()) / Math.pow(e.getBoundingBox().getYsize() / 2, 2)))
            );
            if (e instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        });
    }


    private void fx(Level level, Vec3 pos) {
        PlayerUtil.playSoundToAll(level, pos.x, pos.y, pos.z, 32, SoySounds.GAS_BURST.get(), SoundSource.BLOCKS, 2, 0.75f);
        PlayerUtil.playSoundToAll(level, pos.x, pos.y, pos.z, 32, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5f, 1.8f);
        PlayerUtil.spawnParticleForAll(
                level,
                32,
                ParticleTypes.CLOUD,
                false,
                pos.x(),
                pos.y(),
                pos.z(),
                0,
                0,
                0,
                3,
                25
        );
    }
}
