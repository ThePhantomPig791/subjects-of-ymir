package net.phantompig.soy.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.phantompig.soy.SoyConfig;
import net.threetag.palladium.util.EntityUtil;
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
        if (SoyConfig.Server.canInjectOthers()) {
            HitResult hit = EntityUtil.rayTraceWithEntities(player, 3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE);
            if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof Player p) {
                if (inject(stack, p)) return InteractionResultHolder.consume(stack); // TODO figure out what happens if it's not a player like conceptually
            }
        }
        return super.use(level, player, usedHand);
    }
}
