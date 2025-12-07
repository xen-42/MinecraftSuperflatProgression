package xen42.superflatprogression.rei;

import java.util.List;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionBlocks;
import xen42.superflatprogression.SuperflatProgressionItems;
import xen42.superflatprogression.SuperflatProgressionTags;
import xen42.superflatprogression.recipe.GrinderRecipe;
import xen42.superflatprogression.recipe.ScrollCraftingRecipe;
import xen42.superflatprogression.screen.GrinderHandledScreen;
import xen42.superflatprogression.screen.ScrollCraftingHandledScreen;

public class SuperflatProgressionREIClientPlugin implements REIClientPlugin {
    public SuperflatProgressionREIClientPlugin() {
		SuperflatProgression.LOGGER.info("Creating REI client plugin");
	}
	
	@Override
	public void registerCategories(CategoryRegistry registry) {
		SuperflatProgression.LOGGER.info("Registering categories");
		
		registry.add(new PulverizerCategory());
		registry.add(new ScriptoriumCategory());
		registry.add(new WaterBottleCategory());
		registry.add(new EnrichedBoneMealCategory());

		SuperflatProgression.LOGGER.info("Registering workstations");
		
		registry.addWorkstations(SuperflatProgressionREIServerPlugin.SCRIPTORIUM_CATEGORY, EntryStacks.of(SuperflatProgressionBlocks.SCROLL_CRAFTING));
		registry.addWorkstations(SuperflatProgressionREIServerPlugin.PULVERIZER_CATEGORY, EntryStacks.of(SuperflatProgressionBlocks.GRINDER));
		registry.addWorkstations(SuperflatProgressionREIServerPlugin.WATER_BOTTLE_CATEGORY, EntryStacks.of(PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
		registry.addWorkstations(SuperflatProgressionREIServerPlugin.ENRICHED_BONE_MEAL_CATEGORY, EntryStacks.of(SuperflatProgressionItems.ENRICHED_BONEMEAL));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		SuperflatProgression.LOGGER.info("Registering displays");

		registry.registerRecipeFiller(ScrollCraftingRecipe.class, SuperflatProgression.SCROLL_CRAFTING_RECIPE_TYPE, ScriptoriumREIDisplay::new);
		registry.registerRecipeFiller(GrinderRecipe.class, SuperflatProgression.GRINDER_RECIPE_TYPE, PulverizerREIDisplay::new);

		registry.add(new WaterBottleREIDisplay(Blocks.DIRT, Blocks.MUD));
		registry.add(new WaterBottleREIDisplay(Blocks.MAGMA_BLOCK, Blocks.COBBLESTONE));

		registry.add(new EnrichedBoneMealREIDisplay(SuperflatProgressionTags.ItemTags.MUSHROOM_BLOCK_PLACEABLE, SuperflatProgressionTags.ItemTags.ENRICHED_BONE_MEAL_MUSHROOM));
		registry.add(new EnrichedBoneMealREIDisplay(Blocks.END_STONE, SuperflatProgressionTags.ItemTags.ENRICHED_BONE_MEAL_ON_END_STONE));
		registry.add(new EnrichedBoneMealREIDisplay(Blocks.SAND, SuperflatProgressionTags.ItemTags.ENRICHED_BONE_MEAL_ON_SAND));
		registry.add(new EnrichedBoneMealREIDisplay(Blocks.GRASS_BLOCK, SuperflatProgressionTags.ItemTags.ENRICHED_BONE_MEAL_ON_GRASS));
		registry.add(new EnrichedBoneMealREIDisplay(Blocks.SOUL_SAND, SuperflatProgressionTags.ItemTags.ENRICHED_BONE_MEAL_ON_SOUL_SAND));
		registry.add(new EnrichedBoneMealREIDisplay(Items.WATER_BUCKET, SuperflatProgressionTags.ItemTags.ENRICHED_BONE_MEAL_UNDER_WATER));

		addInfo(SuperflatProgressionBlocks.CHARCOAL_BLOCK.asItem());
		addInfo(SuperflatProgressionBlocks.GRINDER.asItem());
		addInfo(Items.MAGMA_CREAM);
		addInfo(Items.GOLD_INGOT);
		addInfo(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
		addInfo(SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR.asItem());
	}
	
	@Override
	public void registerScreens(ScreenRegistry registry) {
		SuperflatProgression.LOGGER.info("Registering screens");

		registry.registerContainerClickArea(new Rectangle(94, 30, 27, 21), ScrollCraftingHandledScreen.class, 
            SuperflatProgressionREIServerPlugin.SCRIPTORIUM_CATEGORY);
        registry.registerContainerClickArea(new Rectangle(78, 22, 20, 39), GrinderHandledScreen.class, 
            SuperflatProgressionREIServerPlugin.PULVERIZER_CATEGORY);
	}
	
	@Override
	public void registerTransferHandlers(TransferHandlerRegistry registry) {
		SuperflatProgression.LOGGER.info("Registering transfer handlers");
		
		// Todo: fix these
		//registry.register(new PulverizerTransferHandler());
		//registry.register(new ScriptoriumTransferHandler());
	}

	private static void addInfo(Item item) {
		var key = "info." + SuperflatProgression.MOD_ID + "." + item.getTranslationKey();
		BuiltinClientPlugin.getInstance().registerInformation(EntryStacks.of(item), 
			Text.of(key),
			(text) -> List.of(Text.translatable(key)));
	}
}
