package io.ix0rai.bodaciousberries.mixin;

import io.ix0rai.bodaciousberries.block.BerryVine;
import io.ix0rai.bodaciousberries.registry.Bushes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.LeavesVineTreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.BiConsumer;

@Mixin(LeavesVineTreeDecorator.class)
public class VineDecoratorOverwrite {
    /**
     * reason: get grapevines to spawn on trees more naturally
     * @author ix0rai
     */
    @Overwrite
    private static void placeVines(TestableWorld world, BlockPos pos, BooleanProperty facing, BiConsumer<BlockPos, BlockState> replacer) {
        //convert world to structure world access so that we can test for vines and air blocks
        final StructureWorldAccess access = (StructureWorldAccess) world;

        placeVine(access, replacer, pos, facing);

        //place vines that are hanging down from other vines
        int i = access.getRandom().nextInt(3, 6);
        for(pos = pos.down(); Feature.isAir(world, pos) && i > 0; --i) {
            placeVine(access, replacer, pos, facing);
            pos = pos.down();
        }
    }

    private static void placeVine(StructureWorldAccess access, BiConsumer<BlockPos, BlockState> replacer, BlockPos pos, BooleanProperty facing) {
        BlockState block;

        //if the block above is a vine, return the corresponding vine
        if (access.getBlockState(pos.up()).getBlock() == Blocks.VINE) {
            block = Blocks.VINE.getDefaultState().with(facing, true);
        } else if (access.getBlockState(pos.up()).getBlock() == Bushes.GRAPEVINE){
            block = Bushes.GRAPEVINE.getDefaultState().with(facing, true).with(BerryVine.AGE, 3);
        } else if (reallyIncrediblyStupidAwfulHorrendousDumbCheck(access, pos)) {
            //otherwise, if reallyIncrediblyStupidAwfulHorrendousDumbCheck confirms that we won't be placing a floating vine, choose a vine or grapevine
            if (access.getBiome(pos).getCategory() == Biome.Category.JUNGLE && access.getRandom().nextInt(6) == 0) {
                block = Bushes.GRAPEVINE.getDefaultState().with(facing, true).with(BerryVine.AGE, 3);
            } else if (access.getBlockState(pos.up()).getBlock() == Blocks.AIR) {
                block = Blocks.VINE.getDefaultState().with(facing, true);
            } else {
                return;
            }
        //any other case would leave our newly created vine floating
        } else {
            return;
        }

        replacer.accept(pos, block);
    }

    private static boolean reallyIncrediblyStupidAwfulHorrendousDumbCheck(StructureWorldAccess access, BlockPos pos) {
        final Block east = access.getBlockState(pos.east()).getBlock();
        final Block west = access.getBlockState(pos.west()).getBlock();
        final Block north = access.getBlockState(pos.north()).getBlock();
        final Block south = access.getBlockState(pos.south()).getBlock();

        //block must not be surrounded by air or a non-solid blocks
        //we only return true if it has a supporting solid block
        return east != Blocks.AIR || west != Blocks.AIR || north != Blocks.AIR || south != Blocks.AIR
                && (east.canMobSpawnInside() || west.canMobSpawnInside() || north.canMobSpawnInside() || south.canMobSpawnInside());
    }
}
