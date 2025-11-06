package xen42.superflatprogression;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SuperflatProgressionDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(SuperflatProgressionRecipeGenerator::new);
		pack.addProvider(SuperflatProgressionModelGenerator::new);
		pack.addProvider(SuperflatProgressionLanguageProvider.English::new);
		SuperflatProgressionBlockTagGenerator blockTagProvider = pack.addProvider(SuperflatProgressionBlockTagGenerator::new);
		pack.addProvider((output, registries) -> new SuperflatProgressionItemTagGenerator(output, registries, blockTagProvider));
		pack.addProvider(SuperflatProgressionEntityLootTableGenerator::new);
		pack.addProvider(SuperflatProgressionBlockLootTableGenerator::new);
	}
}
