package xen42.superflatprogression.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.mob.EndermanEntity;
import xen42.superflatprogression.SuperflatProgression;

@Mixin(EndermanEntity.PlaceBlockGoal.class)
public class EndermanPlaceBlockGoalMixin {
	@Inject(at = @At("HEAD"), method = "canStart", cancellable = true)
	private void canStart(CallbackInfoReturnable<Boolean> info) {
		var endermanGoal = (EndermanEntity.PlaceBlockGoal) (Object) this;
		if (endermanGoal.enderman.getDataTracker().get(SuperflatProgression.ENDERMAN_CANNOT_DROP)) {
			info.setReturnValue(false);
			info.cancel();
		}
	}
}
