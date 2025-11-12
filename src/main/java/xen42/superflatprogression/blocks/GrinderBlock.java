package xen42.superflatprogression.blocks;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.recipe.GrinderRecipe;
import xen42.superflatprogression.screen.GrinderScreenHandler;

public class GrinderBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
    private static VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 14, 14);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public GrinderBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
            .with(FACING, Direction.NORTH).with(TRIGGERED, false));
    }

    @Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var blockStateAbove = world.getBlockState(pos.up());
        if (blockStateAbove instanceof Inventory || blockStateAbove.isOpaque()) {
            return ActionResult.FAIL;
        }
        else {
            if (!world.isClient) {
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            }
            return ActionResult.SUCCESS;
        }
    }
    
    public Text getTitle() {
        return Text.translatable(getTranslationKey());
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return (NamedScreenHandlerFactory)new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> 
            new GrinderScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), getTitle());
    }

    @Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(TRIGGERED);
	}

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean isReceivingPower = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean wasTriggered = (Boolean)state.get(TRIGGERED);
        if (isReceivingPower && !wasTriggered) {
            world.scheduleBlockTick(pos, this, 4); // Same settings as dispenser because why not
            world.setBlockState(pos, (BlockState)state.with(TRIGGERED, true), 4);
        } else if (!isReceivingPower && wasTriggered) {
            world.setBlockState(pos, (BlockState)state.with(TRIGGERED, false), 4);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        var entityAbove = world.getBlockEntity(pos.up());

        var craftedFlag = false;

        if (entityAbove != null && entityAbove instanceof Inventory inventory) {
            for (int i = 0; i < inventory.size(); i++) {
                var itemStack = inventory.getStack(i);
                if (itemStack.isEmpty()) {
                    continue;
                }
                var optionalRecipe = world.getRecipeManager().recipes.getOrDefault(SuperflatProgression.GRINDER_RECIPE_TYPE, Collections.emptyMap()).values().stream().filter((recipe) -> {
                    var grinderRecipe = ((GrinderRecipe)recipe);
                    return grinderRecipe.input.test(itemStack);
                }).findFirst();

                if (optionalRecipe.isPresent()) {
                    craftedFlag = tryCraft((GrinderRecipe)optionalRecipe.get(), itemStack, world, pos);
                }
                break;
            }
        }

        if (!craftedFlag) {
            world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.1F + 0.8F);
        }
    }

    private ItemStack tryInsert(Inventory inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.size(); i++) {
            var targetStack = inventory.getStack(i);
            if (targetStack.isEmpty()) {
                inventory.setStack(i, itemStack);
                return ItemStack.EMPTY;
            }
            else if(targetStack.isOf(itemStack.getItem()) && targetStack.getCount() + itemStack.getCount() <= itemStack.getMaxCount()) {
                inventory.setStack(i, new ItemStack(itemStack.getItem(), targetStack.getCount() + itemStack.getCount()));
                return ItemStack.EMPTY;
            }
            // Overflow
            else if (targetStack.isOf(itemStack.getItem())) {
                var diff = targetStack.getMaxCount() - targetStack.getCount();
                inventory.setStack(i, new ItemStack(itemStack.getItem(), itemStack.getMaxCount()));
                itemStack.decrement(diff);
            }
        }
        // Couldn't insert into inventory
        return itemStack;
    }

    private boolean tryCraft(GrinderRecipe recipe, ItemStack inputStack, ServerWorld world, BlockPos pos) {
        if (!recipe.needsBucket || tryConsumeBucket(world, pos)) {
            // Either didn't need a bucket or successfully consumed one, now craft
            var output = new ItemStack(recipe.result.getItem(), recipe.result.getCount());
            inputStack.decrement(1);

            var facing = world.getBlockState(pos).get(this.FACING).getVector();
            if (world.getBlockEntity(pos.add(facing)) instanceof Inventory inventory) {
                output = tryInsert(inventory, output);
            }
            // Either there was no inventory or it tried to put it in but there was overflow
            if (!output.isEmpty()) {
                var spawnPos = new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f)
                    .add(new Vec3d(facing.getX(), facing.getY(), facing.getZ()).multiply(0.5f));
                ItemEntity itemEntity = new ItemEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), output);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }

            // Do some smoke particles to show it did something
            world.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5f, pos.getZ() + 0.5f, 5, 0.3, 0.2, 0.3, 0);
            var random = world.getRandom();
            world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.1F + 1.0F);
            return true;
        }
        else {
            return false;
        }
    }

    private boolean tryConsumeBucket(ServerWorld world, BlockPos pos) {
        var hopperInventory = getAdjacentHopperInventoryWithBucket(world, pos);
        if (hopperInventory != null) {
            for (int j = 0; j < hopperInventory.size(); j++) {
                if (hopperInventory.getStack(j).isOf(Items.BUCKET)) {
                    hopperInventory.getStack(j).decrement(1);
                    return true;
                }
            }
        }
        return false;
    }

    private Inventory getAdjacentHopperInventoryWithBucket(ServerWorld world, BlockPos pos) {
        for (var adjDir : List.of(new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1))) {
            var adjPos = pos.add(adjDir);
            var adjState = world.getBlockState(adjPos);
            // If its a hopper facing towards the pulverizer
            if (adjState.getBlock() instanceof HopperBlock && world.getBlockState(adjPos).get(HopperBlock.ENABLED) 
                && world.getBlockState(adjPos).get(HopperBlock.FACING).getVector().equals(adjDir.multiply(-1))) {
                var entity = world.getBlockEntity(adjPos);
                if (entity instanceof HopperBlockEntity) {
                    var inventory = HopperBlockEntity.getInventoryAt(world, adjPos);
                    if (inventory.containsAny((itemStack) -> itemStack.isOf(Items.BUCKET))) {
                        return inventory;
                    }
                }
            }
        }
        return null;
    }
}