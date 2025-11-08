package xen42.superflatprogression;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.ItemTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;

public class SuperflatProgressionItemTagGenerator extends ItemTagProvider {
	public SuperflatProgressionItemTagGenerator(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture,
			SuperflatProgressionBlockTagGenerator blockTagProvider) {
		super(output, registriesFuture, blockTagProvider);
	}

	@Override
	public String getName() {
		return "SuperflatProgressionItemTagGenerator";
	}
	
	@Override
	protected void configure(WrapperLookup wrapperLookup) {
		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PARCHMENTS)
			.add(SuperflatProgressionItems.PARCHMENT)
			.add(Items.PAPER);
		
		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.DIAMOND_GEAR)
			.add(Items.DIAMOND_AXE)
			.add(Items.DIAMOND_HOE)
			.add(Items.DIAMOND_PICKAXE)
			.add(Items.DIAMOND_SHOVEL)
			.add(Items.DIAMOND_SWORD)
			.add(Items.DIAMOND_BOOTS)
			.add(Items.DIAMOND_CHESTPLATE)
			.add(Items.DIAMOND_HELMET)
			.add(Items.DIAMOND_LEGGINGS)
			.add(Items.DIAMOND_HORSE_ARMOR);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.ICES)
			.add(Items.ICE)
			.add(Items.BLUE_ICE)
			.add(Items.PACKED_ICE);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.BONE_GEAR)
			.add(SuperflatProgressionItems.BONE_PICKAXE)
			.add(SuperflatProgressionItems.BONE_AXE)
			.add(SuperflatProgressionItems.BONE_SHOVEL)
			.add(SuperflatProgressionItems.BONE_HOE)
			.add(SuperflatProgressionItems.BONE_SWORD);
		
		this.getOrCreateTagBuilder(ItemTags.PICKAXES).add(SuperflatProgressionItems.BONE_PICKAXE);
		this.getOrCreateTagBuilder(ItemTags.AXES).add(SuperflatProgressionItems.BONE_AXE);
		this.getOrCreateTagBuilder(ItemTags.SHOVELS).add(SuperflatProgressionItems.BONE_SHOVEL);
		this.getOrCreateTagBuilder(ItemTags.HOES).add(SuperflatProgressionItems.BONE_HOE);
		this.getOrCreateTagBuilder(ItemTags.SWORDS).add(SuperflatProgressionItems.BONE_SWORD);
    }
}
