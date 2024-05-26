package io.ix0rai.bodacious_berries.block;

import io.ix0rai.bodacious_berries.registry.Berry;
import io.ix0rai.bodacious_berries.registry.BodaciousBushes;
import io.ix0rai.bodacious_berries.registry.BodaciousSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BasicBerryBush extends PlantBlock implements BerryBush {
    protected static final Vec3d BERRY_BUSH_SLOWING_VECTOR = new Vec3d(0.5D, 0.25D, 0.5D);
    protected static final int GROW_CHANCE = 5;
    protected static final int MAX_BERRY_AMOUNT = 3;

    protected final Berry berry;
    protected final int maxAge;
    protected final VoxelShape smallShape;
    protected final VoxelShape largeShape;
    protected final int sizeChangeAge;

    /**
     * berry bush constructor
     * @param berry which berries will be given when this bush is picked from
     * @param maxAge maximum age bush can grow to
     * @param smallShape small voxel shape for the bush
     * @param largeShape large voxel shape for the bush
     * @param sizeChangeAge the age when the bush switches from smallShape to largeShape, this will also be the age it resets to when berries are picked
     */
    public BasicBerryBush(Berry berry, int maxAge, VoxelShape smallShape, VoxelShape largeShape, int sizeChangeAge) {
        super(BodaciousBushes.BERRY_BUSH_SETTINGS);
        this.berry = berry;
        this.maxAge = maxAge;
        this.smallShape = smallShape;
        this.largeShape = largeShape;
        this.sizeChangeAge = sizeChangeAge;
    }

    /**
     * used for the pick block key
     * @return what kind of berries this block grows
     */
    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return this.getBerryItem().getDefaultStack();
    }

    /**
     * get the shape of this bush
     * @return a {@link VoxelShape} corresponding to its current age
     */
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(getAge()) < sizeChangeAge ? smallShape : largeShape;
    }

    /**
     * runs when this bush is ticked
     * grows the bush if it can, a random throw is met, and light level is high enough
     */
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
        int age = state.get(getAge());
        // if the age isn't maximum and the light level is high enough grow the bush
        if (age < maxAge && random.nextInt(GROW_CHANCE) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            grow(world, pos, state, age + 1);
        }
    }

    /**
     * handles entity collision with our bush
     * <br> if the entity isn't on our list of small entities, slow it
     */
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && !UNSLOWED_ENTITIES.contains(entity.getType()) && state.get(getAge()) >= sizeChangeAge) {
            entity.setMovementMultiplier(state, BERRY_BUSH_SLOWING_VECTOR);
        }
    }

    @Override
    protected ItemInteractionResult onInteract(
            ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockHitResult hitResult
    ) {
        int age = state.get(getAge());
        boolean isMaxAge = age == getMaxAge();
        return !isMaxAge && stack.isOf(Items.BONE_MEAL)
                ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
                : super.onInteract(stack, state, world, pos, entity, hand, hitResult);
    }

    /**
     * handles when our berry bush is right-clicked
     * <br> if the player clicking has bone meal, grow the plant if possible or pick berries if fully grown
     * <br> otherwise, pick berries if possible
     * @return whether the action fails or passes
     */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity entity, BlockHitResult hitResult) {
        final int currentAge = state.get(getAge());
        // if bone meal is allowed to be used, pass action
        if (currentAge == maxAge) {
            // otherwise, give berries/unripe berries
            return pickBerries(pos, world, state, this.getBerryItem());
        } else {
            // otherwise, do default use action from superclass
            return super.onUse(state, world, pos, entity, hitResult);
        }
    }

    /**
     * handles berries being picked
     * <br> this method is static so that it can be used in {@link DoubleBerryBush}
     */
    public static ActionResult pickBerries(BlockPos pos, World world, BlockState state, Item berryType) {
        // we can assume the state to be a berry bush
        BerryBush bush = (BerryBush) state.getBlock();

        int berryAmount = world.random.nextInt(bush.getMaxBerryAmount() + 1) + 1;
        dropStack(world, pos, new ItemStack(berryType, berryAmount));

        // play randomly picked sound
        world.playSound(null, pos, BodaciousSounds.BERRY_PICK, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);

        // reset berry growth; they were just picked
        bush.resetAge(world, pos, state);
        return ActionResult.success(world.isClient);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(getAge());
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return state.get(getAge()) < getMaxAge();
    }

    @Override
    public boolean canFertilize(World world, RandomGenerator random, BlockPos pos, BlockState state) {
        return isFertilizable(world, pos, state);
    }

    @Override
    public void fertilize(ServerWorld world, RandomGenerator random, BlockPos pos, BlockState state) {
        int newAge = Math.min(maxAge, state.get(getAge()) + 1);
        grow(world, pos, state, newAge);
    }

    public void grow(ServerWorld world, BlockPos pos, BlockState state, int newAge) {
        world.setBlockState(pos, state.with(getAge(), newAge), Block.NOTIFY_LISTENERS);
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public int getSizeChangeAge() {
        return sizeChangeAge;
    }

    @Override
    public IntProperty getAge() {
        throw new AssertionError("getAge() should always be overridden");
    }

    @Override
    public Berry getBerry() {
        return berry;
    }

    @Override
    public int getMaxBerryAmount() {
        return MAX_BERRY_AMOUNT;
    }

    public static class FourStageBush extends BasicBerryBush {
        public FourStageBush(Berry berryType, VoxelShape smallShape, VoxelShape largeShape, int sizeChangeAge) {
            super(berryType, 4, smallShape, largeShape, sizeChangeAge);
        }

        @Override
        public IntProperty getAge() {
            return BodaciousBushes.AGE_4;
        }
    }

    public static class ThreeStageBush extends BasicBerryBush {
        public ThreeStageBush(Berry berryType, VoxelShape smallShape, VoxelShape largeShape, int sizeChangeAge) {
            super(berryType, 3, smallShape, largeShape, sizeChangeAge);
        }

        @Override
        public IntProperty getAge() {
            return Properties.AGE_3;
        }
    }
}
