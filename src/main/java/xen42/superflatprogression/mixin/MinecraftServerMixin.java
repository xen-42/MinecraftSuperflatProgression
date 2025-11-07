package xen42.superflatprogression.mixin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.DataFixer;

import net.minecraft.block.Blocks;
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
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.structure.StructureKeys;
import net.minecraft.world.level.storage.LevelStorage;
import xen42.superflatprogression.SuperflatProgression;

@Mixin(MinecraftServer.class)

public class MinecraftServerMixin {
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

            /* Unfortunately this didn't work and end cities never spawn
            var endCities = server.getRegistryManager().get(RegistryKeys.STRUCTURE_SET).getEntry(StructureSetKeys.END_CITIES).get();
            var endConfig = new FlatChunkGeneratorConfig(
                Optional.of(RegistryEntryList.of(endCities)),
                server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(BiomeKeys.END_HIGHLANDS).get(),
                List.of()
            );
            */

            /* 
            var endConfig = new FlatChunkGeneratorConfig(
                Optional.empty(),
                server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(BiomeKeys.END_HIGHLANDS).get(),
                List.of()
            );
            endConfig.getLayerBlocks().add(Blocks.BEDROCK.getDefaultState());
            endConfig.getLayerBlocks().add(Blocks.END_STONE.getDefaultState());
            endConfig.getLayerBlocks().add(Blocks.END_STONE.getDefaultState());
            endConfig.getLayerBlocks().add(Blocks.END_STONE.getDefaultState());
            MakeWorldSuperflat(server, listener, end, endConfig);
            */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MakeWorldSuperflat(MinecraftServer server, WorldGenerationProgressListener listener, ServerWorld dimension, FlatChunkGeneratorConfig config) {
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

            ChunkGenerator flatNetherGen = new FlatChunkGenerator(config);

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
                flatNetherGen, 
                12, 
                12, 
                false, 
                listener, 
                chunkStatusChangeListener, 
                persistentStateFactory);

            chunkManagerField.set(dimension, newChunkManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
