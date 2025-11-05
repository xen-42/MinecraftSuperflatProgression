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

public class ScrollCraftingRecipe implements Recipe<ScrollCraftingRecipeInput> {
	final Identifier id;
	public final ItemStack result;
	public final Ingredient input;
	public final int cost;
	final String group;

	public ScrollCraftingRecipe(Identifier id, String group, Ingredient input, ItemStack result, int cost) {
		this.id = id;
		this.group = group;
		this.input = input;
		this.result = result;
		this.cost = cost;
	}

	@Override
	public RecipeType<ScrollCraftingRecipe> getType() {
		return SuperflatProgression.SCROLL_CRAFTING_RECIPE_TYPE;
	}

	@Override
	public RecipeSerializer<? extends ScrollCraftingRecipe> getSerializer() {
		return SuperflatProgression.SCROLL_CRAFTING_RECIPE_SERIALIZER;
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

	public DefaultedList<ItemStack> getRecipeRemainders(ScrollCraftingRecipeInput input) {
		return collectRecipeRemainders(input);
	}

	public static DefaultedList<ItemStack> collectRecipeRemainders(ScrollCraftingRecipeInput input) {
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

	public boolean matches(ScrollCraftingRecipeInput input, World world) {
		return input.getStackInSlot(0).isOf(SuperflatProgressionItems.PARCHMENT) &&
			input.getStackInSlot(1).isOf(SuperflatProgressionItems.ESSENCE) &&
			this.input.test(input.getStackInSlot(2));
	}

	public ItemStack craft(ScrollCraftingRecipeInput input, DynamicRegistryManager registryManager) {
		return this.result.copy();
	}

	public ItemStack result() {
		return this.result;
	}
	
	public int getCost() {
		return cost;
	}

	public static class Serializer implements RecipeSerializer<ScrollCraftingRecipe> {
		@Override
		public ScrollCraftingRecipe read(Identifier id, JsonObject json) {
			// Read optional group
			String group = json.has("group") ? json.get("group").getAsString() : "";

			// Read optional cost
			var cost = JsonHelper.getInt(json, "cost");

			JsonObject ingredientObj = JsonHelper.getObject(json, "input");
			var ingredient = Ingredient.fromJson(JsonHelper.getElement(ingredientObj, "item"));

			// Parse result item stack
			JsonObject resultObj = JsonHelper.getObject(json, "result");
			Item item = Registries.ITEM.get(new Identifier(JsonHelper.getString(resultObj, "item")));
			ItemStack result = new ItemStack(item, 1);

			return new ScrollCraftingRecipe(id, group, ingredient, result, cost);
		}

		public void write(PacketByteBuf buf, ScrollCraftingRecipe recipe) {
			buf.writeString(recipe.group);
			recipe.input.write(buf);
			buf.writeItemStack(recipe.result());
			buf.writeVarInt(recipe.cost);
		}

		public ScrollCraftingRecipe read(Identifier id, PacketByteBuf buf) {
			String string = buf.readString();
			var ingredient = Ingredient.fromPacket(buf);
			ItemStack result = buf.readItemStack();
			var cost = buf.readInt();
			return new ScrollCraftingRecipe(id, string, ingredient, result, cost);
		}
	}

	@Override
	public boolean fits(int width, int height) {
		return width >= 0 && width < 3 && height == 0;
	}

	@Override
	public ItemStack getOutput(DynamicRegistryManager registryManager) {
		return result();
	}

	@Override
	public Identifier getId() {
		return this.id;
	}
}