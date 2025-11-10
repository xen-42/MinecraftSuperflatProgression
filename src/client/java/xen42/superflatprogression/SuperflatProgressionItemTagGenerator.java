package xen42.superflatprogression;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.ItemTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

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

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_GREEN)
			.add(Items.CACTUS);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_LIME)
			.add(Items.SEA_PICKLE);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_WHITE)
			.add(Items.BONE_MEAL)
			.add(Items.LILY_OF_THE_VALLEY);
		
		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_GRAY)
			.addOptional(Identifier.of("peaceful-items", "breeze_coral"));

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_LIGHT_GRAY)
			.add(Items.AZURE_BLUET)
			.add(Items.OXEYE_DAISY)
			.add(Items.WHITE_TULIP);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_BLACK)
			.add(Items.INK_SAC)
			.add(Items.WITHER_ROSE);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_BROWN)
			.add(Items.COCOA_BEANS);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_RED)
			.add(Items.POPPY)
			.add(Items.RED_TULIP)
			.add(Items.ROSE_BUSH)
			.add(Items.BEETROOT);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_ORANGE)
			.add(Items.ORANGE_TULIP)
			.add(Items.TORCHFLOWER)
			.addOptionalTag(Identifier.of("peaceful-items", "blaze_coral"));

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_YELLOW)
			.add(Items.DANDELION)
			.add(Items.SUNFLOWER)
			.addOptional(Identifier.of("peaceful-items", "sulphur"));

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_CYAN)
			.add(Items.PITCHER_PLANT);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_LIGHT_BLUE)
			.add(Items.BLUE_ORCHID);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_BLUE)
			.add(Items.LAPIS_LAZULI)
			.add(Items.CORNFLOWER);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_MAGENTA)
			.add(Items.ALLIUM)
			.add(Items.LILAC);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_PINK)
			.add(Items.PINK_TULIP)
			.add(Items.PEONY)
			.add(Items.PINK_PETALS);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_PURPLE)
			.add(SuperflatProgressionItems.ESSENCE)
			.addOptionalTag(Identifier.of("peaceful-items", "guano"));
		
		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_BONE_MEAL)
			.add(Items.BONE)
			.addTag(SuperflatProgressionTags.ItemTags.BONE_GEAR);

		this.getOrCreateTagBuilder(SuperflatProgressionTags.ItemTags.PULVERIZES_INTO_PRISMARINE_SHARD)
			.add(Items.PRISMARINE_BRICKS)
			.add(Items.DARK_PRISMARINE)
			.add(Items.PRISMARINE);
		
		this.getOrCreateTagBuilder(ItemTags.PICKAXES).add(SuperflatProgressionItems.BONE_PICKAXE);
		this.getOrCreateTagBuilder(ItemTags.AXES).add(SuperflatProgressionItems.BONE_AXE);
		this.getOrCreateTagBuilder(ItemTags.SHOVELS).add(SuperflatProgressionItems.BONE_SHOVEL);
		this.getOrCreateTagBuilder(ItemTags.HOES).add(SuperflatProgressionItems.BONE_HOE);
		this.getOrCreateTagBuilder(ItemTags.SWORDS).add(SuperflatProgressionItems.BONE_SWORD);
    }
}
