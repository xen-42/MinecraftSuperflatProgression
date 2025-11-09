package xen42.superflatprogression.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import xen42.superflatprogression.SuperflatProgressionBlocks;
import xen42.superflatprogression.blocks.EndPortalFrameGenerator;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {
    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR)) {
            // Custom stuff!

            if (EndPortalFrameGenerator.canCreateEndPortalFrame(context.getWorld(), blockState, blockPos)) {
                if (world instanceof ServerWorld) {
                    var blockPosUp = blockPos.up();

                    EndCrystalEntity endCrystalEntity = new EndCrystalEntity(world, blockPosUp.getX() + 0.5, blockPosUp.getY(), blockPosUp.getZ() + 0.5);
                    endCrystalEntity.setShowBottom(false);
                    world.spawnEntity(endCrystalEntity);
                    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPosUp);
                }

                context.getStack().decrement(1);

                info.setReturnValue(ActionResult.SUCCESS);
            } else {
                info.setReturnValue(ActionResult.FAIL);
            }
            info.cancel();
        }
    }
}
