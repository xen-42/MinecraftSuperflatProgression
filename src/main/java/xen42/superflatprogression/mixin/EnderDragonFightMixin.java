package xen42.superflatprogression.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.util.math.BlockPos;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {
    @ModifyVariable(
        method = "<init>(Lnet/minecraft/server/world/ServerWorld;JLnet/minecraft/entity/boss/dragon/EnderDragonFight$Data;Lnet/minecraft/util/math/BlockPos;)V",
        at = @At("HEAD"), argsOnly = true)
    private static BlockPos modifyOrigin(BlockPos origin) {
        return new BlockPos(0, 67, 0);
    }
}