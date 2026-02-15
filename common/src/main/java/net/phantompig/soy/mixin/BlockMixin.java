package net.phantompig.soy.mixin;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements ItemLike {
    private BlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "isRandomlyTicking", at = @At("RETURN"), cancellable = true)
    public void soy$isRandomlyTicking(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE) || state.is(Blocks.RAW_IRON_BLOCK)) cir.setReturnValue(true);
    }
}
