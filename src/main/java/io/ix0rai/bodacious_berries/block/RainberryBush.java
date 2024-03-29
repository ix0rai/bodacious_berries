package io.ix0rai.bodacious_berries.block;

import io.ix0rai.bodacious_berries.client.particle.Particles;
import io.ix0rai.bodacious_berries.registry.Berry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
public class RainberryBush extends BasicBerryBush.FourStageBush {
    public RainberryBush(Berry berry, VoxelShape smallShape, VoxelShape largeShape, int sizeChangeAge) {
        super(berry, smallShape, largeShape, sizeChangeAge);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
        if (state.get(getAge()) == maxAge) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 0.7D;
            double z = pos.getZ() + random.nextDouble();
            world.addParticle(Particles.RAINBOW_PARTICLE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
