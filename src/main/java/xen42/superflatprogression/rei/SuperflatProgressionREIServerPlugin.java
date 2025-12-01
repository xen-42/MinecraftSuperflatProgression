package xen42.superflatprogression.rei;

import net.minecraft.util.Identifier;
import xen42.superflatprogression.SuperflatProgression;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class SuperflatProgressionREIServerPlugin implements REIServerPlugin {
	public static final Identifier SCRIPTORIUM = Identifier.of(SuperflatProgression.MOD_ID, "plugins/scriptorium");
	public static final CategoryIdentifier<ScriptoriumREIDisplay> SCRIPTORIUM_CATEGORY = CategoryIdentifier.of(SuperflatProgression.MOD_ID, "plugins/scriptorium");

    public static final Identifier PULVERIZER = Identifier.of(SuperflatProgression.MOD_ID, "plugins/pulverizer");
	public static final CategoryIdentifier<PulverizerREIDisplay> PULVERIZER_CATEGORY = CategoryIdentifier.of(SuperflatProgression.MOD_ID, "plugins/pulverizer");

	public static final Identifier WATER_BOTTLE = Identifier.of(SuperflatProgression.MOD_ID, "plugins/water_bottle");
	public static final CategoryIdentifier<WaterBottleREIDisplay> WATER_BOTTLE_CATEGORY = CategoryIdentifier.of(SuperflatProgression.MOD_ID, "plugins/water_bottle");

	public static final Identifier ENRICHED_BONE_MEAL = Identifier.of(SuperflatProgression.MOD_ID, "plugins/enriched_bone_meal");
	public static final CategoryIdentifier<EnrichedBoneMealREIDisplay> ENRICHED_BONE_MEAL_CATEGORY = CategoryIdentifier.of(SuperflatProgression.MOD_ID, "plugins/enriched_bone_meal");

	public SuperflatProgressionREIServerPlugin() {
		SuperflatProgression.LOGGER.info("Creating REI server plugin");
	}
	
	@Override
	public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
		SuperflatProgression.LOGGER.info("Registering display serializers");

		registry.register(SCRIPTORIUM_CATEGORY, ScriptoriumREIDisplay.SERIALIZER);
		registry.register(PULVERIZER_CATEGORY, PulverizerREIDisplay.SERIALIZER);
	}
}