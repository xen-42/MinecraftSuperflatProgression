package xen42.superflatprogression.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionBlocks;
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

		SuperflatProgression.LOGGER.info("Registering workstations");
		
		registry.addWorkstations(SuperflatProgressionREIServerPlugin.SCRIPTORIUM_CATEGORY, EntryStacks.of(SuperflatProgressionBlocks.SCROLL_CRAFTING));
		registry.addWorkstations(SuperflatProgressionREIServerPlugin.PULVERIZER_CATEGORY, EntryStacks.of(SuperflatProgressionBlocks.GRINDER));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		SuperflatProgression.LOGGER.info("Registering displays");

		registry.registerRecipeFiller(ScrollCraftingRecipe.class, SuperflatProgression.SCROLL_CRAFTING_RECIPE_TYPE, ScriptoriumREIDisplay::new);
		registry.registerRecipeFiller(GrinderRecipe.class, SuperflatProgression.GRINDER_RECIPE_TYPE, PulverizerREIDisplay::new);
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
		
		registry.register(new PulverizerTransferHandler());
		registry.register(new ScriptoriumTransferHandler());
	}
}
