package io.ix0rai.bodacious_berries.block;

import io.ix0rai.bodacious_berries.registry.Berry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class CloudberryBush extends BasicBerryBush.ThreeStageBush {
    private static final BooleanProperty DYING = BooleanProperty.of("dying");

    public CloudberryBush(Berry berryType, VoxelShape smallShape, VoxelShape largeShape, int sizeChangeAge) {
        super(berryType, smallShape, largeShape, sizeChangeAge);
        this.setDefaultState(this.stateManager.getDefaultState().with(DYING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DYING);
        super.appendProperties(builder);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
        if (Boolean.TRUE.equals(state.get(DYING))) {
            int age = state.get(getAge());
            if (age > 0) {
                world.setBlockState(pos, state.with(getAge(), age - 1));
            } else {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        } else if (pos.getY() < 150) {
            world.setBlockState(pos, state.with(DYING, true));
        } else if (state.get(getAge()) < getMaxAge()) {
            super.randomTick(state, world, pos, random);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity entity, BlockHitResult hitResult) {
        if (Boolean.FALSE.equals(state.get(DYING))) {
            super.onUse(state, world, pos, entity, hitResult);
        }

        return ActionResult.FAIL;
    }
}
