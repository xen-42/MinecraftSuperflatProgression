package xen42.superflatprogression.rei;

import java.util.List;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Block;

public class WaterBottleREIDisplay extends BasicDisplay implements SimpleGridMenuDisplay {

	public static final BasicDisplay.Serializer<WaterBottleREIDisplay> SERIALIZER = BasicDisplay.Serializer.<WaterBottleREIDisplay>of(
        (input, output, location, tag) -> {
			return WaterBottleREIDisplay.simple(input, output);
		}, 
        (display, tag) -> {

		});

	public static WaterBottleREIDisplay simple(List<EntryIngredient> input, List<EntryIngredient> output) {
		return new WaterBottleREIDisplay(input, output);
	}

    public WaterBottleREIDisplay(List<EntryIngredient> input, List<EntryIngredient> output) {
        super(input, output);
    }

    public WaterBottleREIDisplay(Block input, Block output) {
        super(List.of(EntryIngredient.of(EntryStacks.of(input))), List.of(EntryIngredient.of(EntryStacks.of(output))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SuperflatProgressionREIServerPlugin.WATER_BOTTLE_CATEGORY;
    }

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 1;
    }
}
