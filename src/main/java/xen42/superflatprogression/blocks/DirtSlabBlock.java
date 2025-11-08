package xen42.superflatprogression.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
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
					.with(TYPE, world.getBlockState(pos).get(TYPE)));
			} else {
				if (world.getLightLevel(pos.up()) >= 9) {
					BlockState blockState = Blocks.GRASS_BLOCK.getDefaultState();

					// Grass slab can spread to dirt slabs and regular dirt, dirt slabs will handle their own logic to spread from grass
					for (int i = 0; i < 4; i++) {
						BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
						// We are grass slab, it is dirt slab -> change to grass slab with the right slab type
						if (world.getBlockState(blockPos).isOf(SuperflatProgressionBlocks.DIRT_SLAB) && canSpread(blockState, world, blockPos)) {
							var dirtBlockState = world.getBlockState(blockPos);

							world.setBlockState(blockPos, SuperflatProgressionBlocks.GRASS_SLAB.getDefaultState()
								.with(TYPE, dirtBlockState.get(SlabBlock.TYPE)));
						}
						// We are grass, it is dirt -> change it to grass
						else if (world.getBlockState(blockPos).isOf(Blocks.DIRT) && canSpread(blockState, world, blockPos)) {
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
					if (world.getBlockState(blockPos).isOf(Blocks.GRASS_BLOCK) && canSpread(SuperflatProgressionBlocks.GRASS_SLAB.getDefaultState(),
							world, pos)) {
						world.setBlockState(pos, SuperflatProgressionBlocks.GRASS_SLAB.getDefaultState()
							.with(TYPE, world.getBlockState(pos).get(SlabBlock.TYPE)));
					}
				}
			}
		}
	}
}
