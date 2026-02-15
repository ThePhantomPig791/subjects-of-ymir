package net.phantompig.soy.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.util.IceburstUtil;
import net.threetag.palladium.util.EntityUtil;

public class IceburstItem extends Item {
    public IceburstItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        Vec3 pos = EntityUtil.rayTraceWithEntities(player, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(3.5)), 3.5, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, en -> true).getLocation();
        IceburstUtil.explode(pos, level, 3, AABB.ofSize(pos, 7, 7, 7));
        ItemStack stack = player.getItemInHand(usedHand);
        stack.shrink(1);
        return InteractionResultHolder.success(stack);
    }
}
