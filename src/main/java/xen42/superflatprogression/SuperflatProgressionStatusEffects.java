package xen42.superflatprogression;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class SuperflatProgressionStatusEffects {
    private static RegistryEntry<StatusEffect> registerStatusEffect(String id, StatusEffect statusEffect) {
		return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SuperflatProgression.MOD_ID, id), statusEffect);
	}
	public static RegistryEntry<StatusEffect> MAGIC_TORCH_EFFECT;
    
    public static void initialize() {
		MAGIC_TORCH_EFFECT = registerStatusEffect("magic_torch_effect",
			(new SuperflatProgressionStatusEffects.CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 8388736)));
    }

    public static class CustomStatusEffect extends StatusEffect {
        protected CustomStatusEffect(StatusEffectCategory category, int color) {
            super(category, color);
        }
    }
}
