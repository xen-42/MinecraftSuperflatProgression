package xen42.superflatprogression.screen;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.windows.INPUT;

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
import xen42.superflatprogression.recipe.GrinderRecipe;
import xen42.superflatprogression.recipe.GrinderRecipeInput;

public class GrinderScreenHandler extends AbstractRecipeScreenHandler<GrinderRecipeInput> {

    public static final int OUTPUT_SLOT = 0;
    public static final int BUCKET_SLOT = 2;
    public static final int INPUT_SLOT = 1;
    public static final int INVENTORY_SLOTS_START = 3;
    public static final int INVENTORY_SLOTS_END = 29;
    public static final int HOTBAR_SLOTS_START = 30;
    public static final int HOTBAR_SLOTS_END = 39;

    public final RecipeInputInventory inventory;
    private final CraftingResultInventory resultInventory;

    public ScreenHandlerContext context;
    private final PlayerEntity player;

    public boolean hasOutput() {
        return _outputSlot.hasStack();
    }

    private Slot _outputSlot;
    
    private boolean filling;

    public GrinderScreenHandler(int syncId, PlayerInventory playerInventory){
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public GrinderScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(SuperflatProgression.GRINDER_SCREEN_HANDLER, syncId);
        this.inventory = new GrinderSimpleInventory(this, 4);
        this.resultInventory = new GrinderResultInventory(this);
        this.context = context;
        this.player = playerInventory.player;

        _outputSlot = this.addSlot(new OutputSlot(this, this.player, this.inventory, this.resultInventory, 0, 111, 34));
        
        this.addSlot(new CustomSlot(this, this.inventory, INPUT_SLOT, 53, 34));
        this.addSlot(new ItemSpecificSlot(this, this.inventory, BUCKET_SLOT, Ingredient.ofItems(Items.BUCKET), 27, 34));
        
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
        @Nullable GrinderRecipe recipe
    ) {
        GrinderRecipeInput recipeInput = GrinderRecipeInput.create(this, inventory.getInputStacks());
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
        ItemStack resultStack = ItemStack.EMPTY;
        
        Optional<GrinderRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(SuperflatProgression.GRINDER_RECIPE_TYPE, recipeInput, world);
        if (optional.isPresent()) {
            GrinderRecipe grinderRecipe = optional.get();
            boolean shouldCraftRecipe = resultInventory.shouldCraftRecipe(world, serverPlayerEntity, grinderRecipe);
            if (shouldCraftRecipe) {
                ItemStack craftedStack = grinderRecipe.craft(recipeInput, world.getRegistryManager());
                boolean isItemEnabled = craftedStack.isItemEnabled(world.getEnabledFeatures());
                if (isItemEnabled) {
                    resultStack = craftedStack;
                }
            }
        }

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
                if (itemStackAtIndex.isOf(Items.BUCKET)) {
                    if (!this.insertItem(itemStackAtIndex, BUCKET_SLOT, BUCKET_SLOT + 1, false)) {
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
        return canUse(this.context, player, SuperflatProgressionBlocks.GRINDER);
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

    public void onInputSlotFillFinish(ServerWorld world, GrinderRecipe recipe) {
        this.filling = false;
        updateResult(world, recipe);
    }

    public Slot getOutputSlot() {
        return this._outputSlot;
    }

    public List<Slot> getInputSlots() {
        return this.slots.subList(BUCKET_SLOT, INPUT_SLOT);
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
        return 2;
    }

    @Override
    public int getCraftingHeight() {
        return 1;
    }

    @Override
    public int getCraftingSlotCount() {
        return 2;
    }

    @Override
    public void clearCraftingSlots() {
        this.inventory.clear();
        this.resultInventory.clear();
    }

    @Override
    public boolean matches(Recipe<? super GrinderRecipeInput> recipe) {
    	GrinderRecipe recipeValue = (GrinderRecipe)recipe;
        return recipeValue.matches(GrinderRecipeInput.create(this, this.inventory.getInputStacks()), this.player.getWorld());
    }
    
    private class GrinderSimpleInventory extends SimpleInventory implements RecipeInputInventory {
        private ScreenHandler _screen;
        GrinderSimpleInventory(GrinderScreenHandler screen, int size) {
            super(size);
            _screen = screen;
        }

        public void markDirty() {
            _screen.onContentChanged(this);
            super.markDirty();
        }

        @Override
        public int getWidth() {
            return 2;
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

    private class GrinderResultInventory extends CraftingResultInventory {
        private ScreenHandler _screen;
        GrinderResultInventory(GrinderScreenHandler screen) {
            super();
            _screen = screen;
        }

        public void markDirty() {
            _screen.onContentChanged(this);
            super.markDirty();
        }
    }

    private class CustomSlot extends Slot {
        private GrinderScreenHandler _altar;
        public CustomSlot(GrinderScreenHandler altar, Inventory inventory, int index, int x, int y) {
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

        public ItemSpecificSlot(GrinderScreenHandler altar, Inventory inventory, int index, Ingredient ingredient, int x, int y) {
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
        private final GrinderScreenHandler handler;
        private int amount;
        public OutputSlot(GrinderScreenHandler handler, PlayerEntity player, RecipeInputInventory input, Inventory inventory, int index, int x, int y) {
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
            return true;
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

        private static DefaultedList<ItemStack> copyInput(GrinderRecipeInput input) {
            DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

            for (int i = 0; i < defaultedList.size(); i++) {
                defaultedList.set(i, input.getStackInSlot(i));
            }

            return defaultedList;
        }

        private DefaultedList<ItemStack> getRecipeRemainders(GrinderRecipeInput input, World world) {
            return world instanceof ServerWorld serverWorld
                ? (DefaultedList<ItemStack>)serverWorld.getRecipeManager()
                    .getFirstMatch(SuperflatProgression.GRINDER_RECIPE_TYPE, input, serverWorld)
                    .map(recipe -> recipe.getRecipeRemainders(input))
                    .orElseGet(() -> copyInput(input))
                : GrinderRecipe.collectRecipeRemainders(input);
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            this.onCrafted(stack);
            GrinderRecipeInput recipeInput = GrinderRecipeInput.create(this.handler, this.input.getInputStacks());
            DefaultedList<ItemStack> defaultedList = this.getRecipeRemainders(recipeInput, player.getWorld());

            this.handler.context.run((world, pos) -> {
                world.playSound((Entity)null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
            });

            if(!this.input.getStack(BUCKET_SLOT).isEmpty()) {
                this.input.removeStack(BUCKET_SLOT, 1);
            }
            if(!this.input.getStack(INPUT_SLOT).isEmpty()) {
                this.input.removeStack(INPUT_SLOT, 1);
            }
        }
    }
}