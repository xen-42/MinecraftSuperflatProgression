package xen42.superflatprogression.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import xen42.superflatprogression.SuperflatProgressionItems;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {
    @Inject(at = @At("HEAD"), method = "dragonKilled")
    private void dragonKilled(EnderDragonEntity dragon, CallbackInfo info) {
        if (!dragon.getWorld().isClient) {
            var serverWorld = (ServerWorld) dragon.getWorld();
            var killer = dragon.getAttacker();
            if (killer == null || !(killer instanceof PlayerEntity)) {
                killer = serverWorld.getRandomAlivePlayer();
            }
            var item = killer.dropItem(SuperflatProgressionItems.ENDER_STAR);
            item.setPosition(new Vec3d(killer.getX(), killer.getY(), killer.getZ()));
        }
    }
}
