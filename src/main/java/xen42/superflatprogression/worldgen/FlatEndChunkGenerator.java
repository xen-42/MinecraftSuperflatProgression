package xen42.superflatprogression.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
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

    private MinecraftServer server;
    private ChunkGeneratorSettings settings;
    private FlatChunkGeneratorConfig config;

    public FlatEndChunkGenerator(FlatChunkGeneratorConfig config, ChunkGeneratorSettings settings, MinecraftServer server, ServerWorld world) {
        super(config);
        this.config = config;
        this.server = server;
        this.settings = settings;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        var cx = chunk.getPos().x;
        var cz = chunk.getPos().z;
        if (cx == 0 && cz == 0) {
            for (int y = 40; y < 67; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        chunk.setBlockState(new BlockPos(3 + x, y, 3 + z), Blocks.BEDROCK.getDefaultState(), false);
                    }
                }
            }
            var start = 0;
            var end = 7;
            for (int x = start; x < end; x++) {
                for (int z = start; z < end; z++) {
                    if (x == start && z == start || x == end && z == start || x == 0 && z == end || x == 6 && z == end) {
                        //continue;
                    }
                    for (int y = 40; y < 66; y++) {
                        chunk.setBlockState(new BlockPos(x, y, z), Blocks.BEDROCK.getDefaultState(), false);
                    }
                }
            }
        }        

        for (var spike : EndSpikeFeature.getSpikes(region)) {
            if (spike.isInChunk(chunk.getPos().getBlockPos(0, 0, 0))) {
                createSpire(spike, region, chunk);
            }
        }
    }

    private void createSpire(Spike spike, ChunkRegion region, Chunk chunk) {
        SuperflatProgression.LOGGER.info("What are the spike positions (x) : " + spike.getCenterX());

        var r = spike.getRadius();
        for(int y = 50; y < spike.getHeight(); y++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + z * z <= r * r + 1) {
                        chunk.setBlockState(new BlockPos(8 + x, y, 8 + z), Blocks.OBSIDIAN.getDefaultState(), false);
                    }
                }
            }
        }

        if (spike.isGuarded()) {
            for(int dx = -2; dx <= 2; ++dx) {
                for(int dz = -2; dz <= 2; ++dz) {
                    for(int dy = 0; dy <= 3; ++dy) {
                        boolean xAxisWall = MathHelper.abs(dx) == 2;
                        boolean zAxisWall = MathHelper.abs(dz) == 2;
                        boolean ceiling = dy == 3;
                        if (xAxisWall || zAxisWall || ceiling) {
                            boolean bl4 = dx == -2 || dx == 2 || ceiling;
                            boolean bl5 = dz == -2 || dz == 2 || ceiling;
                            var blockState = Blocks.IRON_BARS.getDefaultState()
                                .with(PaneBlock.NORTH, bl4 && dz != -2)
                                .with(PaneBlock.SOUTH, bl4 && dz != 2)
                                .with(PaneBlock.WEST, bl5 && dx != -2)
                                .with(PaneBlock.EAST, bl5 && dx != 2);
                            chunk.setBlockState(new BlockPos(8 + dx, spike.getHeight() + dy, 8 + dz), blockState, false);
                        }
                    }
                }
            }
        }

        EndCrystalEntity endCrystalEntity = EntityType.END_CRYSTAL.create(region.toServerWorld());
        if (endCrystalEntity != null) {
            var chunkCenter = chunk.getPos().getBlockPos(8, spike.getHeight(), 8);

            endCrystalEntity.refreshPositionAndAngles(chunkCenter.getX() + 0.5, spike.getHeight() + 1, chunkCenter.getZ() + 0.5, 
                region.getRandom().nextFloat() * 360.0F, 0.0F);
            region.toServerWorld().spawnEntity(endCrystalEntity);
            chunk.setBlockState(new BlockPos(8, spike.getHeight(), 8), Blocks.BEDROCK.getDefaultState(), false);
        }
    }
}
