package xen42.superflatprogression;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.ItemTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

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
    }
}
