package net.phantompig.soy.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.phantompig.soy.block.SoyBlocks;

public class IceburstGeodeFeature extends Feature<OreConfiguration> {
    public IceburstGeodeFeature(Codec<OreConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<OreConfiguration> context) {
        for (OreConfiguration.TargetBlockState state : context.config().targetStates) {
            if (!state.target.test(context.level().getBlockState(context.origin()), context.random())) {
                return false;
            }
        }

        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource rand = context.random();

        level.setBlock(pos, SoyBlocks.ICEBURST_ORE.get().defaultBlockState(), 2);

        return true;
    }
}
