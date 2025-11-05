package xen42.superflatprogression;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.recipe.ScrollCraftingRecipe;
import xen42.superflatprogression.screen.ScrollCraftingScreenHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperflatProgression implements ModInitializer {
	public static final String MOD_ID = "superflat-progression";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DefaultParticleType MAGIC_TORCH_PARTICLE = registerParticleType("magic_torch_flame");

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
	}
}