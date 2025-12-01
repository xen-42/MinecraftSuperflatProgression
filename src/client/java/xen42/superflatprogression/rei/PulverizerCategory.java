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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import xen42.superflatprogression.SuperflatProgressionBlocks;

public class PulverizerCategory implements DisplayCategory<PulverizerREIDisplay> {

	@Override
	public CategoryIdentifier<? extends PulverizerREIDisplay> getCategoryIdentifier() {
		return SuperflatProgressionREIServerPlugin.PULVERIZER_CATEGORY;
	}

	@Override
	public Text getTitle() {
		return Text.translatable(SuperflatProgressionBlocks.GRINDER.getTranslationKey());
	}

	@Override
	public Renderer getIcon() {
		return EntryStacks.of(SuperflatProgressionBlocks.GRINDER);
	}

	@Override
	public List<Widget> setupDisplay(PulverizerREIDisplay display, Rectangle bounds) {
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

		if (display.needsBucket) {
			var bucket = Widgets.createSlot(new Point(startPoint.x, startPoint.y + 19)).markInput();
			bucket.entries(List.of(EntryStacks.of(Items.BUCKET)));
			slots.add(bucket);
		}

		widgets.addAll(slots);
		var outputSlotPoint = new Point(startPoint.x + 95 - 16, startPoint.y + 19);

		widgets.add(Widgets.createSlot(outputSlotPoint).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
		if (display.outputAmount > 1) {
			// Regular label renders underneath the item womp womp
			// widgets.add(Widgets.createLabel(new Point(outputSlotPoint.x + 14, outputSlotPoint.y + 9), Text.of(Integer.toString(display.outputAmount))));
			widgets.add(Widgets.createDrawableWidget((drawContext, mouseX, mouseY, delta) -> {
				drawContext.getMatrices().push();
				drawContext.getMatrices().translate(0, 0, 200);
				drawContext.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of(Integer.toString(display.outputAmount)),
					outputSlotPoint.x + 11, outputSlotPoint.y + 9, 0xFFFFFF);
				drawContext.getMatrices().pop();
			}));
		}
		
		return widgets;
	}
}
