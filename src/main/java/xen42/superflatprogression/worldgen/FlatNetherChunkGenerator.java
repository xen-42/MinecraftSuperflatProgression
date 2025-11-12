package xen42.superflatprogression.worldgen;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.noise.NoiseConfig;

public class FlatNetherChunkGenerator extends FlatChunkGenerator {

	public FlatNetherChunkGenerator(FlatChunkGeneratorConfig config) {
		super(config);
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        // Base game nether is 128 blocks high, say thats like 60% netherrack, 128x0.6 = 77 blocks in a column
        // Flat nether is 3 blocks per column
        // Base game nether has 1.66 ancient debris per chunk, to maintain the same relative abundance of ancient debris to blocks the flat nether
        // needs 77/3 = 25.66 times less in a chunk -> 0.06 per chunk -> 1 debris in every 16 chunks
        // That sucks
        // Actually wait most of it is between y=5 and y=25 thats only 20 blocks in a column so 20/3 = 6 times less so like 1 in every 6 chunks
        // For a full set you'll need to strip mine like 24*6 = 144 chunks wtf
        // Ok to strip a chunk u need to dig 8x8x2=128 blocks, so thats 128x144 = 18000 blocks
        // Say each block takes like 0.2 seconds thats uh actually thats just like 1 hour thats fine you can mine for one hour cant you
		if (region.getRandom().nextFloat() > 1f/6f) {
            var x = region.getRandom().nextInt(16);
            var z = region.getRandom().nextInt(16);
            var y = region.getRandom().nextInt(2) + 1;
            chunk.setBlockState(new BlockPos(x, y, z), Blocks.ANCIENT_DEBRIS.getDefaultState(), false);
        }
	}
}
