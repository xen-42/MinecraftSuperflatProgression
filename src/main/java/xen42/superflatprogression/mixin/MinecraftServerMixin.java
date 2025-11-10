package xen42.superflatprogression.mixin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.DataFixer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.structure.StructureSets;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnDensityCapper;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature.Spike;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.StructureKeys;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.Spawner;
import xen42.superflatprogression.CustomSpawner;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.worldgen.FlatEndChunkGenerator;

@Mixin(MinecraftServer.class)

public class MinecraftServerMixin {
    // This is what makes the nether also superflat
	@Inject(at = @At("RETURN"), method = "createWorlds")
    private void createWorlds(WorldGenerationProgressListener listener, CallbackInfo info) {
        try {
            MinecraftServer server = (MinecraftServer) (Object) this;

            ServerWorld overworld = server.getWorld(World.OVERWORLD);
            ServerWorld nether = server.getWorld(World.NETHER);
            ServerWorld end = server.getWorld(World.END);

            if (overworld == null || nether == null) return;

            // Only superflat the Nether if the Overworld is superflat
            if (!(overworld.getChunkManager().getChunkGenerator() instanceof FlatChunkGenerator)) return;

            var structuresEnabled = server.getSaveProperties().getGeneratorOptions().shouldGenerateStructures();;

            var netherConfig = new FlatChunkGeneratorConfig(
                Optional.empty(),
                server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(BiomeKeys.NETHER_WASTES).get(),
                List.of()
            );
            netherConfig.getLayerBlocks().add(Blocks.BEDROCK.getDefaultState());
            netherConfig.getLayerBlocks().add(Blocks.NETHERRACK.getDefaultState());
            netherConfig.getLayerBlocks().add(Blocks.NETHERRACK.getDefaultState());
            netherConfig.getLayerBlocks().add(Blocks.NETHERRACK.getDefaultState());
            MakeWorldSuperflat(server, listener, nether, netherConfig);

            // If no structures make blazes and wither skeletons just spawn
            if (!structuresEnabled) {
                Field spawnersField = ServerWorld.class.getDeclaredField("spawners");
                spawnersField.setAccessible(true);
                List<Spawner> original = (List<Spawner>) spawnersField.get(nether);
                var spawners = new ArrayList<>();
                spawners.addAll(original);
                spawners.add(new CustomSpawner(EntityType.BLAZE)); 
                spawners.add(new CustomSpawner(EntityType.WITHER_SKELETON)); 
                spawnersField.set(nether, spawners);
            }

            var endCities = server.getRegistryManager().get(RegistryKeys.STRUCTURE_SET).getEntry(StructureSetKeys.END_CITIES).get();
            var endConfig = new FlatChunkGeneratorConfig(
                structuresEnabled ? Optional.of(RegistryEntryList.of(endCities)) : Optional.empty(),
                server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(BiomeKeys.END_HIGHLANDS).get(),
                List.of()
                //List.of(getPlacedFeature(server, "chorus_plant"))
            );
            endConfig.enableFeatures();
            
            // Spawn platform is always at 49, but End Cities only spawn above y level 64
            for (int i = 0; i < 47; i++) {
                endConfig.getLayerBlocks().add(Blocks.AIR.getDefaultState());
            }
            endConfig.getLayerBlocks().add(Blocks.BEDROCK.getDefaultState());
            for (int i = 0; i < 63-48; i++) {
                endConfig.getLayerBlocks().add(Blocks.END_STONE.getDefaultState());
            }

            if (!structuresEnabled) {
                Field spawnersField = ServerWorld.class.getDeclaredField("spawners");
                spawnersField.setAccessible(true);
                List<Spawner> original = (List<Spawner>) spawnersField.get(end);
                var spawners = new ArrayList<>();
                spawners.addAll(original);
                spawners.add(new CustomSpawner(EntityType.SHULKER)); 
                spawnersField.set(end, spawners);
            }

            MakeWorldSuperflat(server, listener, end, endConfig);       
        } catch (Exception e) {
            SuperflatProgression.LOGGER.error("Failed to make worlds superflat", e);
        }
    }

    private RegistryEntry<PlacedFeature> getPlacedFeature(MinecraftServer server, String name) {
        var key = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("minecraft", name));
        var value = server.getRegistryManager().get(RegistryKeys.PLACED_FEATURE).entryOf(key);
        return value;
    };

    private ChunkGenerator MakeWorldSuperflat(MinecraftServer server, WorldGenerationProgressListener listener, ServerWorld dimension, FlatChunkGeneratorConfig config) {
        try { 
            // Everything ever is private
            Field executorField = MinecraftServer.class.getDeclaredField("workerExecutor");
            executorField.setAccessible(true);
            Executor workerExecutor = (Executor) executorField.get(server);

            Field sessionField = MinecraftServer.class.getDeclaredField("session");
            sessionField.setAccessible(true);
            LevelStorage.Session session = (LevelStorage.Session) sessionField.get(server);

            Field dataFixerField = MinecraftServer.class.getDeclaredField("dataFixer");
            dataFixerField.setAccessible(true);
            DataFixer dataFixer = (DataFixer) dataFixerField.get(server);

            Field structureTemplateField = MinecraftServer.class.getDeclaredField("structureTemplateManager");
            structureTemplateField.setAccessible(true);
            StructureTemplateManager structureTemplateManager = (StructureTemplateManager) structureTemplateField.get(server);

            ServerChunkManager oldChunkManager = dimension.getChunkManager();

            Field chunkStatusChangeListenerField = ThreadedAnvilChunkStorage.class.getDeclaredField("chunkStatusChangeListener");
            chunkStatusChangeListenerField.setAccessible(true);
            ChunkStatusChangeListener chunkStatusChangeListener = (ChunkStatusChangeListener) chunkStatusChangeListenerField.get(oldChunkManager.threadedAnvilChunkStorage);

            var oldGenerator = (NoiseChunkGenerator)oldChunkManager.getChunkGenerator();

            ChunkGenerator flatWorldGen = dimension.getRegistryKey() == World.END ? 
                new FlatEndChunkGenerator(config) :
                new FlatChunkGenerator(config);

            // Replace chunk manager to change how it generates
            Field chunkManagerField = ServerWorld.class.getDeclaredField("chunkManager");
            chunkManagerField.setAccessible(true);

            Supplier<PersistentStateManager> persistentStateFactory = () -> dimension.getPersistentStateManager();

            var newChunkManager = new ServerChunkManager(
                dimension, 
                session, 
                dataFixer, 
                structureTemplateManager, 
                workerExecutor, 
                flatWorldGen, 
                12, 
                12, 
                false, 
                listener, 
                chunkStatusChangeListener, 
                persistentStateFactory);

            chunkManagerField.set(dimension, newChunkManager);

            return flatWorldGen;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
