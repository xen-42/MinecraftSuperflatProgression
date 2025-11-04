package xen42.superflatprogression.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgressionBlocks;

public class MagicLanternBlock extends LanternBlock implements BlockEntityProvider {
    public MagicLanternBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MagicTorchBlockEntity(pos, state);
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
		return checkType(type, SuperflatProgressionBlocks.MAGIC_TORCH_ENTITY, MagicTorchBlockEntity::tick);
	}
}
