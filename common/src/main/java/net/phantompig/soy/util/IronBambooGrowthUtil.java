package net.phantompig.soy.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.block.SoyBlockTags;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.item.SoyItems;
import net.threetag.palladium.util.PlayerUtil;

public class IronBambooGrowthUtil {
    public static void ironTick(BlockState state, BlockPos pos, Level level) {
        BlockPos bamboo = null;
        for (int i = 1; i < 25; i++) {
            if (level.getBlockState(pos.offset(0, i, 0)).is(Blocks.BAMBOO)) {
                bamboo = pos.offset(0, i, 0);
                break;
            }
            if (level.getBlockState(pos.offset(0, i, 0)).is(SoyBlockTags.IRON_BAMBOO_CAN_ROOT_THROUGH)) continue;
            return;
        }
        if (bamboo == null) return;
        BlockState bambooBlock = level.getBlockState(bamboo);
        if (bambooBlock.getValue(BlockStateProperties.BAMBOO_LEAVES) == BambooLeaves.SMALL) {
            spawnLeaves(level, bamboo, 2);
        }
        if (bambooBlock.getValue(BlockStateProperties.BAMBOO_LEAVES) == BambooLeaves.SMALL) {
            spawnLeaves(level, bamboo, 4);
        }
        level.setBlock(bamboo, SoyBlocks.IRON_BAMBOO.get().defaultBlockState().setValue(BambooStalkBlock.AGE, bambooBlock.getValue(BambooStalkBlock.AGE)).setValue(BambooStalkBlock.STAGE, 1), 3);

        PlayerUtil.spawnParticleForAll(
                level,
                32,
                ParticleTypes.CLOUD,
                false,
                pos.getCenter().x,
                pos.getCenter().y,
                pos.getCenter().z,
                0.75f,
                0.75f,
                0.75f,
                0.1f,
                8
        );
        PlayerUtil.playSoundToAll(level, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, 24, level.getBlockState(pos).getSoundType().getBreakSound(), SoundSource.BLOCKS);
        if (level.getBlockState(pos).is(Blocks.DEEPSLATE_IRON_ORE)) level.setBlock(pos, Blocks.DEEPSLATE.defaultBlockState(), 3);
        else if (level.getBlockState(pos).is(Blocks.IRON_ORE)) level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
    }

    public static void spawnLeaves(Level level, BlockPos pos, int max) {
        ItemStack leaves = new ItemStack(SoyItems.IRON_BAMBOO_LEAF.get());
        leaves.setCount((int) (1 + max * Math.random()));
        ItemEntity entity = new ItemEntity(level, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, leaves);
        entity.addDeltaMovement(new Vec3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).scale(0.5));
        level.addFreshEntity(entity);
    }
}
