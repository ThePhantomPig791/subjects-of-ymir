package net.phantompig.soy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.entity.FlareEntity;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.particle.FlareParticleOptions;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class FlareGunItem extends Item {
    public FlareGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!(level instanceof ServerLevel)) return;

        final Vec3 lookAngle = livingEntity.getLookAngle();
        final float pitch = (float) (0.85 + 0.15 * Math.random());
        PlayerUtil.playSoundToAll(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 512, SoySounds.FLARE_SHOOT.get(), SoundSource.PLAYERS, 2, pitch);

        ItemStack cartridgeStack = this.getFlareCartridge(stack);
        if (cartridgeStack.isEmpty()) return;

        if (cartridgeStack.getItem() instanceof FlareCartridgeItem cartridgeItem && cartridgeItem.hasColor(cartridgeStack)) {
            FlareEntity flareEntity = new FlareEntity(SoyEntities.FLARE.get(), level);
            flareEntity.setPos(livingEntity.getEyePosition());
            flareEntity.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), 0, 12, 15 - Math.min(15, 0.25f * (this.getUseDuration(stack) - timeCharged)));
            int color = cartridgeItem.getColor(cartridgeStack);
            flareEntity.setColor(color);
            level.addFreshEntity(flareEntity);

            Vec3 out = livingEntity.position().add(lookAngle.scale(45));
            PlayerUtil.playSoundToAll(level, out.x, out.y, out.z, 512, SoySounds.FLARE_SCREECH.get(), SoundSource.PLAYERS, 2, pitch);
            out = out.add(lookAngle.scale(30));
            PlayerUtil.playSoundToAll(level, out.x, out.y, out.z, 512, SoySounds.FLARE_SCREECH.get(), SoundSource.PLAYERS, 2, pitch);

            PlayerUtil.spawnParticleForAll(level,
                    64,
                    new FlareParticleOptions(new Vector3f((color >> 16) & 0xFF, (color >> 8) & 0xFF, (color) & 0xFF).mul(255), 0.05f),
                    false,
                    livingEntity.getX(),
                    livingEntity.getEyeY(),
                    livingEntity.getZ(),
                    0.1f,
                    0.05f,
                    0.1f,
                    0.02f,
                    8
            );
        }

        livingEntity.addDeltaMovement(lookAngle.scale(-0.1));
        if (livingEntity instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
        }

        PlayerUtil.spawnParticleForAll(level, 64, (ParticleOptions) SoyParticles.EMBER.get(), false, livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ(), 1, 1, 1, 1.2f, 24);

        if (!(livingEntity instanceof Player player && player.getAbilities().instabuild)) {
            this.setFlareCartridge(stack, ItemStack.EMPTY);
            stack.hurtAndBreak(1, livingEntity, e -> e.broadcastBreakEvent(livingEntity.getUsedItemHand()));
        }
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (this.getFlareCartridge(itemStack).isEmpty()) {
            return InteractionResultHolder.fail(itemStack);
        } else {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(itemStack);
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY) {
            ItemStack storedCartridge = getFlareCartridge(stack);
            if (other.isEmpty() || ItemEntity.areMergable(storedCartridge, other)) {
                if (!storedCartridge.isEmpty()) {
                    access.set(ItemEntity.merge(storedCartridge, other, storedCartridge.getCount() + other.getCount()));
                    this.setFlareCartridge(stack, ItemStack.EMPTY);
                    playInsertSound(player, 1.2f);
                    return true;
                }
            } else {
                if (other.getItem() instanceof FlareCartridgeItem && storedCartridge.isEmpty()) {
                    this.setFlareCartridge(stack, other.copyWithCount(1));
                    other.shrink(1);
                    playInsertSound(player, 1.6f);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        ItemStack flare = this.getFlareCartridge(stack);
        if (flare.getItem() instanceof FlareCartridgeItem flareCartridgeItem) {
            tooltipComponents.add(Component.translatable("tooltip.subjects_of_ymir.flare_cartridge_color", flareCartridgeItem.getColorString(flare)).withStyle(ChatFormatting.GRAY));
        }
    }

    public ItemStack getFlareCartridge(ItemStack stack) {
        return stack.getOrCreateTagElement("FlareCartridge").isEmpty() ? ItemStack.EMPTY : ItemStack.of(stack.getTagElement("FlareCartridge"));
    }
    public void setFlareCartridge(ItemStack stack, ItemStack flare) {
        stack.getOrCreateTag().put("FlareCartridge", flare.save(new CompoundTag()));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    private static void playInsertSound(Player player, float pitch) {
        player.playSound(SoundEvents.ARMOR_EQUIP_IRON,0.6f, pitch);
    }
}
