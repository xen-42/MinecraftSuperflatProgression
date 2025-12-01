package xen42.superflatprogression.rei;

import java.util.List;
import java.util.Optional;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.registry.RecipeManagerContext;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.recipe.GrinderRecipe;


public class PulverizerREIDisplay extends BasicDisplay implements SimpleGridMenuDisplay {

	public static final BasicDisplay.Serializer<PulverizerREIDisplay> SERIALIZER = BasicDisplay.Serializer.<PulverizerREIDisplay>of(
        (input, output, location, tag) -> {
			boolean needsBucket = tag.getBoolean("needsBucket");
            int outputAmount = tag.getInt("outputAmount");
			return PulverizerREIDisplay.simple(input, output, needsBucket, outputAmount, location);
		}, 
        (display, tag) -> {
			tag.putBoolean("needsBucket", display.needsBucket);
			tag.putInt("outputAmount", display.outputAmount);
		});

	public static PulverizerREIDisplay simple(List<EntryIngredient> input, List<EntryIngredient> output, boolean needsBucket, int outputAmount, Optional<Identifier> location) {
		Optional<Recipe<?>> optionalRecipe = location.flatMap(resourceLocation -> RecipeManagerContext.getInstance().getRecipeManager().get(resourceLocation));
		if (optionalRecipe.isPresent()) {
			return new PulverizerREIDisplay((GrinderRecipe)optionalRecipe.get());
		}
		else {
			return new PulverizerREIDisplay(input, output, needsBucket, outputAmount);
		}
	}

    public boolean needsBucket;
    public int outputAmount;
    public PulverizerREIDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, boolean needsBucket, int outputAmount) {
        super(inputs, outputs);
        this.needsBucket = needsBucket;
        this.outputAmount = outputAmount;
    }

    public PulverizerREIDisplay(GrinderRecipe recipe) {
		this(List.of(EntryIngredients.ofIngredient(recipe.input)), List.of(EntryIngredients.of(recipe.result)), recipe.needsBucket, recipe.count);
	}

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SuperflatProgressionREIServerPlugin.PULVERIZER_CATEGORY;
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
