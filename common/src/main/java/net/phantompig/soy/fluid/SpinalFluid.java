package net.phantompig.soy.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.phantompig.soy.block.SoyBlocks;
import net.threetag.palladium.util.PlayerUtil;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Optional;

public abstract class SpinalFluid extends FlowingFluid {
    public static final ResourceLocation TEXTURE_STILL = new ResourceLocation("block/water_still");
    public static final ResourceLocation TEXTURE_FLOW = new ResourceLocation("block/water_flow");
    public static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation("block/water_overlay");
    public static final int COLOR = new Color(170, 30, 190).getRGB();
    public static final Vector3f FOG = new Vector3f(0.5f, 0.15f, 0.6f);

    public SpinalFluid() {

    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
        super.tick(level, pos, state);
        if (state.isSource()
                && (
                        state.getHeight(level, pos.north()) > 0.8
                        || state.getHeight(level, pos.east()) > 0.8
                        || state.getHeight(level, pos.south()) > 0.8
                        || state.getHeight(level, pos.west()) > 0.8
            )
        ) level.setBlock(pos, level.getBlockState(pos).setValue(LiquidBlock.LEVEL, 7), Block.UPDATE_ALL);
        PlayerUtil.spawnParticleForAll(level, 64,
                (ParticleOptions) ParticleTypes.ENTITY_EFFECT,
                false,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                0.5f,
                state.getOwnHeight(),
                0.5f,
                1,
                1
        );
    }

    @Override
    public Fluid getSource() {
        return SoyFluids.SPINAL_FLUID.get();
    }

    @Override
    public Fluid getFlowing() {
        return SoyFluids.FLOWING_SPINAL_FLUID.get();
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return 2;
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == SoyFluids.SPINAL_FLUID.get() || fluid == SoyFluids.FLOWING_SPINAL_FLUID.get();
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return 1;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 3;
    }

    @Override
    public Item getBucket() {
        return Items.BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.is(SoyFluidTags.SPINAL_FLUID);
    }

    @Override
    protected float getExplosionResistance() {
        return 20;
    }

    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return SoyBlocks.SPINAL_FLUID.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public static class Source extends SpinalFluid {
        public Source() {

        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
    public static class Flowing extends SpinalFluid {
        public Flowing() {
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return (Integer)state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }
}
