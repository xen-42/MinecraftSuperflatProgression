package xen42.superflatprogression.items;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import xen42.superflatprogression.SuperflatProgressionTags;

public class EnrichedBoneMealItem extends BoneMealItem {

    public EnrichedBoneMealItem(Settings settings) {
        super(settings);
    }
    
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(context.getSide());

        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isSideSolidFullSquare(world, blockPos, context.getSide());

        if (!bl) {
            // Do a 3x3 bonemeal square
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    var pos = blockPos.add(i, 0, j);
                    if (useOnFertilizable(context.getStack(), world, pos)) {
                        if (!world.isClient) {
                            world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, pos, 0);
                        }
                    }
                }
            }
        }
        if (bl && enrichedUseOnGround(context.getStack(), world, blockPos2)) {
            return ActionResult.success(world.isClient);
        } 
        enrichedCreateParticles(world, blockPos2, DEFAULT_MAX_COUNT);
        world.playSoundAtBlockCenter(blockPos2, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.PLAYERS, 1f, 1f, bl);
        return ActionResult.success(world.isClient);
	}

    public static void enrichedCreateParticles(WorldAccess world, BlockPos pos, int count) {
		if (count == 0) {
			count = 15;
		}

        double d = 0.5;
        double e;

        count *= 3;
        e = 1.0;
        d = 3.0;

        world.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        Random random = world.getRandom();

        for (int i = 0; i < count; i++) {
            double f = random.nextGaussian() * 0.02;
            double g = random.nextGaussian() * 0.02;
            double h = random.nextGaussian() * 0.02;
            double j = 0.5 - d;
            double k = pos.getX() + j + random.nextDouble() * d * 2.0;
            double l = pos.getY() + random.nextDouble() * e;
            double m = pos.getZ() + j + random.nextDouble() * d * 2.0;
            if (!world.getBlockState(BlockPos.ofFloored(k, l, m).down()).isAir()) {
                world.addParticle(ParticleTypes.HAPPY_VILLAGER, k, l, m, f, g, h);
            }
        }
	}

    public static boolean enrichedUseOnGround(ItemStack stack, World world, BlockPos blockPos) {
        if (!(world instanceof ServerWorld)) {
            return false;
        }
        
        var random = world.getRandom();
        var visitedSpaces = new ArrayList<BlockPos>();

        var flagDidAnything = false;
        
        outerLabel:
        for (int i = 0; i < 128; i++) {
            var blockPos2 = blockPos;
            for (int j = 0; j < i / 16; j++) {
                blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (world.getBlockState(blockPos2).isFullCube(world, blockPos2) || visitedSpaces.contains(blockPos2)) {
                    continue outerLabel;
                }
            }

            // Ok now place a thing
            var isAir = world.getBlockState(blockPos2).isAir();
            var isWater = world.getBlockState(blockPos2).isOf(Blocks.WATER);
            var groundState = world.getBlockState(blockPos2.down());
            ArrayList<Block> possibleBlocks = null;

            if (isAir) {
                if (groundState.isIn(SuperflatProgressionTags.BlockTags.MUSHROOM_BLOCK_PLACEABLE)) {
                    possibleBlocks = SuperflatProgressionTags.GetBlocksInTag(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_MUSHROOM);
                }
                else if (groundState.isOf(Blocks.SAND)) {
                    possibleBlocks = SuperflatProgressionTags.GetBlocksInTag(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_ON_SAND);
                }
                else if (groundState.isOf(Blocks.GRASS_BLOCK)) {
                    possibleBlocks = SuperflatProgressionTags.GetBlocksInTag(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_ON_GRASS);
                }
                else if (groundState.isOf(Blocks.SOUL_SAND)) {
                    possibleBlocks = SuperflatProgressionTags.GetBlocksInTag(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_ON_SOUL_SAND);
                }
                else if (groundState.isOf(Blocks.SUGAR_CANE) && !world.getBlockState(blockPos.down(2)).isOf(Blocks.SUGAR_CANE)) {
                    possibleBlocks = new ArrayList<>(List.of(Blocks.SUGAR_CANE));
                }
                else if (groundState.isOf(Blocks.CACTUS) && !world.getBlockState(blockPos.down(2)).isOf(Blocks.CACTUS)) {
                    possibleBlocks = new ArrayList<>(List.of(Blocks.CACTUS));
                }
            }            
            else if (isWater && groundState.isOpaque()) {
                possibleBlocks = SuperflatProgressionTags.GetBlocksInTag(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_UNDER_WATER);
            }

            if (possibleBlocks != null) {
                var block = possibleBlocks.get(random.nextInt(possibleBlocks.size()));
                if (block != null && block != Blocks.AIR && block.getDefaultState().canPlaceAt(world, blockPos2)) {
                    world.setBlockState(blockPos2, block.getDefaultState());
                    block.onPlaced(world, blockPos2, world.getBlockState(blockPos2), (LivingEntity)stack.getHolder(), stack);
                    flagDidAnything = true;
                    visitedSpaces.add(blockPos2);
                    if (block instanceof TallPlantBlock || block instanceof TallFlowerBlock) {
                        visitedSpaces.add(blockPos2.up());
                    }
                }
            }
        }

        if (flagDidAnything) {
            stack.decrement(1);
        }

        return flagDidAnything;
    }
}
