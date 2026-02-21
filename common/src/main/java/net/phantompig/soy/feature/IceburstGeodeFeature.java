package net.phantompig.soy.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
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

        int r = context.config().size;
        int fullBoxRadius = 2 * r;

        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource rand = context.random();

        for(int x = pos.getX() - fullBoxRadius; x <= pos.getX() + fullBoxRadius; ++x) {
            for (int y = pos.getY() - fullBoxRadius; y <= pos.getY() + fullBoxRadius; ++y) {
                for (int z = pos.getZ() - fullBoxRadius; z <= pos.getZ() + fullBoxRadius; ++z) {
                    BlockPos p = new BlockPos(x, y, z);
                    boolean aboveDeepslate = p.getY() > rand.nextFloat() * 3 + 1;
                    if (withinOvals(p.subtract(pos), 1.5f * r, r, 1, 2, 1) && level.getBlockState(p).getBlock() != Blocks.BEDROCK) {
                        if (aboveDeepslate) level.setBlock(p, Blocks.TUFF.defaultBlockState(), 2);
                        else level.setBlock(p, Blocks.SMOOTH_BASALT.defaultBlockState(), 2);
                    }
                    if (withinOvals(p.subtract(pos), r, 0.75f * r, 1, 2, 1) && level.getBlockState(p).getBlock() != Blocks.BEDROCK) {
                        level.setBlock(p, Blocks.CALCITE.defaultBlockState(), 2);
                    }
                    if (withinOvals(p.subtract(pos), 0.75f * r, r / 2f, 1, 2, 1) && level.getBlockState(p).getBlock() != Blocks.BEDROCK) {
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
                    if (inOval(p.subtract(pos), r / 2f, 1, 2, 1) && level.getBlockState(p).getBlock() != Blocks.BEDROCK) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        return true;
    }

    private static boolean inOval(Vec3i pos, float r, int rx, int ry, int rz) {
        return (Math.pow(pos.getX(), 2) / rx + Math.pow(pos.getY(), 2) / ry + Math.pow(pos.getZ(), 2) / rz) <= r * r;
    }

    private static boolean withinOvals(Vec3i pos, float r, float outerRadius, int rx, int ry, int rz) {
        return (Math.pow(pos.getX(), 2) / rx + Math.pow(pos.getY(), 2) / ry + Math.pow(pos.getZ(), 2) / rz) <= r * r && !inOval(pos, outerRadius, rx, ry, rz);
    }
}
