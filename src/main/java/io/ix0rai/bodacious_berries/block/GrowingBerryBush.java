package io.ix0rai.bodacious_berries.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemInteractionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class GrowingBerryBush extends BasicBerryBush {
    private final DoubleBerryBush futureBush;

    public GrowingBerryBush(VoxelShape smallShape, VoxelShape largeShape, DoubleBerryBush bush) {
        super(bush.getBerry(), 2, smallShape, largeShape, 2);
        this.futureBush = bush;
    }

    @Override
    public void grow(ServerWorld world, BlockPos pos, BlockState state, int newAge) {
        if (newAge < maxAge) {
            world.setBlockState(pos, state.with(getAge(), newAge), Block.NOTIFY_LISTENERS);
        } else {
            TallPlantBlock.placeAt(world, futureBush.getDefaultState(), pos, Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
        int age = state.get(getAge());
        if (random.nextInt(GROW_CHANCE) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            grow(world, pos, state, age + 1);
        }
    }

    @Override
    protected ItemInteractionResult onInteract(
            ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockHitResult hitResult
    ) {
        int i = state.get(getAge());
        boolean isMaxAge = i == getMaxAge();
        if (isMaxAge && stack.isOf(Items.BONE_MEAL)) {
            final int newAge = Math.min(maxAge, state.get(getAge()) + 1);
            // grow to a double bush if new age exceeds maximum
            if (newAge > maxAge) {
                TallPlantBlock.placeAt(world, futureBush.getDefaultState(), pos, Block.NOTIFY_LISTENERS);
            }

            return ItemInteractionResult.CONSUME;
        }

        return super.onInteract(stack, state, world, pos, entity, hand, hitResult);
    }

    @Override
    public IntProperty getAge() {
        return Properties.AGE_2;
    }
}
