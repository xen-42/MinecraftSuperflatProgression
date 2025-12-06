package xen42.superflatprogression.compat;

import java.util.function.Consumer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionItems;

public class ModEventCompatibility {
    public static void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("peaceful-items")) {
            try {
                // When using a Dragon effigy, give the player an Ender Star as well
                var peacefulModEvents = Class.forName("xen42.peacefulitems.PeacefulModEvents");
                var totemEventRegister = peacefulModEvents.getMethod("RegisterTotemEventListener", String.class, Consumer.class);
                Consumer<ServerPlayerEntity> listener = player -> {
                    var item = player.dropItem(SuperflatProgressionItems.ENDER_STAR);
                    item.setPosition(new Vec3d(player.getX(), player.getY(), player.getZ()));
                };
                totemEventRegister.invoke(null, "DRAGON_TOTEM_USE_EVENT", listener);
            }
            catch (Exception e) {
                SuperflatProgression.LOGGER.error("Couldn't register peaceful-progression compatibility", e);
            }
        }
    }
}
