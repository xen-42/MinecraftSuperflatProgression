package xen42.superflatprogression.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import xen42.superflatprogression.SuperflatProgressionBlocks;

@Mixin(FireBlock.class)
public class FireBlockMixin {
	@Inject(at = @At("HEAD"), method = "scheduledTick")
    private void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        for (var adjBlock : List.of(pos.east(), pos.west(), pos.south(), pos.north(), pos.up(), pos.down())) {
            if (world.getRandom().nextFloat() < 1f / (3 * 20) && CanTurnToCharcoal(world, adjBlock)) {
                world.setBlockState(adjBlock, SuperflatProgressionBlocks.CHARCOAL_BLOCK.getDefaultState());                
            }
        }
    }

    private static boolean CanTurnToCharcoal(ServerWorld world, BlockPos pos) {
        if (world.getBlockState(pos).isIn(BlockTags.LOGS_THAT_BURN)) 
        {
            var count = 0;
            for (var adjacentBlockPos : List.of(pos.east(), pos.west(), pos.north(), pos.south(), pos.up(), pos.down())) {
                if (world.getBlockState(adjacentBlockPos).isOpaque()) {
                    count++;
                }
            }

            if (count >= 4) {
                return true;
            }
        }
        return false;
    }
}
