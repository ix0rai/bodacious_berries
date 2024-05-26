package io.ix0rai.bodacious_berries.block;

import io.ix0rai.bodacious_berries.registry.Berry;
import io.ix0rai.bodacious_berries.registry.BodaciousBushes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemInteractionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class DoubleBerryBush extends TallPlantBlock implements BerryBush {
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;

    protected static final Vec3d DOUBLE_BUSH_SLOWING_VECTOR = new Vec3d(0.7D, 0.9D, 0.7D);
    protected static final int MAX_BERRY_AMOUNT = 5;

    protected final Berry berry;

    public DoubleBerryBush(Berry berry) {
        super(BodaciousBushes.BERRY_BUSH_SETTINGS);
        this.berry = berry;
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return this.getBerryItem().getDefaultStack();
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && !BasicBerryBush.UNSLOWED_ENTITIES.contains(entity.getType())) {
            entity.setMovementMultiplier(state, DOUBLE_BUSH_SLOWING_VECTOR);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(getAge()).add(HALF);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
        int age = state.get(getAge());
        // if the age isn't maximum and the light level is high enough grow the bush
        if (age < MAX_AGE && random.nextInt(5) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            world.setBlockState(pos, state.with(getAge(), age + 1), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return state.get(getAge()) < MAX_AGE;
    }

    @Override
    public boolean canFertilize(World world, RandomGenerator random, BlockPos pos, BlockState state) {
        return isFertilizable(world, pos, state);
    }

    @Override
    public void fertilize(ServerWorld world, RandomGenerator random, BlockPos pos, BlockState state) {
        int newBerryAge = Math.min(MAX_AGE, state.get(getAge()) + 1);
        grow(world, pos, state, newBerryAge);
    }

    public void grow(ServerWorld world, BlockPos pos, BlockState state, int newAge) {
        world.setBlockState(pos, state.with(getAge(), newAge), Block.NOTIFY_LISTENERS);
    }

    @Override
    protected ItemInteractionResult onInteract(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockHitResult hitResult) {
        int age = state.get(AGE);
        boolean isMaxAge = age == MAX_AGE;
        return !isMaxAge && stack.isOf(Items.BONE_MEAL)
                ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
                : super.onInteract(stack, state, world, pos, entity, hand, hitResult);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hitResult) {
        if (state.get(getAge()) == MAX_AGE) {
            return BasicBerryBush.pickBerries(pos, world, state, this.getBerryItem());
        } else {
            return super.onUse(state, world, pos, player, hitResult);
        }
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public int getSizeChangeAge() {
        return 0;
    }

    @Override
    public IntProperty getAge() {
        return AGE;
    }

    @Override
    public Berry getBerry() {
        return berry;
    }

    @Override
    public int getMaxBerryAmount() {
        return MAX_BERRY_AMOUNT;
    }
}
