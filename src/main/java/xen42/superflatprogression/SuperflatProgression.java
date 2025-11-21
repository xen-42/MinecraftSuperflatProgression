package xen42.superflatprogression;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnRestriction.Location;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import xen42.superflatprogression.entities.PixieEntity;
import xen42.superflatprogression.recipe.GrinderRecipe;
import xen42.superflatprogression.recipe.ScrollCraftingRecipe;
import xen42.superflatprogression.screen.GrinderScreenHandler;
import xen42.superflatprogression.screen.ScrollCraftingScreenHandler;
import xen42.superflatprogression.worldgen.FlatEndChunkGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperflatProgression implements ModInitializer {
	public static final String MOD_ID = "superflat-progression";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DefaultParticleType MAGIC_TORCH_PARTICLE = registerParticleType("magic_torch_flame");
	public static final DefaultParticleType PIXIE_PARTICLE = registerParticleType("pixie_particle");

	private static DefaultParticleType registerParticleType(String name) {
		var particle = FabricParticleTypes.simple();
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, name), particle);
		return particle;
	}

	public static final RegistryKey<RecipeType<?>> SCROLL_CRAFTING_RECIPE_TYPE_KEY = RegistryKey.of(RegistryKeys.RECIPE_TYPE, Identifier.of(MOD_ID, "scroll_crafting"));
	public static final RecipeType<ScrollCraftingRecipe> SCROLL_CRAFTING_RECIPE_TYPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(MOD_ID, "scroll_crafting"), new RecipeType<ScrollCraftingRecipe>() {
		public String toString() {
			return "scroll_crafting";
		}
	});
	public static final RecipeSerializer<ScrollCraftingRecipe> SCROLL_CRAFTING_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(MOD_ID, "scroll_crafting"), new ScrollCraftingRecipe.Serializer());
	public static final ScreenHandlerType<ScrollCraftingScreenHandler> SCROLL_CRAFTING_SCREEN_HANDLER = Registry.register(
		Registries.SCREEN_HANDLER,
		Identifier.of(MOD_ID, "scroll_crafting"),
		new ScreenHandlerType<ScrollCraftingScreenHandler>(ScrollCraftingScreenHandler::new, null));

	public static final RegistryKey<RecipeType<?>> GRINDER_RECIPE_TYPE_KEY = RegistryKey.of(RegistryKeys.RECIPE_TYPE, Identifier.of(MOD_ID, "grinder"));
	public static final RecipeType<GrinderRecipe> GRINDER_RECIPE_TYPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(MOD_ID, "grinder"), new RecipeType<GrinderRecipe>() {
		public String toString() {
			return "grinder";
		}
	});
	public static final RecipeSerializer<GrinderRecipe> GRINDER_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(MOD_ID, "grinder"), new GrinderRecipe.Serializer());
	public static final ScreenHandlerType<GrinderScreenHandler> GRINDER_SCREEN_HANDLER = Registry.register(
		Registries.SCREEN_HANDLER,
		Identifier.of(MOD_ID, "grinder"),
		new ScreenHandlerType<GrinderScreenHandler>(GrinderScreenHandler::new, null));

	public static final RegistryKey<EntityType<?>> PIXIE_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID,"pixie"));
	public static final EntityType<PixieEntity> PIXIE_ENTITY = Registry.register(
		Registries.ENTITY_TYPE, 
		Identifier.of(MOD_ID, "pixie"), 
		EntityType.Builder.create(PixieEntity::new, SpawnGroup.AMBIENT).setDimensions(0.5f, 0.5f).build(PIXIE_ENTITY_KEY.toString()));

	public static final TrackedData<Boolean> ENDERMAN_CANNOT_DROP = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		SuperflatProgressionStatusEffects.initialize();
		SuperflatProgressionItems.initialize();
		SuperflatProgressionBlocks.initialize();
		SuperflatProgressionPotions.initialize();
		SuperflatProgressionVillagers.initialize();

		FabricDefaultAttributeRegistry.register(PIXIE_ENTITY, PixieEntity.createPixieAttributes());

		BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.AMBIENT, PIXIE_ENTITY, 150, 1, 2);
		SpawnRestriction.register(PIXIE_ENTITY, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PixieEntity::isValidSpawn);

        var waterPotionNbt = new NbtCompound();
        waterPotionNbt.putString("Potion","minecraft:water");

		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (id.equals(Identifier.of("minecraft", "entities/witch"))) {
				tableBuilder.pool(LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1f))
					.with(
					ItemEntry.builder(Items.POTION)
						.apply(SetNbtLootFunction.builder(waterPotionNbt))
						.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
					).build()
				);
			}
			else if (LootTables.FISHING_FISH_GAMEPLAY.equals(id)) {
				tableBuilder.modifyPools(pool -> {
					pool.with(ItemEntry.builder(Items.BAMBOO).weight(10));
				});
			}
		});
	}
}