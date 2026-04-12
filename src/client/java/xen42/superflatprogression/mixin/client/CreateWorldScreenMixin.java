package xen42.superflatprogression.mixin.client;

import java.util.Optional;
import java.util.OptionalLong;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @ModifyArg(
        method = "create(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/world/GeneratorOptionsHolder;Ljava/util/Optional;Ljava/util/OptionalLong;)V"
        ),
        index = 3
    )
    private static Optional<?> superflatprogression_useFlatPreset(Optional<?> original) {
        return Optional.of(WorldPresets.FLAT);
    }
}