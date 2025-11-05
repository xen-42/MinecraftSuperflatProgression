package xen42.superflatprogression.recipe;

import org.jetbrains.annotations.VisibleForTesting;

import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionItems;
import xen42.superflatprogression.SuperflatProgressionTags;
import xen42.superflatprogression.screen.GrinderScreenHandler;

public class GrinderRecipe implements Recipe<GrinderRecipeInput> {
	final Identifier id;
	public final ItemStack result;
	public final ItemStack secondaryResult;
	public final Ingredient input;
	final String group;

	public GrinderRecipe(Identifier id, String group, Ingredient input, ItemStack result, ItemStack secondaryResult) {
		this.id = id;
		this.group = group;
		this.input = input;
		this.result = result;
		this.secondaryResult = secondaryResult;
	}

	@Override
	public RecipeType<GrinderRecipe> getType() {
		return SuperflatProgression.GRINDER_RECIPE_TYPE;
	}

	@Override
	public RecipeSerializer<? extends GrinderRecipe> getSerializer() {
		return SuperflatProgression.GRINDER_RECIPE_SERIALIZER;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return false;
	}

	@Override
	public boolean showNotification() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		DefaultedList<Ingredient> defaultedList = this.getIngredients();
		return defaultedList.isEmpty()
			|| defaultedList.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(ingredient -> ingredient.getMatchingStacks().length == 0);
	}

	public DefaultedList<ItemStack> getRecipeRemainders(GrinderRecipeInput input) {
		return collectRecipeRemainders(input);
	}

	public static DefaultedList<ItemStack> collectRecipeRemainders(GrinderRecipeInput input) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			defaultedList.set(i, input.getStackInSlot(i));
		}

		return defaultedList;
	}

	@Override
	@VisibleForTesting
	public DefaultedList<Ingredient> getIngredients() {
		return DefaultedList.copyOf(Ingredient.EMPTY, this.input);
	}

	public boolean matches(GrinderRecipeInput input, World world) {
		return input.getStackInSlot(GrinderScreenHandler.PARCHMENT_SLOT).isIn(SuperflatProgressionTags.ItemTags.PARCHMENTS) &&
			input.getStackInSlot(GrinderScreenHandler.ESSENCE_SLOT).isOf(SuperflatProgressionItems.ESSENCE) &&
			this.input.test(input.getStackInSlot(GrinderScreenHandler.INPUT_SLOT));
	}

	public ItemStack craft(GrinderRecipeInput input, DynamicRegistryManager registryManager) {
		return this.result.copy();
	}

	public static class Serializer implements RecipeSerializer<GrinderRecipe> {
		@Override
		public GrinderRecipe read(Identifier id, JsonObject json) {
			// Read optional group
			String group = json.has("group") ? json.get("group").getAsString() : "";

			JsonObject ingredientObj = JsonHelper.getObject(json, "input");
			var ingredient = Ingredient.fromJson(JsonHelper.getElement(ingredientObj, "item"));

			// Parse result item stack
			JsonObject resultObj = JsonHelper.getObject(json, "result");
			Item item = Registries.ITEM.get(new Identifier(JsonHelper.getString(resultObj, "item")));
			ItemStack result = new ItemStack(item, 1);

			// Secondary
			JsonObject secondaryResultObj = JsonHelper.getObject(json, "secondaryResult");
			Item secondaryItem = Registries.ITEM.get(new Identifier(JsonHelper.getString(secondaryResultObj, "item")));
			ItemStack secondaryResult = new ItemStack(secondaryItem, 1);

			return new GrinderRecipe(id, group, ingredient, result, secondaryResult);
		}

		public void write(PacketByteBuf buf, GrinderRecipe recipe) {
			buf.writeString(recipe.group);
			recipe.input.write(buf);
			buf.writeItemStack(recipe.result);
			buf.writeItemStack(recipe.secondaryResult);
		}

		public GrinderRecipe read(Identifier id, PacketByteBuf buf) {
			String string = buf.readString();
			var ingredient = Ingredient.fromPacket(buf);
			ItemStack result = buf.readItemStack();
			ItemStack secondaryResult = buf.readItemStack();
			return new GrinderRecipe(id, string, ingredient, result, secondaryResult);
		}
	}

	@Override
	public boolean fits(int width, int height) {
		return width == 0 && height == 0;
	}

	@Override
	public ItemStack getOutput(DynamicRegistryManager registryManager) {
		return this.result;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}
}