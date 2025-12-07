package xen42.superflatprogression;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.screen.GrinderScreenHandler;

public class SuperflatProgressionNetworking {
    public static final Identifier GRIND_BUTTON_PRESSED = Identifier.of(SuperflatProgression.MOD_ID, "grind_button_pressed");

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(GRIND_BUTTON_PRESSED, (server, player, handler, buf, responseSender) -> {
            SuperflatProgression.LOGGER.info("hello??");
            server.execute(() -> {
                if (player.currentScreenHandler instanceof GrinderScreenHandler grinderHandler) {
                    var world = player.getWorld();
                    var pos = player.getBlockPos();

                    world.playSound((Entity)null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                    var output = grinderHandler.getOutputSlot().inventory.getStack(0);
                    if (!output.isEmpty()) {
                        var itemEntity = new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(output.getItem(), output.getCount()));
                        world.spawnEntity(itemEntity);

                        if(!grinderHandler.inventory.getStack(GrinderScreenHandler.BUCKET_SLOT).isEmpty()) {
                            grinderHandler.inventory.removeStack(GrinderScreenHandler.BUCKET_SLOT, 1);
                        }
                        if(!grinderHandler.inventory.getStack(GrinderScreenHandler.INPUT_SLOT).isEmpty()) {
                            grinderHandler.inventory.removeStack(GrinderScreenHandler.INPUT_SLOT, 1);
                        }
                    }
                }
            });
        });
    }
}
