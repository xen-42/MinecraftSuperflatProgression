package xen42.superflatprogression.recipe;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import xen42.superflatprogression.screen.ScrollCraftingScreenHandler;

public class ScrollCraftingRecipeInput implements RecipeInputInventory {
	private final List<ItemStack> stacks;
	private final RecipeMatcher matcher = new RecipeMatcher();
	private final int stackCount;
	private final ScrollCraftingScreenHandler handler;

	private ScrollCraftingRecipeInput(ScrollCraftingScreenHandler handler, List<ItemStack> stacks) {
		this.handler = handler;
		this.stacks = stacks;
		int i = 0;

		for (ItemStack itemStack : stacks) {
			if (!itemStack.isEmpty()) {
				i++;
				this.matcher.addInput(itemStack, 1);
			}
		}

		this.stackCount = i;
	}

	public static ScrollCraftingRecipeInput create(ScrollCraftingScreenHandler handler, List<ItemStack> stacks) {
		return new ScrollCraftingRecipeInput(handler, stacks);
	}

	public ItemStack getStackInSlot(int slot) {
		return (ItemStack)this.stacks.get(slot);
	}

	public int size() {
		return this.stacks.size();
	}

	@Override
	public boolean isEmpty() {
		return this.stackCount == 0;
	}

	public RecipeMatcher getRecipeMatcher() {
		return this.matcher;
	}

	public List<ItemStack> getStacks() {
		return this.stacks;
	}

	public int getStackCount() {
		return this.stackCount;
	}
	
	public static boolean stacksEqual(List<ItemStack> left, List<ItemStack> right) {
		if (left.size() != right.size()) {
			return false;
		} else {
			for (int i = 0; i < left.size(); i++) {
				if (!ItemStack.areEqual((ItemStack)left.get(i), (ItemStack)right.get(i))) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else {
			return !(o instanceof ScrollCraftingRecipeInput scollCraftingRecipeInput)
				? false
				: this.stackCount == scollCraftingRecipeInput.stackCount
					&& stacksEqual(this.stacks, scollCraftingRecipeInput.stacks);
		}
	}

	public int hashCode() {
		int i = 0;
		
		for (ItemStack itemStack : this.stacks) {
			i = i * 31 + itemStack.hashCode();
		}

		return i;
	}

	public int getSize() {
		return size();
	}

	@Override
	public ItemStack getStack(int slot) {
		return slot >= this.size() ? ItemStack.EMPTY : this.stacks.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(this.stacks, slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack itemStack = Inventories.splitStack(this.stacks, slot, amount);
		if (!itemStack.isEmpty()) {
			this.handler.onContentChanged(this);
		}

		return itemStack;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);
		this.handler.onContentChanged(this);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		getStacks().clear();
	}

	@Override
	public void provideRecipeInputs(RecipeMatcher finder) {
		for (ItemStack itemStack : getStacks()) {
			finder.addUnenchantedInput(itemStack);
		}
	}

	@Override
	public int getWidth() {
		return 3;
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@Override
	public List<ItemStack> getInputStacks() {
		return List.copyOf(getStacks());
	}
}