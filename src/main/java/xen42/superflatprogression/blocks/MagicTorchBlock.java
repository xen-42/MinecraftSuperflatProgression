package xen42.superflatprogression.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
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

public class MagicTorchBlock extends BlockWithEntity {
	protected static final VoxelShape BOUNDING_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
	protected final ParticleEffect particle;

	public MagicTorchBlock(AbstractBlock.Settings settings, ParticleEffect particle) {
		super(settings);
		this.particle = particle;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return BOUNDING_SHAPE;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return direction == Direction.DOWN && !this.canPlaceAt(state, world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return sideCoversSmallSquare(world, pos.down(), Direction.UP);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = pos.getX() + 0.5;
		double e = pos.getY() + 0.7;
		double f = pos.getZ() + 0.5;
		world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
		world.addParticle(this.particle, d, e, f, 0.0, 0.0, 0.0);
	}

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MagicTorchBlockEntity(pos, state);
    }

    @Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, SuperflatProgressionBlocks.MAGIC_TORCH_ENTITY, MagicTorchBlockEntity::tick);
	}
}
