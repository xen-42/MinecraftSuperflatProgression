package xen42.superflatprogression.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgression;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {
    @Inject(at = @At("RETURN"), method = "<init>")
    private void onEndermanSpawned(EntityType<? extends EndermanEntity> entityType, World world, CallbackInfo info) {
        var enderman = (EndermanEntity) (Object) this;
        if (world.getRandom().nextFloat() < 0.05) {
            var possibleBlocks = List.of(Blocks.END_STONE, Blocks.WARPED_NYLIUM, Blocks.CRIMSON_NYLIUM);
            enderman.setCarriedBlock(
                    possibleBlocks.get(world.getRandom().nextInt(possibleBlocks.size() - 1)).getDefaultState());
            enderman.getDataTracker().set(SuperflatProgression.ENDERMAN_CANNOT_DROP, true);
        }
    }

    @Inject(at = @At("RETURN"), method = "cannotDespawn", cancellable = true)
    public void cannotDespawn(CallbackInfoReturnable<Boolean> info) {
        var enderman = (EndermanEntity) (Object) this;
        // Like base class but if its holding an item that we spawned it with let it despawn
        if (info.getReturnValue()) {
            info.setReturnValue(enderman.hasVehicle() || (enderman.getCarriedBlock() != null && !enderman.getDataTracker().get(SuperflatProgression.ENDERMAN_CANNOT_DROP)));
            info.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    public void initDataTracker(CallbackInfo info) {
        var enderman = ((EndermanEntity) (Object) this);

        // When we have an enderman spawning in with an item they must never place it
        // down
        // Can only be acquired by killing them
        enderman.getDataTracker().startTracking(SuperflatProgression.ENDERMAN_CANNOT_DROP, false);
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        var enderman = ((EndermanEntity) (Object) this);

        nbt.putBoolean("CannotDrop", enderman.getDataTracker().get(SuperflatProgression.ENDERMAN_CANNOT_DROP));
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        var enderman = ((EndermanEntity) (Object) this);

        enderman.getDataTracker().set(SuperflatProgression.ENDERMAN_CANNOT_DROP, nbt.getBoolean("CannotDrop"));
    }
}
