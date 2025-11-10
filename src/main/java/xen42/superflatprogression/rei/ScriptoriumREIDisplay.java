package xen42.superflatprogression.rei;

import java.util.List;
import java.util.Optional;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.registry.RecipeManagerContext;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.SuperflatProgressionItems;
import xen42.superflatprogression.SuperflatProgressionTags;
import xen42.superflatprogression.recipe.ScrollCraftingRecipe;

public class ScriptoriumREIDisplay extends BasicDisplay implements SimpleGridMenuDisplay {

	public static final BasicDisplay.Serializer<ScriptoriumREIDisplay> SERIALIZER = BasicDisplay.Serializer.<ScriptoriumREIDisplay>of(
        (input, output, location, tag) -> {
			int cost = tag.getInt("cost");
			return ScriptoriumREIDisplay.simple(input, output, cost, location);
		}, 
        (display, tag) -> {
			tag.putInt("cost", display.cost);
		});

	public static ScriptoriumREIDisplay simple(List<EntryIngredient> input, List<EntryIngredient> output, int cost, Optional<Identifier> location) {
		Optional<Recipe<?>> optionalRecipe = location.flatMap(resourceLocation -> RecipeManagerContext.getInstance().getRecipeManager().get(resourceLocation));
		if (optionalRecipe.isPresent()) {
			return new ScriptoriumREIDisplay((ScrollCraftingRecipe)optionalRecipe.get());
		}
		else {
			return new ScriptoriumREIDisplay(input, output, cost);
		}
	}

    public int cost;
    public ScriptoriumREIDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, int cost) {
        super(inputs, outputs);
        this.cost = cost;
    }

    public ScriptoriumREIDisplay(ScrollCraftingRecipe recipe) {
		this(List.of(EntryIngredients.ofIngredient(recipe.input), 
            EntryIngredients.ofIngredient(Ingredient.fromTag(SuperflatProgressionTags.ItemTags.PARCHMENTS)),
            EntryIngredients.of(SuperflatProgressionItems.ESSENCE)), List.of(EntryIngredients.of(recipe.result)), recipe.cost);
	}

    public List<EntryIngredient> getInputs() {
        return this.inputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SuperflatProgressionREIServerPlugin.SCRIPTORIUM_CATEGORY;
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 1;
    }
    
}
