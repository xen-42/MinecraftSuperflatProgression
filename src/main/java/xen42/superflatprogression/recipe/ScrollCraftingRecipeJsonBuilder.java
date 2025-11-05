package xen42.superflatprogression.recipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.Map.Entry;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.SuperflatProgression;

public class ScrollCraftingRecipeJsonBuilder extends RecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private static final int MAX_WIDTH_AND_HEIGHT = 3;
	private static final int MAX_WIDTH_END = 1;
	public static final String SPACE = " ";
	Identifier ROOT = Identifier.of("minecraft","recipes/root");
	private final Item output;
	private final int count;
	private final Ingredient input;
	private final Map<String, CriterionConditions> criteria = new LinkedHashMap<String, CriterionConditions>();
	private OptionalInt cost = OptionalInt.empty();
	@Nullable
	private String group;

	private final RegistryEntryLookup<Item> registryLookup;

	private ScrollCraftingRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, ItemConvertible output, Ingredient input, int count) {
		this.registryLookup = registryLookup;
		this.output = output.asItem();
        this.input = input;
		this.count = count;
	}

	@Override
	public ScrollCraftingRecipeJsonBuilder criterion(String name, CriterionConditions conditions) {
		this.criteria.put(name, conditions);
		return this;
	}

	public ScrollCraftingRecipeJsonBuilder cost(int cost) {
		if (cost > 0) {
			this.cost = OptionalInt.of(cost);
		} else {
			this.cost = OptionalInt.empty();
		}
		return this;
	}

	public ScrollCraftingRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	public Item getOutputItem() {
		return this.output;
	}

	@Override
	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		ScrollCraftingRecipe.RawRecipe rawRecipe = this.validate(recipeId);
		Advancement.Builder builder = Advancement.Builder.createUntelemetered()
			.parent(ROOT)
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);
		this.criteria.forEach(builder::criterion);
		ScrollCraftingRecipe recipe = new ScrollCraftingRecipe(
			recipeId,
			(String)Objects.requireNonNullElse(this.group, ""),
			rawRecipe,
			new ItemStack(this.output, this.count),
			this.cost
		);
		exporter.accept(new JsonProvider(
				recipeId,
				this.output,
				this.count,
				this.group == null ? "" : this.group,
                this.input,
				this.cost,
				recipeId.withPrefixedPath("recipes/effigy_altar/"),
				builder));
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, RegistryKey<Recipe<?>> recipeKey) {
		offerTo(exporter, recipeKey.getValue());
	}

	private ScrollCraftingRecipe.RawRecipe validate(Identifier recipeId) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		} else {
			return ScrollCraftingRecipe.RawRecipe.create(this.input);
		}
	}
	
	public void offerTo(Consumer<RecipeJsonProvider> exporter) {
		this.offerTo(exporter, getItemId(this.getOutputItem()));
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipePath) {
		Identifier identifier = getItemId(this.getOutputItem());
		Identifier identifier2 = Identifier.of(SuperflatProgression.MOD_ID, recipePath);
		if (identifier2.equals(identifier)) {
			throw new IllegalStateException("Recipe " + recipePath + " should remove its 'save' argument as it is equal to default one");
		} else {
			this.offerTo(exporter, identifier2);
		}
	}

	public static Identifier getItemId(ItemConvertible item) {
		return Identifier.of(SuperflatProgression.MOD_ID, Registries.ITEM.getId(item.asItem()).getPath());
	}

	private static class JsonProvider implements RecipeJsonProvider {
		private final Identifier id;
		private final Item output;
		private final int resultCount;
		private final String group;
		private final Ingredient input;
		private final OptionalInt cost;
		private final Identifier advancementId;
		private final Advancement.Builder advancementBuilder;

		protected JsonProvider(
				Identifier id,
				Item output,
				int resultCount,
				String group,
				Ingredient input,
				OptionalInt cost,
				Identifier advancementId,
				Advancement.Builder advancementBuilder) {
			this.id = id;
			this.output = output;
			this.resultCount = resultCount;
			this.group = group;
			this.input = input;
			this.cost = cost;
			this.advancementId = advancementId;
			this.advancementBuilder = advancementBuilder;
		}

		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}
			if (!this.cost.isEmpty()) {
				json.addProperty("cost", this.cost.getAsInt());
			}

            JsonObject inputJson = new JsonObject();
			inputJson.addProperty("item", input.toJson().toString());
			json.add("input", inputJson);

			JsonObject result = new JsonObject();
			result.addProperty("item", Registries.ITEM.getId(this.output).toString());
			if (this.resultCount > 1) {
				result.addProperty("count", this.resultCount);
			}
			json.add("result", result);
		}

		@Override
		public Identifier getRecipeId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return SuperflatProgression.SCROLL_CRAFTING_RECIPE_SERIALIZER;
		}

		@Override
		public Identifier getAdvancementId() {
			return advancementId;
		}

		@Override
		public JsonObject toAdvancementJson() {
			return advancementBuilder.toJson();
		}
	}
}