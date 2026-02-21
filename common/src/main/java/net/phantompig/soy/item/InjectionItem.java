package net.phantompig.soy.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.TitanInstance;
import net.phantompig.soy.titan.TitanRegistry;

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
        // TODO sound effect
        return InteractionResultHolder.pass(stack);
    }

    private static boolean inject(ItemStack stack, Player player) {
        if (stack.getItem() instanceof InjectionItem inj && player instanceof SoyPlayerExtension ext && ext.getTitanInstance().titan == null) {
            if (inj.get(stack) == inj.max) { // TODO pure titan if injection is not full
                TitanInstance newTitan = new TitanInstance(player);
                newTitan.titan = getRandom(TitanRegistry.getTitans().values().asList());
                newTitan.variant = getRandom(newTitan.titan.variants);
                ext.setTitanInstance(newTitan);
            }

            inj.set(stack, 0);
            return true;
        }
        return false;
    }

    private static <T> T getRandom(List<T> list) {
        return list.get((int) (list.size() * Math.random()));
    }
}
