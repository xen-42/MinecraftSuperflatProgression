package xen42.superflatprogression;

import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public class SuperflatProgressionModelGenerator extends FabricModelProvider {

    public SuperflatProgressionModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(SuperflatProgressionBlocks.CHARCOAL_BLOCK);
		blockStateModelGenerator.registerTorch(SuperflatProgressionBlocks.MAGIC_TORCH, SuperflatProgressionBlocks.WALL_MAGIC_TORCH);
		blockStateModelGenerator.registerLantern(SuperflatProgressionBlocks.MAGIC_LANTERN);

		blockStateModelGenerator.registerParentedItemModel(SuperflatProgressionBlocks.SCROLL_CRAFTING, ModelIds.getBlockModelId(SuperflatProgressionBlocks.SCROLL_CRAFTING));
		blockStateModelGenerator.registerParentedItemModel(SuperflatProgressionBlocks.GRINDER, ModelIds.getBlockModelId(SuperflatProgressionBlocks.GRINDER));

		registerCubeTopSideBottom(SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR, blockStateModelGenerator);

		makeSlabOfAnotherBlockAll(Blocks.DIRT, SuperflatProgressionBlocks.DIRT_SLAB, blockStateModelGenerator);
		//makeGrassSlab(SuperflatProgressionBlocks.GRASS_SLAB, blockStateModelGenerator);
    }

	private void registerCubeTopSideBottom(Block block, BlockStateModelGenerator blockStateModelGenerator) {
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.TOP, TextureMap.getSubId(block, "_top"))
			.put(TextureKey.SIDE, TextureMap.getSubId(block, "_side"))
			.put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_bottom"));
        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(
                        block,
                        Models.CUBE_BOTTOM_TOP.upload(block, textureMap, blockStateModelGenerator.modelCollector)
                )
        );
	}

	private void makeSlabOfAnotherBlockAll(Block original, Block slab, BlockStateModelGenerator blockStateModelGenerator) {
		TextureMap textureMap = TextureMap.all(original);
		Identifier identifier = Models.SLAB.upload(slab, textureMap, blockStateModelGenerator.modelCollector);
		Identifier identifier2 = Models.SLAB_TOP.upload(slab, textureMap, blockStateModelGenerator.modelCollector);
		Identifier identifier3 = Models.CUBE_COLUMN.uploadWithoutVariant(slab, "_double", textureMap, blockStateModelGenerator.modelCollector);
		blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(slab, identifier, identifier2, identifier3));
	}

	private void makeGrassSlab(Block slab, BlockStateModelGenerator blockStateModelGenerator) {
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_side"))
			.put(TextureKey.TOP, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_top"))
			.put(TextureKey.END, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_end"))
			.put(TextureKey.BOTTOM, TextureMap.getSubId(Blocks.DIRT, ""));
		
		Identifier bottomSlab = Models.SLAB.upload(slab, textureMap, blockStateModelGenerator.modelCollector);
		Identifier topSlab = Models.SLAB_TOP.upload(slab, textureMap, blockStateModelGenerator.modelCollector);
		Identifier doubleSlab = Models.CUBE_BOTTOM_TOP.upload(slab, "_double", textureMap, blockStateModelGenerator.modelCollector);
		blockStateModelGenerator.blockStateCollector.accept(blockStateModelGenerator.createSlabBlockState(slab, bottomSlab, topSlab, doubleSlab));
	}
	
	private void makeScroll(Item scroll, TextureMap textureMap, ItemModelGenerator itemModelGenerator) {
		Models.GENERATED.upload(ModelIds.getItemModelId(scroll), textureMap, itemModelGenerator.writer);
	}

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(SuperflatProgressionItems.ENRICHED_BONEMEAL, Models.GENERATED);
        itemModelGenerator.register(SuperflatProgressionItems.FIRE_STARTER, Models.GENERATED);
        itemModelGenerator.register(SuperflatProgressionItems.ESSENCE, Models.GENERATED);
        itemModelGenerator.register(SuperflatProgressionItems.END_DUST, Models.GENERATED);
        itemModelGenerator.register(SuperflatProgressionItems.ENDER_STAR, Models.GENERATED);
        itemModelGenerator.register(SuperflatProgressionItems.PARCHMENT, Models.GENERATED);
        itemModelGenerator.register(SuperflatProgressionItems.DIAMOND_SHARD, Models.GENERATED);
        itemModelGenerator.register(SuperflatProgressionItems.PIXIE_SPAWN_EGG, Models.GENERATED);

        itemModelGenerator.register(SuperflatProgressionItems.BONE_AXE, Models.HANDHELD);
        itemModelGenerator.register(SuperflatProgressionItems.BONE_HOE, Models.HANDHELD);
        itemModelGenerator.register(SuperflatProgressionItems.BONE_SWORD, Models.HANDHELD);
        itemModelGenerator.register(SuperflatProgressionItems.BONE_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(SuperflatProgressionItems.BONE_PICKAXE, Models.HANDHELD);

		Identifier scrollTexture = new Identifier(SuperflatProgression.MOD_ID, "item/scroll_blank");
		TextureMap textureMap = new TextureMap().put(TextureKey.LAYER0, scrollTexture);

		for (var scroll : SuperflatProgressionItems.SCROLLS) {
			makeScroll(scroll.item(), textureMap, itemModelGenerator);
		}
    }

    @Override
    public String getName() {
        return "SuperflatProgressionModelGenerator";
    }
}
