package xen42.superflatprogression.blocks;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgressionBlocks;
import xen42.superflatprogression.entities.EndPortalFrameGeneratorEntity;
import xen42.superflatprogression.entities.MagicTorchBlockEntity;

public class EndPortalFrameGeneratorBlock extends Block implements BlockEntityProvider {

    public EndPortalFrameGeneratorBlock(Settings settings) {
        super(settings);
    }
    
    public static boolean canCreateEndPortalFrame(World world, BlockState state, BlockPos pos) {
        if (world.getRegistryKey() != World.OVERWORLD) {
            return false;
        }

        for (var dir : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) {
            for (int i = -1; i <= 1; i++) {
                var framePos = pos.subtract(dir.getVector().multiply(2)).add(dir.getVector().crossProduct(new Vec3i(0, 1, 0).multiply(i))); 
                if (!world.getBlockState(framePos).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void tryCreateEndPortalFrame(ServerWorld world, BlockState state, BlockPos pos) {
        if (canCreateEndPortalFrame(world, state, pos)) {
            for (var dir : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) {
                for (int i = -1; i <= 1; i++) {
                    var framePos = pos.subtract(dir.getVector().multiply(2)).add(dir.getVector().crossProduct(new Vec3i(0, 1, 0).multiply(i)));
                    world.setBlockState(framePos, Blocks.END_PORTAL_FRAME.getDefaultState()
                        .with(EndPortalFrameBlock.FACING, dir)
                        .with(EndPortalFrameBlock.EYE, world.getRandom().nextFloat() < 0.1));
                }
            }

            // Remove this block when done
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EndPortalFrameGeneratorEntity(pos, state);
    }

	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(
		BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker
	) {
		return expectedType == givenType ? (@Nullable BlockEntityTicker<A>)ticker : null;
	}

    @Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR_ENTITY, EndPortalFrameGeneratorEntity::tick);
	}
}
