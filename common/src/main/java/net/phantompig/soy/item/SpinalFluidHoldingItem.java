package net.phantompig.soy.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.stat.SoyStats;
import net.phantompig.soy.titan.Titan;
import net.phantompig.soy.titan.TitanInstance;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class SpinalFluidHoldingItem extends DataHoldingItem {
    public final boolean drinkable;

    public SpinalFluidHoldingItem(Properties properties, int max) {
        this(properties, max, false);
    }
    public SpinalFluidHoldingItem(Properties properties, int max, boolean drinkable) {
        super(properties.stacksTo(1), "SpinalFluid", max, new Color(131, 89, 255));
        this.drinkable = drinkable;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (usedHand == InteractionHand.OFF_HAND) return InteractionResultHolder.pass(stack);
        if (level.isClientSide()) return InteractionResultHolder.pass(stack);
        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offhandStack.getItem() instanceof SpinalFluidHoldingItem osfhi && stack.getItem() instanceof SpinalFluidHoldingItem msfhi) {
            if (player.isCrouching()) {
                int extra = msfhi.move(osfhi.get(offhandStack), stack);
                if (extra > -1) {
                    osfhi.set(offhandStack, extra);
                    transferSound(player.level(), player.getX(), player.getY(), player.getZ());
                    return InteractionResultHolder.success(stack);
                }
            } else if (msfhi.moveOne(osfhi.get(offhandStack), stack)) {
                osfhi.add(offhandStack, -1);
                transferSound(player.level(), player.getX(), player.getY(), player.getZ());
                return InteractionResultHolder.pass(stack);
            }
        }
        if (this.drinkable && this.get(stack) > 0) {
            ItemUtils.startUsingInstantly(level, player, usedHand);
        }
        return InteractionResultHolder.pass(stack);
    }

    private static void transferSound(Level level, double x, double y, double z) {
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.HONEY_BLOCK_STEP, SoundSource.PLAYERS, 0.5f, 1.4f);
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.5f, 1.8f);
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            inject(stack, player);
            set(stack, 0);
            livingEntity.gameEvent(GameEvent.DRINK);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return stack;
    }

    public static boolean inject(ItemStack stack, Player player) {
        if (stack.getItem() instanceof SpinalFluidHoldingItem spfhi && player instanceof SoyPlayerExtension ext) {
            if (ext.getTitanInstance().titan == null) {
                if (spfhi.get(stack) >= InjectionItem.MAX) {
                    Tuple<Titan, String> titan = TitanInstance.sequentialRandomizeFor(player);
                    if (ext.getTitanInstance().getMemoryManager().populateWithAncientMessages(titan.getB())) {
                        PlayerUtil.playSound(player, player.getX(), player.getY(), player.getZ(), SoySounds.HEARTBEAT.get(), SoundSource.PLAYERS, 1, 1.1f + (float) (0.1 * Math.random()));
                    }
                    injectSound(player.level(), player.getX(), player.getY(), player.getZ());
                    spfhi.set(stack, 0);
                    return true;
                } else {
                    return false; // TODO pure titan if injection is not full
                }
            } else if (spfhi.get(stack) > 0) {
                SoyProperties.PATH_POINTS.set(player, SoyProperties.PATH_POINTS.get(player) + spfhi.get(stack));
                player.awardStat(SoyStats.PATH_POINTS_GAINED, spfhi.get(stack));
                if (ext.getTitanInstance().getMemoryManager().populateWithAncientMessages(ext.getTitanInstance().variant, 3, -2)) {
                    PlayerUtil.playSound(player, player.getX(), player.getY(), player.getZ(), SoySounds.HEARTBEAT.get(), SoundSource.PLAYERS, 0.8f, 1.2f + (float) (0.1 * Math.random()));
                }
                spfhi.set(stack, 0);
                PlayerUtil.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6f, 1.1f);
                return true;
            }
        }
        return false;
    }
    private static void injectSound(Level level, double x, double y, double z) {
        // TODO better sound effect
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.HONEY_BLOCK_STEP, SoundSource.PLAYERS, 0.5f, 2);
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.5f, 1.9f);
    }

    public int getUseDuration(ItemStack stack) {
        return drinkable ? 64 * get(stack) / this.max : 0;
    }

    @NotNull
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
}
