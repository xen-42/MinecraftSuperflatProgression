package xen42.superflatprogression.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;
import xen42.superflatprogression.SuperflatProgressionItems;

@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin {
    @Inject(at = @At("HEAD"), method = "kill")
    private void kill(CallbackInfo info) {
        var enderDragon = (EnderDragonEntity) (Object) this;
        if (!enderDragon.getWorld().isClient) {
            var item = enderDragon.dropItem(SuperflatProgressionItems.ENDER_STAR);
            item.setPosition(new Vec3d(enderDragon.getX(), enderDragon.getY(), enderDragon.getZ()));
        }
    }
}
