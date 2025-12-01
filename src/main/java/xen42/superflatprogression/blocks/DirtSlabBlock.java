package xen42.superflatprogression.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionBlocks;

public class DirtSlabBlock extends SlabBlock {
	private boolean isGrass;

    public DirtSlabBlock(Settings settings, boolean isGrass) {
        super(settings);
		this.isGrass = isGrass;
    }

    private static boolean canSurvive(BlockState state, WorldView world, BlockPos pos) {
		var posUp = pos.up();
		var stateUp = world.getBlockState(posUp);
		if (stateUp.getFluidState().getLevel() == 8) {
			return false;
		} else if (state.getProperties().contains(SlabBlock.WATERLOGGED) && state.get(SlabBlock.WATERLOGGED)) {
			return false;
		} else if (state.getProperties().contains(SlabBlock.TYPE) && state.get(TYPE) == SlabType.BOTTOM) {
			return true;
		} else {
			int i = ChunkLightProvider.getRealisticOpacity(world, state.getBlock().getDefaultState(),
				pos, stateUp, posUp, Direction.UP, stateUp.getOpacity(world, posUp));
			return i < world.getMaxLightLevel();
		}
	}
    
	private static boolean canSpread(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.up();
		return canSurvive(state, world, pos) && !world.getFluidState(blockPos).isIn(FluidTags.WATER);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (isGrass) {
			if (!canSurvive(state, world, pos)) {
				// We are grass but can't survive -> change us to dirt with right state
				world.setBlockState(pos, SuperflatProgressionBlocks.DIRT_SLAB.getDefaultState()
					.with(TYPE, world.getBlockState(pos).get(TYPE))
					.with(WATERLOGGED, world.getBlockState(pos).get(WATERLOGGED)));
			} else {
				if (world.getLightLevel(pos.up()) >= 9) {				 
					// Grass slab can spread to dirt slabs and regular dirt, dirt slabs will handle their own logic to spread from grass
					for (int i = 0; i < 4; i++) {
						BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
						// We are grass slab, it is dirt slab -> change to grass slab with the right slab type
						if (world.getBlockState(blockPos).isOf(SuperflatProgressionBlocks.DIRT_SLAB)) {
							var dirtBlockState = world.getBlockState(blockPos);
							BlockState grassBlockState = SuperflatProgressionBlocks.GRASS_SLAB.getDefaultState()
								.with(SlabBlock.TYPE, dirtBlockState.get(SlabBlock.TYPE))
								.with(SlabBlock.WATERLOGGED, dirtBlockState.get(SlabBlock.WATERLOGGED));

							if (canSpread(grassBlockState, world, blockPos)) {
								world.setBlockState(blockPos, grassBlockState);
							}
						}
						// We are grass slab, it is dirt block -> change it to grass block
						else if (world.getBlockState(blockPos).isOf(Blocks.DIRT) && canSpread(Blocks.GRASS_BLOCK.getDefaultState(), world, blockPos)) {
							world.setBlockState(blockPos, Blocks.GRASS_BLOCK.getDefaultState());
						}
					}
				}
			}
		}
		else {
			if (world.getLightLevel(pos.up()) >= 9) {
				for (int i = 0; i < 4; i++) {
					BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					// We are dirt, it is a nearby grass block, spread it to us
					BlockState blockState = SuperflatProgressionBlocks.GRASS_SLAB.getDefaultState()
						.with(SlabBlock.TYPE, state.get(SlabBlock.TYPE))
						.with(SlabBlock.WATERLOGGED, state.get(SlabBlock.WATERLOGGED));
					if (world.getBlockState(blockPos).isOf(Blocks.GRASS_BLOCK) && canSpread(blockState,	world, pos)) {
						world.setBlockState(pos, SuperflatProgressionBlocks.GRASS_SLAB.getDefaultState()
							.with(TYPE, world.getBlockState(pos).get(SlabBlock.TYPE)));
					}
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		BlockState blockState = ctx.getWorld().getBlockState(blockPos);
		if (blockState.getBlock() instanceof DirtSlabBlock) {
			// If we are placing a grass slab on top, make the result be double grass slab
			// If we are placing a dirt slab on top, make the result be double dirt slab
			// If we are placing a slab on the bottom, keep the original block type
			if (blockState.get(TYPE) == SlabType.TOP) {
				return blockState.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
			}
			else {
				return this.getDefaultState().with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
			}			
		} else {
			FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
			BlockState blockState2 = (BlockState)((BlockState)this.getDefaultState().with(TYPE, SlabType.BOTTOM)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
			Direction direction = ctx.getSide();
			return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5)) ? blockState2 : (BlockState)blockState2.with(TYPE, SlabType.TOP);
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		ItemStack itemStack = context.getStack();
		SlabType slabType = (SlabType)state.get(TYPE);
		if (slabType != SlabType.DOUBLE && itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DirtSlabBlock) {
			if (context.canReplaceExisting()) {
				boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5;
				Direction direction = context.getSide();
				if (slabType == SlabType.BOTTOM) {
					return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
				} else {
					return direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}
