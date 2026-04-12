package xen42.superflatprogression;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SuperflatProgressionPotions {

    public static final Potion MAGIC_TORCH = register("magic_torch", new StatusEffectInstance[] { 
        new StatusEffectInstance(SuperflatProgressionStatusEffects.MAGIC_TORCH_EFFECT, 600) 
    });
    public static final Potion WARP = register("warp", new StatusEffectInstance[] { 
        new StatusEffectInstance(SuperflatProgressionStatusEffects.WARP_EFFECT, 200) 
    });

	public static Potion register(String name, StatusEffectInstance... effects) {
		var id = Identifier.of(SuperflatProgression.MOD_ID, name);
		var potion = new Potion(name, effects);
		return Registry.register(Registries.POTION, id, potion);
	}

    public static void initialize() {
    	BrewingRecipeRegistry.registerPotionRecipe(
            Potions.AWKWARD,
            SuperflatProgressionItems.ESSENCE,
            MAGIC_TORCH);
        BrewingRecipeRegistry.registerPotionRecipe(
            Potions.AWKWARD, 
            SuperflatProgressionItems.END_DUST, 
            WARP);
    }
}
