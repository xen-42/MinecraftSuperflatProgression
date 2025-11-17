package xen42.superflatprogression;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.predicate.StatePredicate.Builder;

public class SuperflatProgressionBlockLootTableGenerator extends FabricBlockLootTableProvider {

    protected SuperflatProgressionBlockLootTableGenerator(FabricDataOutput dataOutput,
            CompletableFuture<WrapperLookup> registryLookup) {
        super(dataOutput);
    }

    @Override
    public String getName() {
        return "SuperflatProgressionBlockLootTableGenerator";
    }

    @Override
    public void generate() {
        addDrop(SuperflatProgressionBlocks.CHARCOAL_BLOCK);
        addDrop(SuperflatProgressionBlocks.MAGIC_TORCH);
        addDrop(SuperflatProgressionBlocks.WALL_MAGIC_TORCH);
        addDrop(SuperflatProgressionBlocks.MAGIC_LANTERN);
        addDrop(SuperflatProgressionBlocks.SCROLL_CRAFTING);
        addDrop(SuperflatProgressionBlocks.GRINDER);
        addDrop(SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR);
        addDrop(SuperflatProgressionBlocks.DIRT_SLAB, slabDrops(SuperflatProgressionBlocks.DIRT_SLAB));
        addDrop(SuperflatProgressionBlocks.GRASS_SLAB, slabDropsWithSilkTouch(SuperflatProgressionBlocks.GRASS_SLAB, SuperflatProgressionBlocks.DIRT_SLAB));
    }

    public LootTable.Builder slabDropsWithSilkTouch(Block silkTouch, Block nonSilkTouchDrop) {
      return LootTable.builder()
	  	.pool(
			LootPool.builder()
				.rolls(ConstantLootNumberProvider.create(1.0F))
				.with(applyExplosionDecay(nonSilkTouchDrop, ItemEntry.builder(nonSilkTouchDrop).conditionally(WITHOUT_SILK_TOUCH).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))
					.conditionally(BlockStatePropertyLootCondition.builder(silkTouch).properties(Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE))))))
				.with(ItemEntry.builder(silkTouch).conditionally(WITH_SILK_TOUCH).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))
					.conditionally(BlockStatePropertyLootCondition.builder(silkTouch).properties(Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE)))))
			);
	}
}
