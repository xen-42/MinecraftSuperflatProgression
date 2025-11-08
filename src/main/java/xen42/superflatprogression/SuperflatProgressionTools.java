package xen42.superflatprogression;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class SuperflatProgressionTools {
    public static final ToolMaterial BONE = new ToolMaterial() {
        @Override
        public int getDurability() {
            // Between stone and iron
            return 200;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            // Between stone and iron
            return 5f;
        }

        @Override
        public float getAttackDamage() {
            // Between stone and iron
            return 1.5f;
        }

        @Override
        public int getMiningLevel() {
            return MiningLevels.STONE;
        }

        @Override
        public int getEnchantability() {
            // Between stone and iron
            return 10;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(Items.BONE);
        }
    };
}
