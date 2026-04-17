package net.phantompig.soy.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class InjectionItem extends SpinalFluidHoldingItem {
    public static final int MAX = 100;

    public InjectionItem(Properties properties) {
        super(properties, MAX);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) return InteractionResultHolder.pass(stack);
        if (player.isCrouching()) {
            if (inject(stack, player)) return InteractionResultHolder.consume(stack);
        }
        // TODO injecting others
        return super.use(level, player, usedHand);
    }
}
