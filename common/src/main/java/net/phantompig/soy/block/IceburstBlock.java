package net.phantompig.soy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.phantompig.soy.util.IceburstUtil;

public class IceburstBlock extends Block {
    public final float explosionStrength;

    public IceburstBlock(Properties properties, float explosionStrength) {
        super(properties);
        this.explosionStrength = explosionStrength;
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        // TODO silk touch somehow
        IceburstUtil.explode(pos.getCenter(), (Level) level, this.explosionStrength, AABB.ofSize(pos.getCenter(), 10, 10, 10));
    }
}
