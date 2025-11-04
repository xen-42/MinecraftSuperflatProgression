package xen42.superflatprogression;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class SuperflatProgressionPotions {
    public static final RegistryEntry<Potion> MAGIC_TORCH = register("magic_torch", 
        new Potion("magic_torch", new StatusEffectInstance[] { new StatusEffectInstance(SuperflatProgressionStatusEffects.MAGIC_TORCH_EFFECT.value(), 600) }));

    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return (RegistryEntry<Potion>)Registry.registerReference(Registries.POTION, Identifier.of(SuperflatProgression.MOD_ID, name), potion);
    }

    public static void initialize() {
    	BrewingRecipeRegistry.registerPotionRecipe(
                Potions.AWKWARD,
                SuperflatProgressionItems.ESSENCE,
                MAGIC_TORCH.value());
    }
}
