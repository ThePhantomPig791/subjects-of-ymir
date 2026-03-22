package net.phantompig.soy.item;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.util.IceburstUtil;
import net.threetag.palladium.util.EntityUtil;
import org.jetbrains.annotations.NotNull;

public class IceburstItem extends Item {
    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        @NotNull
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            Position dPos = DispenserBlock.getDispensePosition(source);
            Vec3 pos = new Vec3(dPos.x(), dPos.y(), dPos.z());
            IceburstUtil.explode(pos, source.getLevel(), 3, AABB.ofSize(pos, 7, 7, 7));
            if (!EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.INFINITY_ARROWS)) stack.shrink(1);
            return stack;
        }
    };

    public IceburstItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        Vec3 pos = EntityUtil.rayTraceWithEntities(player, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(3.5)), 3.5, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, en -> true).getLocation();
        IceburstUtil.explode(pos, level, 3, AABB.ofSize(pos, 7, 7, 7));
        ItemStack stack = player.getItemInHand(usedHand);
        if (!player.isCreative() && !EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.INFINITY_ARROWS)) stack.shrink(1);
        return InteractionResultHolder.success(stack);
    }
}
