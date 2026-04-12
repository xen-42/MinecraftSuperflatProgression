package xen42.superflatprogression;

import java.util.function.Predicate;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.SpawnRestriction.Location;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionOptions;
import xen42.superflatprogression.entities.PixieEntity;

public class SuperflatProgressionEntities {
	public static final Identifier PIXIE_ENTITY_ID = Identifier.of(SuperflatProgression.MOD_ID, "pixie");
	public static final RegistryKey<EntityType<?>> PIXIE_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, PIXIE_ENTITY_ID);
	public static final EntityType<PixieEntity> PIXIE_ENTITY = Registry.register(
		Registries.ENTITY_TYPE, 
		PIXIE_ENTITY_ID, 
		EntityType.Builder.create(PixieEntity::new, SpawnGroup.AMBIENT).setDimensions(0.5f, 0.5f).build(PIXIE_ENTITY_KEY.toString()));

    public static void initialize() {
		FabricDefaultAttributeRegistry.register(SuperflatProgressionEntities.PIXIE_ENTITY, PixieEntity.createPixieAttributes());
		addBiomeSpawn(BiomeSelectors.foundInOverworld(), SuperflatProgressionEntities.PIXIE_ENTITY, 150, 1, 2);
		addSpawnRestriction(SuperflatProgressionEntities.PIXIE_ENTITY, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PixieEntity::isValidSpawn);

		var plains = BiomeSelectors.vanilla().and(context -> {
			var overworld = context.canGenerateIn(DimensionOptions.OVERWORLD);
			var isPlains = SuperflatProgressionUtils.getPath(context.getBiomeKey()).equals("plains");
			return overworld && isPlains;
		});
		addBiomeSpawn(plains, EntityType.SQUID, 2, 1, 4);
		addBiomeSpawn(plains, EntityType.COD, 5, 3, 6);
		addBiomeSpawn(plains, EntityType.SALMON, 5, 1, 5);
		addBiomeSpawn(plains, EntityType.DROWNED, 1, 1, 1);
	}
	
	public static void addBiomeSpawn(
		Predicate<BiomeSelectionContext> biomeSelector, EntityType<?> entity, int weight, int minGroupCount, int maxGroupCount
	) {
		BiomeModifications.addSpawn(biomeSelector, entity.getSpawnGroup(), entity, weight, minGroupCount, maxGroupCount);
	}

	public static <T extends MobEntity> void addSpawnRestriction(
		EntityType<T> type, SpawnRestriction.Location location, Heightmap.Type heightmapType, SpawnRestriction.SpawnPredicate<T> predicate
	) {
		SpawnRestriction.register(type, location, heightmapType, predicate);
	}
}
