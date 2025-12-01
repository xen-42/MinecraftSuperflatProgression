package xen42.superflatprogression.rei;

import java.util.List;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;

public class EnrichedBoneMealREIDisplay extends BasicDisplay implements SimpleGridMenuDisplay {

	public static final BasicDisplay.Serializer<EnrichedBoneMealREIDisplay> SERIALIZER = BasicDisplay.Serializer.<EnrichedBoneMealREIDisplay>of(
        (input, output, location, tag) -> {
			return EnrichedBoneMealREIDisplay.simple(input, output);
		}, 
        (display, tag) -> {

		});

	public static EnrichedBoneMealREIDisplay simple(List<EntryIngredient> input, List<EntryIngredient> output) {
		return new EnrichedBoneMealREIDisplay(input, output);
	}

    public EnrichedBoneMealREIDisplay(List<EntryIngredient> input, List<EntryIngredient> output) {
        super(input, output);
    }

    public EnrichedBoneMealREIDisplay(Block input, Block output) {
        super(List.of(EntryIngredient.of(EntryStacks.of(input))), List.of(EntryIngredient.of(EntryStacks.of(output))));
    }

    public EnrichedBoneMealREIDisplay(TagKey<Item> input, TagKey<Item> output) {
        super(List.of(EntryIngredients.ofIngredient(Ingredient.fromTag(input))), List.of(EntryIngredients.ofIngredient(Ingredient.fromTag(output))));
    }

    public EnrichedBoneMealREIDisplay(Item input, TagKey<Item> output) {
        super(List.of(EntryIngredient.of(EntryStacks.of(input))), List.of(EntryIngredients.ofIngredient(Ingredient.fromTag(output))));
    }

    public EnrichedBoneMealREIDisplay(Block input, TagKey<Item> output) {
        super(List.of(EntryIngredient.of(EntryStacks.of(input))), List.of(EntryIngredients.ofIngredient(Ingredient.fromTag(output))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SuperflatProgressionREIServerPlugin.ENRICHED_BONE_MEAL_CATEGORY;
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
