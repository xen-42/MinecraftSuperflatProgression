package xen42.superflatprogression.items;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgression;

public class ScrollItem extends Item {

    private Consumer<ServerPlayerEntity> _onUse;

    public ScrollItem(Settings settings, Consumer<ServerPlayerEntity> onUse) {
        super(settings);
        _onUse = onUse;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var itemStack = user.getStackInHand(hand);

        world.playSoundFromEntity(user, user, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS, 2f, 0.5f);

        if (!user.isCreative()) {
            itemStack.decrement(1);
        }

        if (!world.isClient()) {
            _onUse.accept((ServerPlayerEntity)user);
            var serverWorld = (ServerWorld)world;
            serverWorld.spawnParticles(
				SuperflatProgression.PIXIE_PARTICLE,
				user.getEyePos().getX(),
				user.getEyePos().getY(),
				user.getEyePos().getZ(),
				30,
				1, 0.5, 1,
				1
				);
        }
        return TypedActionResult.success(itemStack);
    }    

    @Override
    public boolean hasGlint(ItemStack stack) {
		return true;
	}
}
