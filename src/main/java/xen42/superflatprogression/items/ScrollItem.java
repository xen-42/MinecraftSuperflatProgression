package xen42.superflatprogression.items;

import java.util.function.Consumer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ScrollItem extends Item {

    private Consumer<ServerPlayerEntity> _onUse;

    public ScrollItem(Settings settings, Consumer<ServerPlayerEntity> onUse) {
        super(settings);
        _onUse = onUse;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // TODO: Add sound
        //if (_sound != null) {
        //    world.playSoundFromEntity(user, user, _sound, SoundCategory.HOSTILE, 0.2f, 1f);
        //}

        if (!user.isCreative()) {
            itemStack.decrement(1);
        }

        if (!world.isClient()) {
            _onUse.accept((ServerPlayerEntity)user);
        }

        return TypedActionResult.consume(itemStack);
    }    

    @Override
    public boolean hasGlint(ItemStack stack) {
		return true;
	}
}
