package xen42.superflatprogression.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import xen42.superflatprogression.SuperflatProgressionBlocks;

public class MagicTorchBlock extends TorchBlock implements BlockEntityProvider {

    public MagicTorchBlock(Settings settings, ParticleEffect particle) {
		super(settings, particle);
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
