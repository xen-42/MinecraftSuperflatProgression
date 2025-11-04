package xen42.superflatprogression.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import xen42.superflatprogression.SuperflatProgressionStatusEffects;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	@Inject(at = @At("RETURN"), method = "getLooting", cancellable = true)
	private static void getLooting(LivingEntity entity, CallbackInfoReturnable<Integer> info) {
        // Magic torch gives looting 3, when combined with actual looting sword lets you go up to looting 4
        if (entity.hasStatusEffect(SuperflatProgressionStatusEffects.LOOTING_EFFECT.value())) {
            info.setReturnValue(Math.max(info.getReturnValue() + 3, 4));
            info.cancel();
        }
	}
}
