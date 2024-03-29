package io.ix0rai.bodacious_berries.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DoubleBushFeature extends Feature<DoubleBushFeatureConfig> {
    public DoubleBushFeature(Codec<DoubleBushFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeatureContext<DoubleBushFeatureConfig> context) {
        DoubleBushFeatureConfig config = context.getConfig();
        StructureWorldAccess access = context.getWorld();
        BlockPos pos = context.getOrigin();
        RandomGenerator random = context.getRandom();
        BlockState tallState = config.tallForm().getBlockState(random, pos);
        BlockState babyState = config.babyForm().getBlockState(random, pos);

        if (random.nextBoolean() && tallState.canPlaceAt(access, pos) && access.isAir(pos.up())) {
            TallPlantBlock.placeAt(access, tallState, pos, Block.NOTIFY_LISTENERS);
            access.setBlockState(pos, tallState, Block.NOTIFY_LISTENERS);
        } else {
            access.setBlockState(pos, babyState, Block.NOTIFY_LISTENERS);
        }
        return true;
    }
}
