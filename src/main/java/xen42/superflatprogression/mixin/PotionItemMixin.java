package xen42.superflatprogression.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

@Mixin(PotionItem.class)
public class PotionItemMixin {
	// This is all to make it so using a water bottle on a lava block makes cobble
	@Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		PlayerEntity playerEntity = context.getPlayer();
		ItemStack itemStack = context.getStack();
		BlockState blockState = world.getBlockState(blockPos);
		if (context.getSide() != Direction.DOWN && blockState.isOf(Blocks.MAGMA_BLOCK) && PotionUtil.getPotion(itemStack) == Potions.WATER) {
			world.playSound(null, blockPos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
			playerEntity.setStackInHand(context.getHand(), ItemUsage.exchangeStack(itemStack, playerEntity, new ItemStack(Items.GLASS_BOTTLE)));
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
			if (!world.isClient) {
				ServerWorld serverWorld = (ServerWorld)world;

				for (int i = 0; i < 5; i++) {
					serverWorld.spawnParticles(
						ParticleTypes.SMOKE,
						blockPos.getX() + world.random.nextDouble(),
						blockPos.getY() + 1,
						blockPos.getZ() + world.random.nextDouble(),
						1,
						0.0,
						0.0,
						0.0,
						0.01
					);
				}
			}

			world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
			world.emitGameEvent(null, GameEvent.FLUID_PLACE, blockPos);
			world.setBlockState(blockPos, Blocks.COBBLESTONE.getDefaultState());
			info.setReturnValue(ActionResult.success(world.isClient));
            info.cancel();
		}
	}
}
