package net.phantompig.soy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.odm.OdmLevelHelper;
import net.phantompig.soy.odm.component.OdmComponentItem;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.item.AddonArmorItem;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.property.PalladiumProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class OdmAttachableAddonArmorItem extends AddonArmorItem {
    public OdmAttachableAddonArmorItem(ArmorMaterial armorMaterial, Type type, Properties properties) {
        super(armorMaterial, type, properties);
    }

    @Override
    public void armorTick(ItemStack stack, Level level, Player player) {
        super.armorTick(stack, level, player);
        if (!level.isClientSide()) {
            gasStrafe(stack, player);
            hooksTick(stack, level, player);
        }
    }

    private void gasStrafe(ItemStack stack, Player player) {
        if (player.onGround()) return;
        boolean forward = PalladiumProperties.FORWARD_KEY_DOWN.get(player);
        boolean backward = PalladiumProperties.BACKWARDS_KEY_DOWN.get(player);
        boolean left = PalladiumProperties.LEFT_KEY_DOWN.get(player);
        boolean right = PalladiumProperties.RIGHT_KEY_DOWN.get(player);
        boolean up = PalladiumProperties.JUMP_KEY_DOWN.get(player);
        boolean down = player.isCrouching();
        if (!forward && !backward && !left && !right && !up && !down) return;
        this.getComponents(stack).forEach(item -> {
            if (item.getItem() instanceof OdmComponentItem component) {
                boolean moved = false;
                if (forward && component.canStrafe(Direction.NORTH) && consumeGas(stack, 1)) {
                    moved = gasStrafe(player, deIntensifyPitch(player.getXRot() - 20), player.getYRot(), component.odmComponent.gasStrafeStrength().get(Direction.NORTH));
                }
                if (backward && component.canStrafe(Direction.SOUTH) && consumeGas(stack, 1)) {
                    moved = gasStrafe(player, deIntensifyPitch(player.getXRot() - 20), player.getYRot() + 180, component.odmComponent.gasStrafeStrength().get(Direction.SOUTH));
                }
                if (left && component.canStrafe(Direction.WEST) && consumeGas(stack, 1)) {
                    moved = gasStrafe(player, 0, player.getYRot() - 90, component.odmComponent.gasStrafeStrength().get(Direction.WEST));
                }
                if (right && component.canStrafe(Direction.EAST) && consumeGas(stack, 1)) {
                    moved = gasStrafe(player, 0, player.getYRot() + 90, component.odmComponent.gasStrafeStrength().get(Direction.EAST));
                }
                if (up && component.canStrafe(Direction.UP) && consumeGas(stack, 1)) {
                    moved = gasStrafe(player, -90, 0, component.odmComponent.gasStrafeStrength().get(Direction.UP));
                }
                if (down && component.canStrafe(Direction.DOWN) && consumeGas(stack, 1)) {
                    moved = gasStrafe(player, 90, 0, component.odmComponent.gasStrafeStrength().get(Direction.DOWN));
                }
                if (moved) sendMotionPacket(player);
            }
        });
    }
    private boolean gasStrafe(Player player, float pitch, float yaw, float strength) {
        Vec3 direction = Vec3.directionFromRotation(pitch, yaw).scale(strength);
        player.addDeltaMovement(direction);
        PlayerUtil.spawnParticleForAll(
                player.level(),
                128,
                ParticleTypes.CLOUD,
                true,
                player.getX(),
                player.getY() + 1,
                player.getZ(),
                0,
                0,
                0,
                0.1f,
                2
        );
        PlayerUtil.playSoundToAll(
                player.level(),
                player.getX(),
                player.getY() + 1,
                player.getZ(),
                64,
                SoySounds.GAS_BURST.get().getLocation(),
                SoundSource.PLAYERS,
                0.4f,
                (float) (Math.random() * 0.2 + 1.4 / (0.2 * strength))
        );
        return true;
    }
    private float deIntensifyPitch(float x) { // https://www.desmos.com/calculator/btea69orrh
        return (float) (-0.0000439557 * Math.pow(x, 3) + 0.0000847253 * Math.pow(x, 2) + 1.00316 * x - 0.868569);
    }
    public boolean consumeGas(ItemStack stack, int amount) {
        for (ItemStack item : this.getComponents(stack).toList()) {
            if (item.getItem() instanceof OdmComponentItem component) {
                amount = component.consume(item, amount);
                if (amount == 0) break;
            }
        }
        return amount == 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, components, isAdvanced);
        components.add(Component.translatable("tooltip.subjects_of_ymir.odm_attachable"));
        ItemStack item = getTurbine(stack);
        if (!item.isEmpty()) {
            addTooltip("turbine", components, item);
        }
        item = getRightSheath(stack);
        if (!item.isEmpty()) {
            addTooltip("right_sheath", components, item);
        }
        item = getLeftSheath(stack);
        if (!item.isEmpty()) {
            addTooltip("left_sheath", components, item);
        }
    }
    private void addTooltip(String slot, List<Component> components, ItemStack item) {
        components.add(Component.translatable("tooltip.subjects_of_ymir.odm_attachable." + slot, Component.literal(((OdmComponentItem) item.getItem()).odmComponent.prettyName()).withStyle(ChatFormatting.ITALIC)));
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        ItemUtils.onContainerDestroyed(itemEntity, getComponents(itemEntity.getItem()));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY) {
            if (other.isEmpty()) {
                access.set(extractFirstComponent(stack));
                playAttachSound(player, 1.2f);
                return true;
            } else if (other.getItem() instanceof OdmComponentItem component && add(stack, other, component)) {
                other.shrink(1);
                playAttachSound(player, 1);
                return true;
            }
        }
        return false;
    }

    public boolean add(ItemStack stack, ItemStack other, OdmComponentItem component) {
        switch (component.odmComponent.slot()) {
            case SHEATH -> {
                if (getRightSheath(stack).isEmpty()) {
                    setRightSheath(stack, other);
                    return true;
                } else if (getLeftSheath(stack).isEmpty()) {
                    setLeftSheath(stack, other);
                    return true;
                }
            }
            case TURBINE -> {
                if (getTurbine(stack).isEmpty()) {
                    setTurbine(stack, other);
                    return true;
                }
            }
        }
        return false;
    }

    public Stream<ItemStack> getComponents(ItemStack stack) {
        return Stream.of(getTurbine(stack), getRightSheath(stack), getLeftSheath(stack));
    }

    public ItemStack getTurbine(ItemStack stack) {
        if (stack.getOrCreateTagElement("Turbine").isEmpty()) stack.getOrCreateTag().put("Turbine", ItemStack.EMPTY.save(new CompoundTag()));
        return ItemStack.of(stack.getOrCreateTagElement("Turbine"));
    }
    public ItemStack getRightSheath(ItemStack stack) {
        if (stack.getOrCreateTagElement("RightSheath").isEmpty()) stack.getOrCreateTag().put("RightSheath", ItemStack.EMPTY.save(new CompoundTag()));
        return ItemStack.of(stack.getOrCreateTagElement("RightSheath"));
    }
    public ItemStack getLeftSheath(ItemStack stack) {
        if (stack.getOrCreateTagElement("LeftSheath").isEmpty()) stack.getOrCreateTag().put("LeftSheath", ItemStack.EMPTY.save(new CompoundTag()));
        return ItemStack.of(stack.getOrCreateTagElement("LeftSheath"));
    }

    public void setTurbine(ItemStack stack, ItemStack other) {
        stack.getOrCreateTag().put("Turbine", other.save(new CompoundTag()));
    }
    public void setRightSheath(ItemStack stack, ItemStack other) {
        stack.getOrCreateTag().put("RightSheath", other.save(new CompoundTag()));
    }
    public void setLeftSheath(ItemStack stack, ItemStack other) {
        stack.getOrCreateTag().put("LeftSheath", other.save(new CompoundTag()));
    }

    public ItemStack extractFirstComponent(ItemStack stack) {
        ItemStack extracted = getLeftSheath(stack);
        if (!extracted.isEmpty()) {
            setLeftSheath(stack, ItemStack.EMPTY);
            return extracted;
        }
        extracted = getRightSheath(stack);
        if (!extracted.isEmpty()) {
            setRightSheath(stack, ItemStack.EMPTY);
            return extracted;
        }
        extracted = getTurbine(stack);
        if (!extracted.isEmpty()) {
            setTurbine(stack, ItemStack.EMPTY);
            return extracted;
        }
        return extracted;
    }


    public void hooksTick(ItemStack stack, Level level, Player player) {
        validateHooks(stack, level);
    }

    public void validateHooks(ItemStack stack, Level level) {
        if (getTurbine(stack).isEmpty()) {
            UUID hook;
            if ((hook = getLeftHook(stack)) != null || (hook = getRightHook(stack)) != null) {
                OdmLevelHelper.removeNode(level, hook);
            }
        }
    }

    public boolean hasShotLeftHook(ItemStack stack) {
        return !stack.getOrCreateTag().contains("LeftHookUUID");
    }
    public UUID getLeftHook(ItemStack stack) {
        if (hasShotLeftHook(stack)) return null;
        return stack.getOrCreateTag().getUUID("LeftHookUUID");
    }
    public void setLeftHook(ItemStack stack, UUID uuid) {
        stack.getOrCreateTag().putUUID("LeftHookUUID", uuid);
    }
    public void removeLeftHook(ItemStack stack) {
        stack.getOrCreateTag().remove("LeftHookUUID");
    }
    public boolean hasShotRightHook(ItemStack stack) {
        return !stack.getOrCreateTag().contains("RightHookUUID");
    }
    public UUID getRightHook(ItemStack stack) {
        if (hasShotRightHook(stack)) return null;
        return stack.getOrCreateTag().getUUID("RightHookUUID");
    }
    public void setRightHook(ItemStack stack, UUID uuid) {
        stack.getOrCreateTag().putUUID("RightHookUUID", uuid);
    }
    public void removeRightHook(ItemStack stack) {
        stack.getOrCreateTag().remove("RightHookUUID");
    }


    private void playAttachSound(Player player, float pitch) {
        player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER,1, pitch);
    }

    private void sendMotionPacket(Player player) {
        if (player instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
        }
    }
}
