package net.phantompig.soy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.phantompig.soy.util.IceburstUtil;
import org.jetbrains.annotations.Nullable;

public class IceburstBlock extends Block {
    public final float explosionStrength;

    public IceburstBlock(Properties properties, float explosionStrength) {
        super(properties);
        this.explosionStrength = explosionStrength;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!EnchantmentHelper.hasSilkTouch(tool)) explode(level, pos);
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        explode(level, pos);
    }

    private void explode(LevelAccessor level, BlockPos pos) {
        IceburstUtil.explode(pos.getCenter(), (Level) level, this.explosionStrength, AABB.ofSize(pos.getCenter(), 10, 10, 10));
    }
}
