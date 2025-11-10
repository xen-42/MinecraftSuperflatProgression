package xen42.superflatprogression.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature.Spike;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.noise.NoiseConfig;
import xen42.superflatprogression.SuperflatProgression;

public class FlatEndChunkGenerator extends FlatChunkGenerator {

	public FlatEndChunkGenerator(FlatChunkGeneratorConfig config) {
		super(config);
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
		for (var spike : EndSpikeFeature.getSpikes(region)) {
			if (checkChunkOverlap(spike, chunk)) {
				createSpire(spike, region, chunk);
			}
		}
	}

	private boolean checkChunkOverlap(Spike spike, Chunk chunk) {
		return chunk.getPos().getBlockPos(0, 0, 0).isWithinDistance(new BlockPos(spike.getCenterX(), 0, spike.getCenterZ()), 32);
	}

	private void createSpire(Spike spike, ChunkRegion region, Chunk chunk) {
		var r = spike.getRadius();
		for(int y = 50; y < spike.getHeight(); y++) {
			for (int x = -r; x <= r; x++) {
				for (int z = -r; z <= r; z++) {
					if (x * x + z * z <= r * r + 1) {
						var dx = spike.getCenterX() + x - (chunk.getPos().x * 16);
						var dz = spike.getCenterZ() + z - (chunk.getPos().z * 16);

						if (dx >= 0 && dx < 16 && dz >= 0 && dz < 16) {
							chunk.setBlockState(new BlockPos(dx, y, dz), Blocks.OBSIDIAN.getDefaultState(), false);
						}
					}
				}
			}
		}

		if (spike.isGuarded()) {
			for(int x = -2; x <= 2; ++x) {
				for(int z = -2; z <= 2; ++z) {
					for(int y = 0; y <= 3; ++y) {
						boolean xAxisWall = MathHelper.abs(x) == 2;
						boolean zAxisWall = MathHelper.abs(z) == 2;
						boolean ceiling = y == 3;
						if (xAxisWall || zAxisWall || ceiling) {

							var dx = spike.getCenterX() + x - (chunk.getPos().x * 16);
							var dz = spike.getCenterZ() + z - (chunk.getPos().z * 16);

							if (dx >= 0 && dx < 16 && dz >= 0 && dz < 16) {
								boolean bl4 = x == -2 || x == 2 || ceiling;
								boolean bl5 = z == -2 || z == 2 || ceiling;
								var blockState = Blocks.IRON_BARS.getDefaultState()
									.with(PaneBlock.NORTH, bl4 && z != -2)
									.with(PaneBlock.SOUTH, bl4 && z != 2)
									.with(PaneBlock.WEST, bl5 && x != -2)
									.with(PaneBlock.EAST, bl5 && x != 2);
								chunk.setBlockState(new BlockPos(dx, spike.getHeight() + y, dz), blockState, false);
							}
						}
					}
				}
			}
		}

		EndCrystalEntity endCrystalEntity = EntityType.END_CRYSTAL.create(region.toServerWorld());
		if (endCrystalEntity != null) {
			if (spike.isInChunk(chunk.getPos().getBlockPos(0, 0, 0))) {
				endCrystalEntity.refreshPositionAndAngles(spike.getCenterX() + 0.5, spike.getHeight() + 1, spike.getCenterZ() + 0.5, 
					region.getRandom().nextFloat() * 360.0F, 0.0F);
				region.toServerWorld().spawnEntity(endCrystalEntity);

				var dx = spike.getCenterX() - (chunk.getPos().x * 16);
				var dz = spike.getCenterZ() - (chunk.getPos().z * 16);
				chunk.setBlockState(new BlockPos(dx, spike.getHeight(), dz), Blocks.BEDROCK.getDefaultState(), false);
			}
		}
	}
}
