package xen42.superflatprogression;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public class SuperflatProgressionTags {
    public class ItemTags {
        public static final TagKey<Item> PARCHMENTS = ofItem("parchments");
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
