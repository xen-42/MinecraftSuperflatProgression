package xen42.superflatprogression;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public class SuperflatProgressionTags {
    public static ArrayList<Block> GetBlocksInTag(TagKey<Block> tag) {
       return new ArrayList<Block>(Registries.BLOCK.getEntryList(tag).stream().flatMap(x -> x.stream()).map(entry -> entry.value()).toList());
    }

    public class ItemTags {
        public static final TagKey<Item> PARCHMENTS = ofItem("parchments");
		public static final TagKey<Item> DIAMOND_GEAR = ofItem("diamond_gear");
		public static final TagKey<Item> BONE_GEAR = ofItem("bone_gear");
		public static final TagKey<Item> ICES = ofItem("ices");
		public static final TagKey<Item> MAGIC_SCROLLS = ofItem("magic_scrolls");

		public static final TagKey<Item> PULVERIZES_INTO_GREEN = ofItem("pulverizes_into_green");
		public static final TagKey<Item> PULVERIZES_INTO_LIME = ofItem("pulverizes_into_lime");
		public static final TagKey<Item> PULVERIZES_INTO_WHITE = ofItem("pulverizes_into_white");
		public static final TagKey<Item> PULVERIZES_INTO_GRAY = ofItem("pulverizes_into_gray");
		public static final TagKey<Item> PULVERIZES_INTO_LIGHT_GRAY = ofItem("pulverizes_into_light_gray");
		public static final TagKey<Item> PULVERIZES_INTO_BLACK = ofItem("pulverizes_into_black");
		public static final TagKey<Item> PULVERIZES_INTO_BROWN = ofItem("pulverizes_into_brown");
		public static final TagKey<Item> PULVERIZES_INTO_RED = ofItem("pulverizes_into_red");
		public static final TagKey<Item> PULVERIZES_INTO_ORANGE = ofItem("pulverizes_into_orange");
		public static final TagKey<Item> PULVERIZES_INTO_YELLOW = ofItem("pulverizes_into_yellow");
		public static final TagKey<Item> PULVERIZES_INTO_CYAN = ofItem("pulverizes_into_cyan");
		public static final TagKey<Item> PULVERIZES_INTO_LIGHT_BLUE = ofItem("pulverizes_into_light_blue");
		public static final TagKey<Item> PULVERIZES_INTO_BLUE = ofItem("pulverizes_into_blue");
		public static final TagKey<Item> PULVERIZES_INTO_MAGENTA = ofItem("pulverizes_into_magenta");
		public static final TagKey<Item> PULVERIZES_INTO_PINK = ofItem("pulverizes_into_pink");
		public static final TagKey<Item> PULVERIZES_INTO_PURPLE = ofItem("pulverizes_into_purple");
		public static final TagKey<Item> PULVERIZES_INTO_BONE_MEAL = ofItem("pulverizes_into_bone_meal");
		public static final TagKey<Item> PULVERIZES_INTO_PRISMARINE_SHARD = ofItem("pulverizes_into_prismarine_shard");

		public static final TagKey<Item> ENRICHED_BONE_MEAL_ON_SAND = ofItem("enriched_bone_meal_on_sand");
		public static final TagKey<Item> ENRICHED_BONE_MEAL_ON_GRASS = ofItem("enriched_bone_meal_on_grass");
		public static final TagKey<Item> ENRICHED_BONE_MEAL_ON_SOUL_SAND = ofItem("enriched_bone_meal_on_soul_sand");
		public static final TagKey<Item> ENRICHED_BONE_MEAL_UNDER_WATER = ofItem("enriched_bone_meal_under_water");
		public static final TagKey<Item> ENRICHED_BONE_MEAL_MUSHROOM = ofItem("enriched_bone_meal_on_stone");
		public static final TagKey<Item> MUSHROOM_BLOCK_PLACEABLE = ofItem("mushroom_block_placeable");
    }

	public class BlockTags {
		public static final TagKey<Block> ENRICHED_BONE_MEAL_ON_SAND = ofBlock("enriched_bone_meal_on_sand");
		public static final TagKey<Block> ENRICHED_BONE_MEAL_ON_GRASS = ofBlock("enriched_bone_meal_on_grass");
		public static final TagKey<Block> ENRICHED_BONE_MEAL_ON_SOUL_SAND = ofBlock("enriched_bone_meal_on_soul_sand");
		public static final TagKey<Block> ENRICHED_BONE_MEAL_UNDER_WATER = ofBlock("enriched_bone_meal_under_water");
		public static final TagKey<Block> ENRICHED_BONE_MEAL_MUSHROOM = ofBlock("enriched_bone_meal_on_stone");
		public static final TagKey<Block> MUSHROOM_BLOCK_PLACEABLE = ofBlock("mushroom_block_placeable");
	}

    public static TagKey<Block> ofBlock(String name) {
		return TagKey.of(RegistryKeys.BLOCK, Identifier.of(SuperflatProgression.MOD_ID, name));
	}

	public static TagKey<Item> ofItem(String name) {
		return TagKey.of(RegistryKeys.ITEM, Identifier.of(SuperflatProgression.MOD_ID, name));
	}
	
	public static TagKey<EntityType<?>> ofEntity(String name) {
		return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(SuperflatProgression.MOD_ID, name));
	}
	
	public static TagKey<Structure> ofStructure(String name) {
		return TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(SuperflatProgression.MOD_ID, name));
	}
}
