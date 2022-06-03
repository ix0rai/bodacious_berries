package io.ix0rai.bodaciousberries.mixin;

import io.ix0rai.bodaciousberries.block.BerryVine;
import io.ix0rai.bodaciousberries.registry.Bushes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.treedecorator.LeavesVineTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesVineTreeDecorator.class)
public class VineDecoratorRedirect {
    /**
     * @reason get grapevines to spawn on trees more naturally
     * @author ix0rai
     */
    @Inject(method = "placeVines", at = @At("HEAD"), cancellable = true)
    private static void placeVines(BlockPos pos, BooleanProperty facing, TreeDecorator.class_7402 arg, CallbackInfo ci) {
        //convert world to structure world access so that we can test for vines and air blocks
        final StructureWorldAccess access = (StructureWorldAccess) arg.method_43316();

        //only redirect the method if we're in a jungle biome
        if (access.getBiome(pos).hasTag(BiomeTags.IS_JUNGLE)) {
            placeVine(access, arg, pos, facing);

            //place vines that are hanging down from other vines
            int i = access.getRandom().range(3, 6);
            for(pos = pos.down(); access.isAir(pos) && i > 0; --i) {
                placeVine(access, arg, pos, facing);
                pos = pos.down();
            }

            ci.cancel();
        }
    }

    private static void placeVine(StructureWorldAccess access, TreeDecorator.class_7402 arg, BlockPos pos, BooleanProperty facing) {
        BlockState block = matchBlockAbove(access, pos, facing);

        if (block == null && reallyIncrediblyTremendouslyStupidAwfulHorrendousTerribleHorribleDumbCheck(access, pos)) {
            //otherwise, if reallyIncrediblyStupidAwfulHorrendousDumbCheck confirms that we won't be placing a floating vine, choose a vine or grapevine
            if (access.getBiome(pos).hasTag(BiomeTags.IS_JUNGLE) && access.getRandom().nextInt(6) == 0) {
                block = Bushes.GRAPEVINE.getDefaultState().with(facing, true).with(BerryVine.AGE, 3);
            } else if (access.getBlockState(pos.up()).getBlock() == Blocks.AIR) {
                block = Blocks.VINE.getDefaultState().with(facing, true);
            }
        }

        if (block != null) {
            arg.method_43318(pos, block);
        }
    }

    private static BlockState matchBlockAbove(StructureWorldAccess access, BlockPos pos, BooleanProperty facing) {
        if (access.getBlockState(pos.up()).getBlock() == Blocks.VINE) {
            return Blocks.VINE.getDefaultState().with(facing, true);
        } else if (access.getBlockState(pos.up()).getBlock() == Bushes.GRAPEVINE) {
            return Bushes.GRAPEVINE.getDefaultState().with(facing, true).with(Bushes.GRAPEVINE.getAge(), Bushes.GRAPEVINE.getMaxAge());
        }

        return null;
    }

    private static boolean reallyIncrediblyTremendouslyStupidAwfulHorrendousTerribleHorribleDumbCheck(StructureWorldAccess access, BlockPos pos) {
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
