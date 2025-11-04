package xen42.superflatprogression.items;

import java.util.ArrayList;
import java.util.List;


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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

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
            var isDark = world.getLightLevel(blockPos2, world.getAmbientDarkness()) < 12;
            var isAir = world.getBlockState(blockPos2).isAir();
            var isWater = world.getBlockState(blockPos2).isOf(Blocks.WATER);
            var isAdjacentToWater = world.getBlockState(blockPos2.down().east()).isOf(Blocks.WATER) ||
                world.getBlockState(blockPos2.down().north()).isOf(Blocks.WATER) ||
                world.getBlockState(blockPos2.down().south()).isOf(Blocks.WATER) ||
                world.getBlockState(blockPos2.down().west()).isOf(Blocks.WATER);
            var groundState = world.getBlockState(blockPos2.down());
            ArrayList<Block> possibleBlocks = null;

            if (isAir) {
                if (isDark && (groundState.isOf(Blocks.STONE) || groundState.isOf(Blocks.DIRT))) {
                    possibleBlocks = new ArrayList<>(List.of(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM));
                }
                else if (groundState.isOf(Blocks.SAND)) {
                    if (isAdjacentToWater) {
                        possibleBlocks = new ArrayList<>(List.of(Blocks.CACTUS, Blocks.DEAD_BUSH, Blocks.SUGAR_CANE, Blocks.SUGAR_CANE, Blocks.SUGAR_CANE));
                    }
                    else {
                        possibleBlocks = new ArrayList<>(List.of(Blocks.CACTUS, Blocks.DEAD_BUSH));
                    }
                }
                else if (groundState.isOf(Blocks.GRASS_BLOCK)) {
                    possibleBlocks = new ArrayList<>(List.of(Blocks.GRASS, Blocks.GRASS, Blocks.GRASS, Blocks.TALL_GRASS, Blocks.TALL_GRASS,
                        Blocks.FERN, Blocks.OAK_SAPLING, Blocks.SUNFLOWER, Blocks.ROSE_BUSH, Blocks.LILAC, Blocks.PEONY));
                }
                else if (groundState.isOf(Blocks.SOUL_SAND)) {
                    possibleBlocks = new ArrayList<>(List.of(Blocks.NETHER_WART));
                }
                else if (groundState.isOf(Blocks.SUGAR_CANE) && !world.getBlockState(blockPos.down(2)).isOf(Blocks.SUGAR_CANE)) {
                    possibleBlocks = new ArrayList<>(List.of(Blocks.SUGAR_CANE));
                }
                else if (groundState.isOf(Blocks.CACTUS) && !world.getBlockState(blockPos.down(2)).isOf(Blocks.CACTUS)) {
                    possibleBlocks = new ArrayList<>(List.of(Blocks.CACTUS));
                }
            }            
            else if (isWater && groundState.isOpaque()) {
                possibleBlocks = new ArrayList<>(List.of(Blocks.SEAGRASS, Blocks.SEAGRASS, Blocks.SEAGRASS, Blocks.SEAGRASS, Blocks.SEAGRASS, Blocks.SEAGRASS,
                    Blocks.SEA_PICKLE, Blocks.FIRE_CORAL_BLOCK, Blocks.HORN_CORAL_BLOCK, Blocks.TUBE_CORAL_BLOCK, Blocks.BRAIN_CORAL_BLOCK, Blocks.BUBBLE_CORAL_BLOCK,
                    Blocks.FIRE_CORAL_FAN, Blocks.HORN_CORAL_FAN, Blocks.TUBE_CORAL_FAN, Blocks.BRAIN_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN));
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
