package xen42.superflatprogression.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "getHardness", at = @At("RETURN"), cancellable = true)
    private void getHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
        var blockState = (BlockState)(Object)this;
        if (blockState.getBlock() instanceof EndPortalFrameBlock) {
            info.setReturnValue(20f);
            info.cancel();
        }
    }
}
