package xen42.superflatprogression.recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

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

public class GrinderRecipeJsonBuilder extends RecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	public static final String SPACE = " ";
	Identifier ROOT = Identifier.of("minecraft","recipes/root");
	private final Item output;
	private final Ingredient input;
	private final boolean needsBucket;
	private int count = 1;
	private final Map<String, CriterionConditions> criteria = new LinkedHashMap<String, CriterionConditions>();
	@Nullable
	private String group;

	private Identifier uniqueID;

	private final RegistryEntryLookup<Item> registryLookup;

	public GrinderRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, Item input, ItemConvertible output, boolean needsBucket) {
		this.registryLookup = registryLookup;
		this.output = output.asItem();
        this.input = Ingredient.ofItems(input);
		this.uniqueID = getItemId(input);
		this.needsBucket = needsBucket;
	}

	public GrinderRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, TagKey<Item> input, ItemConvertible output, boolean needsBucket) {
		this.registryLookup = registryLookup;
		this.output = output.asItem();
        this.input = Ingredient.fromTag(input);
		this.uniqueID = getTagId(input);
		this.needsBucket = needsBucket;
	}

	public static Identifier getItemId(ItemConvertible item) {
		return Identifier.of(SuperflatProgression.MOD_ID, Registries.ITEM.getId(item.asItem()).getPath());
	}

	public static Identifier getTagId(TagKey<Item> itemTag) {
		return Identifier.of(SuperflatProgression.MOD_ID, itemTag.id().getPath());
	}

	public GrinderRecipeJsonBuilder setCount(int count) {
		this.count = count;
		return this;
	}

	@Override
	public GrinderRecipeJsonBuilder criterion(String name, CriterionConditions conditions) {
		this.criteria.put(name, conditions);
		return this;
	}

	public GrinderRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	public Item getOutputItem() {
		return this.output;
	}

	@Override
	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		validate(recipeId);
		Advancement.Builder builder = Advancement.Builder.createUntelemetered()
			.parent(ROOT)
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);
		this.criteria.forEach(builder::criterion);
		exporter.accept(new JsonProvider(
				recipeId.withPrefixedPath("grinder/"),
				this.output,
				this.needsBucket,
				this.group == null ? "" : this.group,
                this.input,
				this.count,
				recipeId.withPrefixedPath("recipes/grinder/"),
				builder));
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, RegistryKey<Recipe<?>> recipeKey) {
		offerTo(exporter, recipeKey.getValue());
	}

	private GrinderRecipe validate(Identifier recipeId) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		} else {
			return new GrinderRecipe(recipeId, this.group, this.input, new ItemStack(this.output, this.count), this.needsBucket, this.count);
		}
	}
	
	public void offerTo(Consumer<RecipeJsonProvider> exporter) {
		this.offerTo(exporter, this.uniqueID);
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipePath) {
		Identifier identifier = this.uniqueID;
		Identifier identifier2 = Identifier.of(SuperflatProgression.MOD_ID, recipePath);
		if (identifier2.equals(identifier)) {
			throw new IllegalStateException("Recipe " + recipePath + " should remove its 'save' argument as it is equal to default one");
		} else {
			this.offerTo(exporter, identifier2);
		}
	}

	private static class JsonProvider implements RecipeJsonProvider {
		private final Identifier id;
		private final Item output;
		private final boolean needsBucket;
		private final String group;
		private final Ingredient input;
		private final int count;
		private final Identifier advancementId;
		private final Advancement.Builder advancementBuilder;

		protected JsonProvider(
				Identifier id,
				Item output,
				boolean needsBucket, 
				String group,
				Ingredient input,
				int count,
				Identifier advancementId,
				Advancement.Builder advancementBuilder) {
			this.id = id;
			this.output = output;
			this.needsBucket = needsBucket;
			this.group = group;
			this.input = input;
			this.count = count;
			this.advancementId = advancementId;
			this.advancementBuilder = advancementBuilder;
		}

		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

            JsonObject inputJson = new JsonObject();
			inputJson.add("item", input.toJson());
			json.add("input", inputJson);

			JsonObject result = new JsonObject();
			result.addProperty("item", Registries.ITEM.getId(this.output).toString());
			json.add("result", result);

			json.addProperty("count", this.count);

			json.addProperty("needsBucket", this.needsBucket);
		}

		@Override
		public Identifier getRecipeId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return SuperflatProgression.GRINDER_RECIPE_SERIALIZER;
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