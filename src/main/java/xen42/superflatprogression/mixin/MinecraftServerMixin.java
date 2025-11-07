package xen42.superflatprogression.mixin;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.DataFixer;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
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

            if (overworld == null || nether == null) return;

            // Only superflat the Nether if the Overworld is superflat
            if (!(overworld.getChunkManager().getChunkGenerator() instanceof FlatChunkGenerator overworldGen)) return;

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

            ServerChunkManager oldChunkManager = nether.getChunkManager();

            Field chunkStatusChangeListenerField = ThreadedAnvilChunkStorage.class.getDeclaredField("chunkStatusChangeListener");
            chunkStatusChangeListenerField.setAccessible(true);
            ChunkStatusChangeListener chunkStatusChangeListener = (ChunkStatusChangeListener) chunkStatusChangeListenerField.get(oldChunkManager.threadedAnvilChunkStorage);

            // For now use the overworld flat config but really this should be netherrack and stuff
            FlatChunkGeneratorConfig flatConfig = overworldGen.getConfig();
            ChunkGenerator flatNetherGen = new FlatChunkGenerator(flatConfig);

            // Replace chunk manager to change how it generates
            Field chunkManagerField = ServerWorld.class.getDeclaredField("chunkManager");
            chunkManagerField.setAccessible(true);

            Supplier<PersistentStateManager> persistentStateFactory = () -> nether.getPersistentStateManager();

            var newCM = new ServerChunkManager(
                nether, 
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

            chunkManagerField.set(nether, newCM);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
