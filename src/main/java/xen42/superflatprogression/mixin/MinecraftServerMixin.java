package xen42.superflatprogression.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.DataFixer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import xen42.superflatprogression.CustomSpawner;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.worldgen.FlatEndChunkGenerator;
import xen42.superflatprogression.worldgen.FlatNetherChunkGenerator;

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

            var structuresEnabled = server.getSaveProperties().getGeneratorOptions().shouldGenerateStructures();

            var netherConfig = new FlatChunkGeneratorConfig(
                Optional.empty(),
                server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(BiomeKeys.NETHER_WASTES).get(),
                List.of()
            );
            netherConfig.getLayerBlocks().add(Blocks.BEDROCK.getDefaultState());
            for (int i = 0; i < 3; i ++) {
                netherConfig.getLayerBlocks().add(Blocks.NETHERRACK.getDefaultState());
            }
            MakeWorldSuperflat(server, listener, nether, netherConfig);

            // If no structures make blazes and wither skeletons just spawn
            List<Spawner> original = nether.spawners;
            var spawners = new ArrayList<Spawner>();
            spawners.addAll(original);
            if (!structuresEnabled) {
                spawners.add(new CustomSpawner(EntityType.BLAZE).markIsHostile().markRequiresDark()); 
                spawners.add(new CustomSpawner(EntityType.WITHER_SKELETON).markIsHostile().markRequiresDark()); 
                spawners.add(new CustomSpawner(EntityType.PIGLIN_BRUTE).markIsHostile().setMaxCount(1).markRequiresDark()); 
            }

            if (FabricLoader.getInstance().isModLoaded("peaceful-items")) {
                if (!structuresEnabled) {
                    spawners.add(new CustomSpawner(Registries.ENTITY_TYPE.get(Identifier.of("peaceful-items", "ghastling"))));
                }
                spawners.add(new CustomSpawner(Registries.ENTITY_TYPE.get(Identifier.of("peaceful-items", "end_clam"))));
            }

            nether.spawners = spawners;

            var endCities = server.getRegistryManager().get(RegistryKeys.STRUCTURE_SET).getEntry(StructureSetKeys.END_CITIES).get();
            var endConfig = new FlatChunkGeneratorConfig(
                structuresEnabled ? Optional.of(RegistryEntryList.of(endCities)) : Optional.empty(),
                server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(BiomeKeys.END_MIDLANDS).get(),
                List.of()
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

            var endSpawners = new ArrayList<Spawner>();
            endSpawners.addAll(end.spawners);
            if (!structuresEnabled) {
                // Shulkers can spawn in peaceful and just don't attack
                endSpawners.add(new CustomSpawner(EntityType.SHULKER).markRequiresDark().setMaxCount(1).disableDuringDragonFight()); 
            }
            end.spawners = endSpawners;

            MakeWorldSuperflat(server, listener, end, endConfig);       
        } catch (Exception e) {
            SuperflatProgression.LOGGER.error("Failed to make worlds superflat", e);
        }
    }

    private ChunkGenerator MakeWorldSuperflat(MinecraftServer server, WorldGenerationProgressListener listener, ServerWorld dimension, FlatChunkGeneratorConfig config) {
        try { 
            Executor workerExecutor = server.workerExecutor;
            LevelStorage.Session session = server.session;
            DataFixer dataFixer = server.dataFixer;
            StructureTemplateManager structureTemplateManager = server.structureTemplateManager;
            ServerChunkManager oldChunkManager = dimension.getChunkManager();
            ChunkStatusChangeListener chunkStatusChangeListener = oldChunkManager.threadedAnvilChunkStorage.chunkStatusChangeListener;

            ChunkGenerator flatWorldGen = new FlatChunkGenerator(config);
            if (dimension.getRegistryKey() == World.END) {
                flatWorldGen = new FlatEndChunkGenerator(config);
            }
            else if (dimension.getRegistryKey() == World.NETHER) {
                flatWorldGen = new FlatNetherChunkGenerator(config);
            }

            Supplier<PersistentStateManager> persistentStateFactory = () -> dimension.getPersistentStateManager();

            dimension.chunkManager = new ServerChunkManager(
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

            return flatWorldGen;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
