package xen42.superflatprogression.screen;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionBlocks;
import xen42.superflatprogression.SuperflatProgressionItems;
import xen42.superflatprogression.SuperflatProgressionTags;
import xen42.superflatprogression.recipe.ScrollCraftingRecipe;
import xen42.superflatprogression.recipe.ScrollCraftingRecipeInput;

public class ScrollCraftingScreenHandler extends AbstractRecipeScreenHandler<ScrollCraftingRecipeInput> {

    public static final int OUTPUT_SLOT = 0;
    public static final int PARCHMENT_SLOT = 1;
    public static final int ESSENCE_SLOT = 2;
    public static final int INPUT_SLOT = 3;
    public static final int INVENTORY_SLOTS_START = 4;
    public static final int INVENTORY_SLOTS_END = 30;
    public static final int HOTBAR_SLOTS_START = 31;
    public static final int HOTBAR_SLOTS_END = 40;

    public final RecipeInputInventory inventory;
    private final CraftingResultInventory resultInventory;
    private final Property levelCost = Property.create();

    public ScreenHandlerContext context;
    private final PlayerEntity player;

    public boolean canTake(int xpCost) {
        return (player.isCreative() || player.experienceLevel >= xpCost) && xpCost > 0;
    }

    public boolean canTake() {
        return canTake(getOutputXPCost());
    }

    public boolean hasOutput() {
        return _outputSlot.hasStack();
    }

    public int getOutputXPCost() {
        return levelCost.get();
    }

    private Slot _outputSlot;
    private Slot _inputSlot;
    private Slot _parchmentSlot;
    private Slot _essenceSlot;
    
    private boolean filling;

    public ScrollCraftingScreenHandler(int syncId, PlayerInventory playerInventory){
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public ScrollCraftingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(SuperflatProgression.SCROLL_CRAFTING_SCREEN_HANDLER, syncId);
        this.inventory = new ScrollCraftingSimpleInventory(this, 4);
        this.resultInventory = new ScrollCraftingResultInventory(this);
        this.addProperty(this.levelCost);
        this.context = context;
        this.player = playerInventory.player;

        _outputSlot = this.addSlot(new OutputSlot(this, this.player, this.inventory, this.resultInventory, 0, 132, 29));
        
        _parchmentSlot = this.addSlot(new ItemSpecificSlot(this, this.inventory, PARCHMENT_SLOT, Ingredient.fromTag(SuperflatProgressionTags.ItemTags.PARCHMENTS), 37-15, 32-15));
        _essenceSlot = this.addSlot(new ItemSpecificSlot(this, this.inventory, ESSENCE_SLOT, Ingredient.ofItems(SuperflatProgressionItems.ESSENCE), 37-15, 55-15));
        _inputSlot = this.addSlot(new CustomSlot(this, this.inventory, INPUT_SLOT, 86-15, 44-15));
        
        this.addPlayerSlots(playerInventory, 8, 84);
    }
    
    protected void addPlayerHotbarSlots(Inventory playerInventory, int left, int y) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, left + i * 18, y));
        }
    }

    protected void addPlayerInventorySlots(Inventory playerInventory, int left, int top) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + (i + 1) * 9, left + j * 18, top + i * 18));
            }
        }
    }

    protected void addPlayerSlots(Inventory playerInventory, int left, int top) {
        this.addPlayerInventorySlots(playerInventory, left, top);
        this.addPlayerHotbarSlots(playerInventory, left, top + 58);
    }
    
    public static boolean isAir(ItemStack stack) {
        return stack.isOf(Items.AIR);
    }
    
    public void updateResult(
        ServerWorld world,
        @Nullable ScrollCraftingRecipe recipe
    ) {
        ScrollCraftingRecipeInput recipeInput = ScrollCraftingRecipeInput.create(this, inventory.getInputStacks());
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
        ItemStack resultStack = ItemStack.EMPTY;
        int cost = 0;
        
        Optional<ScrollCraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(SuperflatProgression.SCROLL_CRAFTING_RECIPE_TYPE, recipeInput, world);
        if (optional.isPresent()) {
            ScrollCraftingRecipe scrollRecipe = optional.get();
            boolean shouldCraftRecipe = resultInventory.shouldCraftRecipe(world, serverPlayerEntity, scrollRecipe);
            if (shouldCraftRecipe) {
                ItemStack craftedStack = scrollRecipe.craft(recipeInput, world.getRegistryManager());
                boolean isItemEnabled = craftedStack.isItemEnabled(world.getEnabledFeatures());
                if (isItemEnabled) {
                    resultStack = craftedStack;
                    cost = scrollRecipe.cost;
                }
            }
        }

        levelCost.set(cost);
        resultInventory.setStack(0, resultStack);
        this.setPreviousTrackedSlot(0, resultStack);
        serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(this.syncId, this.nextRevision(), 0, resultStack));
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (!this.filling) {
            this.context.run((world, pos) -> {
                if (world instanceof ServerWorld serverWorld) {
                    updateResult(serverWorld, null);
                }
            });
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slotAtIndex = this.slots.get(slot);
        if (slotAtIndex != null && slotAtIndex.hasStack() && slotAtIndex.canTakeItems(player)) {
            ItemStack itemStackAtIndex = slotAtIndex.getStack();
            itemStack = itemStackAtIndex.copy();
            if (slot == OUTPUT_SLOT) {
                itemStackAtIndex.getItem().onCraft(itemStackAtIndex, player.getWorld(), player);
                if (!this.insertItem(itemStackAtIndex, INVENTORY_SLOTS_START, HOTBAR_SLOTS_END, true)) {
                    return ItemStack.EMPTY;
                }

                slotAtIndex.onQuickTransfer(itemStackAtIndex, itemStack);
            } else if (slot >= INVENTORY_SLOTS_START && slot < HOTBAR_SLOTS_END) {
                if (itemStackAtIndex.isOf(SuperflatProgressionItems.ESSENCE)) {
                    if (!this.insertItem(itemStackAtIndex, ESSENCE_SLOT, ESSENCE_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (itemStackAtIndex.isIn(SuperflatProgressionTags.ItemTags.PARCHMENTS)) {
                    if (!this.insertItem(itemStackAtIndex, PARCHMENT_SLOT, PARCHMENT_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else 
                {
                    if (!this.insertItem(itemStackAtIndex, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                if (slot < HOTBAR_SLOTS_START) {
                    if (!this.insertItem(itemStackAtIndex, HOTBAR_SLOTS_START, HOTBAR_SLOTS_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemStackAtIndex, INVENTORY_SLOTS_START, HOTBAR_SLOTS_START, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStackAtIndex, INVENTORY_SLOTS_START, HOTBAR_SLOTS_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStackAtIndex.isEmpty()) {
                slotAtIndex.setStack(ItemStack.EMPTY);
            } else {
                slotAtIndex.markDirty();
            }

            if (itemStackAtIndex.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slotAtIndex.onTakeItem(player, itemStackAtIndex);
            if (slot == OUTPUT_SLOT) {
                player.dropItem(itemStackAtIndex, false);
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, SuperflatProgressionBlocks.SCROLL_CRAFTING);
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        return index != this.getCraftingResultSlotIndex();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
    }

    @Override
    public void populateRecipeFinder(RecipeMatcher finder) {
        this.inventory.provideRecipeInputs(finder);
    }

    @Override
    public RecipeBookCategory getCategory() {
        return RecipeBookCategory.CRAFTING; // Return crafting because making a RecipeBookCategory is impossible.
    }

    public void onInputSlotFillStart() {
        this.filling = true;
    }

    public void onInputSlotFillFinish(ServerWorld world, ScrollCraftingRecipe recipe) {
        this.filling = false;
        updateResult(world, recipe);
    }

    public Slot getOutputSlot() {
        return this._outputSlot;
    }

    public List<Slot> getInputSlots() {
        return this.slots.subList(PARCHMENT_SLOT, INPUT_SLOT);
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return OUTPUT_SLOT;
    }

    @Override
    public int getCraftingWidth() {
        return 3;
    }

    @Override
    public int getCraftingHeight() {
        return 1;
    }

    @Override
    public int getCraftingSlotCount() {
        return INPUT_SLOT;
    }

    @Override
    public void clearCraftingSlots() {
        this.inventory.clear();
        this.resultInventory.clear();
    }

    @Override
    public boolean matches(Recipe<? super ScrollCraftingRecipeInput> recipe) {
    	ScrollCraftingRecipe recipeValue = (ScrollCraftingRecipe)recipe;
        return recipeValue.matches(ScrollCraftingRecipeInput.create(this, this.inventory.getInputStacks()), this.player.getWorld());
    }
    
    private class ScrollCraftingSimpleInventory extends SimpleInventory implements RecipeInputInventory {
        private ScreenHandler _screen;
        ScrollCraftingSimpleInventory(ScrollCraftingScreenHandler screen, int size) {
            super(size);
            _screen = screen;
        }

        public void markDirty() {
            _screen.onContentChanged(this);
            super.markDirty();
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
			return this.stacks;
		}
    }

    private class ScrollCraftingResultInventory extends CraftingResultInventory {
        private ScreenHandler _screen;
        ScrollCraftingResultInventory(ScrollCraftingScreenHandler screen) {
            super();
            _screen = screen;
        }

        public void markDirty() {
            _screen.onContentChanged(this);
            super.markDirty();
        }
    }

    private class CustomSlot extends Slot {
        private ScrollCraftingScreenHandler _altar;
        public CustomSlot(ScrollCraftingScreenHandler altar, Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            _altar = altar;
        }

        @Override
        public void markDirty() {
            super.markDirty();
            _altar.onContentChanged(_altar.inventory);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return true;
        }
    }

    private class ItemSpecificSlot extends CustomSlot {
        private Ingredient _ingredient;

        public ItemSpecificSlot(ScrollCraftingScreenHandler altar, Inventory inventory, int index, Ingredient ingredient, int x, int y) {
            super(altar, inventory, index, x, y);
            _ingredient = ingredient;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return isAir(stack) || _ingredient.test(stack);
        }
    }

    private class OutputSlot extends Slot {
        private final RecipeInputInventory input;
        private final PlayerEntity player;
        private final ScrollCraftingScreenHandler handler;
        private int amount;
        public OutputSlot(ScrollCraftingScreenHandler handler, PlayerEntity player, RecipeInputInventory input, Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            this.player = player;
            this.input = input;
            this.handler = handler;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity player) {
            return handler.canTake();
        }

        @Override
        public ItemStack takeStack(int amount) {
            if (this.hasStack()) {
                this.amount = this.amount + Math.min(amount, this.getStack().getCount());
            }

            return super.takeStack(amount);
        }

        @Override
        protected void onCrafted(ItemStack stack, int amount) {
            this.amount += amount;
            this.onCrafted(stack);
        }

        @Override
        protected void onTake(int amount) {
            this.amount += amount;
        }

        @Override
        protected void onCrafted(ItemStack stack) {
            if (this.amount > 0) {
                stack.onCraft(this.player.getWorld(), this.player, this.amount);
            }

            if (this.inventory instanceof RecipeUnlocker recipeUnlocker) {
                recipeUnlocker.unlockLastRecipe(this.player, this.input.getInputStacks());
            }

            this.amount = 0;
        }

        private static DefaultedList<ItemStack> copyInput(ScrollCraftingRecipeInput input) {
            DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

            for (int i = 0; i < defaultedList.size(); i++) {
                defaultedList.set(i, input.getStackInSlot(i));
            }

            return defaultedList;
        }

        private DefaultedList<ItemStack> getRecipeRemainders(ScrollCraftingRecipeInput input, World world) {
            return world instanceof ServerWorld serverWorld
                ? (DefaultedList<ItemStack>)serverWorld.getRecipeManager()
                    .getFirstMatch(SuperflatProgression.SCROLL_CRAFTING_RECIPE_TYPE, input, serverWorld)
                    .map(recipe -> recipe.getRecipeRemainders(input))
                    .orElseGet(() -> copyInput(input))
                : ScrollCraftingRecipe.collectRecipeRemainders(input);
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            this.onCrafted(stack);
            ScrollCraftingRecipeInput recipeInput = ScrollCraftingRecipeInput.create(this.handler, this.input.getInputStacks());
            DefaultedList<ItemStack> defaultedList = this.getRecipeRemainders(recipeInput, player.getWorld());

            this.handler.context.run((world, pos) -> {
                if (!player.isCreative()) {
                    player.addExperienceLevels(-getOutputXPCost());
                }
                world.playSound((Entity)null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                world.playSound((Entity)null, pos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
            });

            for (int z = 1; z <= 3; z++) {
                if(!this.input.getStack(z).isEmpty()) {
                    this.input.removeStack(z, 1);
                }
            }
        }
    }
}