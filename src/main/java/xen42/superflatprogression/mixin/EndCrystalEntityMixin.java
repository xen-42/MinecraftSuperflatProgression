package xen42.superflatprogression.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import xen42.superflatprogression.SuperflatProgressionBlocks;
import xen42.superflatprogression.blocks.EndPortalFrameGenerator;

@Mixin(EndCrystalEntity.class)
public class EndCrystalEntityMixin {
    @Inject(at = @At("HEAD"), method = "damage")
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        var crystal = (EndCrystalEntity)(Object)this;
        var world = crystal.getWorld();

        if (!crystal.isInvulnerableTo(source) && !crystal.isRemoved() && !world.isClient) {
            var pos = crystal.getBlockPos().down();
            var blockState = crystal.getWorld().getBlockState(pos);
            if (blockState.isOf(SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR)) {
                EndPortalFrameGenerator.tryCreateEndPortalFrame((ServerWorld)world, blockState, pos);
            }
        }
    }
}
