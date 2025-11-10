package xen42.superflatprogression.rei;

import java.util.List;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import xen42.superflatprogression.SuperflatProgressionBlocks;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplayMerger;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;

public class ScriptoriumCategory implements DisplayCategory<ScriptoriumREIDisplay> {

    @Override
    public CategoryIdentifier<? extends ScriptoriumREIDisplay> getCategoryIdentifier() {
        return SuperflatProgressionREIServerPlugin.SCRIPTORIUM_CATEGORY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(SuperflatProgressionBlocks.SCROLL_CRAFTING.getTranslationKey());
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(SuperflatProgressionBlocks.SCROLL_CRAFTING);
    }

    @Override
	public List<Widget> setupDisplay(ScriptoriumREIDisplay display, Rectangle bounds) {
		Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - (display.cost > 0 ? 34 : 27));
		List<Widget> widgets = Lists.newArrayList();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 18)));
		widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95, startPoint.y + 19)));
		var input = display.getInputEntries();
		List<Slot> slots = Lists.newArrayList();

        // Main ingredient
        var mainIngredient = Widgets.createSlot(new Point(startPoint.x + 36, startPoint.y + 19)).markInput();
        mainIngredient.entries(input.get(0));
        slots.add(mainIngredient);

        var parchment = Widgets.createSlot(new Point(startPoint.x, startPoint.y + 8)).markInput();
        parchment.entries(input.get(1));
        slots.add(parchment); 

        var pixieDust = Widgets.createSlot(new Point(startPoint.x, startPoint.y + 30)).markInput();
        pixieDust.entries(input.get(2));
        slots.add(pixieDust); 

		widgets.addAll(slots);
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
		if (display.cost > 0) {
			widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
				TextRenderer font = MinecraftClient.getInstance().textRenderer;
				Text component = Text.translatable("container.repair.cost", display.cost);
				int endX = startPoint.x + 102 + 26;
				int x = endX - font.getWidth(component) - 2;
				graphics.fill(x - 2, startPoint.y + 46, endX, startPoint.y + 46 + 12, 0x4f000000);
				graphics.drawTextWithShadow(font, component, x, startPoint.y + 46 + 2, 0x80ff20);
			}));
		}
		return widgets;
	}
    
}
