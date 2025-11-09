package xen42.superflatprogression.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {
        for (var dir : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) {
            var adjPos = pos.add(dir.getVector());
            BlockState blockState = world.getBlockState(adjPos);
            if (!blockState.isOf(Blocks.END_PORTAL) && !blockState.isOf(Blocks.END_PORTAL_FRAME)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }
}
