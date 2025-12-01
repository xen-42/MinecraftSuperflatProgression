package xen42.superflatprogression.rei;

import java.util.List;

import com.google.common.collect.Lists;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import xen42.superflatprogression.SuperflatProgressionItems;

public class EnrichedBoneMealCategory implements DisplayCategory<EnrichedBoneMealREIDisplay> {

	@Override
	public CategoryIdentifier<? extends EnrichedBoneMealREIDisplay> getCategoryIdentifier() {
		return SuperflatProgressionREIServerPlugin.ENRICHED_BONE_MEAL_CATEGORY;
	}

	@Override
	public Text getTitle() {
		return Text.translatable(SuperflatProgressionItems.ENRICHED_BONEMEAL.getTranslationKey());
	}

	@Override
	public Renderer getIcon() {
		return EntryStacks.of(SuperflatProgressionItems.ENRICHED_BONEMEAL);
	}

	@Override
	public List<Widget> setupDisplay(EnrichedBoneMealREIDisplay display, Rectangle bounds) {
		Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
		List<Widget> widgets = Lists.newArrayList();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createArrow(new Point(startPoint.x + 60 - 16, startPoint.y + 18)));
		widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95 - 16, startPoint.y + 19)));
		var input = display.getInputEntries();
		List<Slot> slots = Lists.newArrayList();

		// Main ingredient
		var mainIngredient = Widgets.createSlot(new Point(startPoint.x + 36 - 16, startPoint.y + 19)).markInput();
		mainIngredient.entries(input.get(0));
		slots.add(mainIngredient);

		widgets.addAll(slots);
		var outputSlotPoint = new Point(startPoint.x + 95 - 16, startPoint.y + 19);

		widgets.add(Widgets.createSlot(outputSlotPoint).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
		
		return widgets;
	}
}
