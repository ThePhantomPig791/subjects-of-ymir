package net.phantompig.soy.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.stat.SoyStats;
import net.phantompig.soy.titan.TitanInstance;
import net.phantompig.soy.titan.TitanRegistry;
import net.threetag.palladium.util.PlayerUtil;

import java.util.List;

public class InjectionItem extends SpinalFluidHoldingItem {
    public InjectionItem(Properties properties) {
        super(properties, 100);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) return InteractionResultHolder.pass(stack);
        if (player.isCrouching() && inject(stack, player)) return InteractionResultHolder.consume(stack);
        // TODO injecting others
        return InteractionResultHolder.pass(stack);
    }

    private static boolean inject(ItemStack stack, Player player) {
        if (stack.getItem() instanceof InjectionItem inj && player instanceof SoyPlayerExtension ext) {
            if (ext.getTitanInstance().titan == null) {
                if (inj.get(stack) == inj.max) {
                    TitanInstance newTitan = new TitanInstance(player);
                    newTitan.titan = getRandom(TitanRegistry.getTitans().values().asList()); // TODO investigate: this may have gotten me into a weird ghost state where the attack was rendered but i gained the power of the test titan
                    newTitan.variant = getRandom(newTitan.titan.variants);
                    ext.setTitanInstance(newTitan);

                    injectSound(player.level(), player.getX(), player.getY(), player.getZ());
                    inj.set(stack, 0);
                    return true;
                } else {
                    return false; // TODO pure titan if injection is not full
                }
            } else if (inj.get(stack) > 0) {
                SoyProperties.PATH_POINTS.set(player, SoyProperties.PATH_POINTS.get(player) + inj.get(stack));
                player.awardStat(SoyStats.PATH_POINTS_GAINED, inj.get(stack));
                inj.set(stack, 0);
                return true;
            }
        }
        return false;
    }

    private static <T> T getRandom(List<T> list) {
        return list.get((int) (list.size() * Math.random()));
    }

    private static void injectSound(Level level, double x, double y, double z) {
        // TODO better sound effect
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.HONEY_BLOCK_STEP, SoundSource.PLAYERS, 0.5f, 2);
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.5f, 1.9f);
    }
}
