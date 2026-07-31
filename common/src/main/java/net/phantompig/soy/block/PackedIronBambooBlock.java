package net.phantompig.soy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PackedIronBambooBlock extends RotatedPillarBlock {
    public PackedIronBambooBlock(Properties properties) {
        super(properties);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (player.hasCorrectToolForDrops(state)) {
            Direction miningDirection = level.clip(new ClipContext(
                    player.getEyePosition(),
                    player.getEyePosition().add(player.getLookAngle().scale(5)),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
            )).getDirection();
            if (miningDirection.getAxis().equals(state.getValue(AXIS))) return 1;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }
}
