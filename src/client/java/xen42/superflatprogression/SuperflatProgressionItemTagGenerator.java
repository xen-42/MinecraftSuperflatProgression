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
    }
}
