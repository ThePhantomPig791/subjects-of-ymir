package net.phantompig.soy.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.util.ShapeUtil;

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

        int r = context.config().size + rand.nextInt(-2, 1);
        if (pos.getY() >= 30) r -= 2;
        if (pos.getY() <= -15) r++;
        if (pos.getY() <= -30) r++;
        if (r == 4) r--; // hate 4
        r = Math.max(1, r);
        int fullBoxRadius = 2 * r;

        for(int x = pos.getX() - fullBoxRadius; x <= pos.getX() + fullBoxRadius; ++x) {
            for (int y = pos.getY() - fullBoxRadius; y <= pos.getY() + fullBoxRadius; ++y) {
                for (int z = pos.getZ() - fullBoxRadius; z <= pos.getZ() + fullBoxRadius; ++z) {
                    BlockPos p = new BlockPos(x, y, z);
                    boolean aboveDeepslate = p.getY() > rand.nextFloat() * 3 + 1;
                    if (ShapeUtil.withinOvals(p.subtract(pos), 1.5f * r, r, 1, 2, 1) && level.getBlockState(p).getBlock() != Blocks.BEDROCK) {
                        if (aboveDeepslate) level.setBlock(p, Blocks.TUFF.defaultBlockState(), 2);
                        else level.setBlock(p, Blocks.SMOOTH_BASALT.defaultBlockState(), 2);
                    }
                    if (ShapeUtil.withinOvals(p.subtract(pos), r, 0.75f * r, 1, 2, 1) && level.getBlockState(p).getBlock() != Blocks.BEDROCK) {
                        level.setBlock(p, Blocks.CALCITE.defaultBlockState(), 2);
                    }
                    if (ShapeUtil.withinOvals(p.subtract(pos), 0.75f * r, 0.5f * r, 1, 2, 1) && level.getBlockState(p).getBlock() != Blocks.BEDROCK) {
                        int i = rand.nextInt(100);
                        if (aboveDeepslate) {
                            if (i <= 5) level.setBlock(p, Blocks.DIAMOND_ORE.defaultBlockState(), 2);
                            else if (i <= 45) level.setBlock(p, SoyBlocks.ICEBURST_ORE.get().defaultBlockState(), 2);
                            else if (i <= 55) level.setBlock(p, Blocks.BLUE_ICE.defaultBlockState(), 2);
                            else level.setBlock(p, Blocks.STONE.defaultBlockState(), 2);
                        } else {
                            if (i <= 20) level.setBlock(p, Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState(), 2);
                            else if (i <= 60) level.setBlock(p, SoyBlocks.DEEPSLATE_ICEBURST_ORE.get().defaultBlockState(), 2);
                            else if (i <= 85) level.setBlock(p, Blocks.BLUE_ICE.defaultBlockState(), 2);
                            else level.setBlock(p, Blocks.DEEPSLATE.defaultBlockState(), 2);
                        }
                    }
                    if (ShapeUtil.inOval(p.subtract(pos), 0.5f * r, 1, 2, 1) && level.getBlockState(p).getBlock() != Blocks.BEDROCK) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        return true;
    }
}
