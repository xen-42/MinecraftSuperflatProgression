package xen42.superflatprogression.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(Block.class)
public class BlockMixin {
    // Now that end portal frames can be destroyed we must also remove the portal blocks themselves
    // Unlike nether portals the base game doesn't handle this
    @Inject(at = @At("HEAD"), method = "onBreak")
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo info) {
        var block = (Block)(Object)this;
        if (!world.isClient && block instanceof EndPortalFrameBlock endPortalFrame) {
            removeAdjacentPortals(world, pos);
        }
    }

    private void removeAdjacentPortals(World world, BlockPos pos) {
        for (var adj : List.of(Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST)) {
            var adjPos = pos.add(adj.getVector());
            if (world.getBlockState(pos.add(adj.getVector())).isOf(Blocks.END_PORTAL)) {
                world.setBlockState(adjPos, Blocks.AIR.getDefaultState());
                removeAdjacentPortals(world, adjPos);
            }
        }
    }
}
