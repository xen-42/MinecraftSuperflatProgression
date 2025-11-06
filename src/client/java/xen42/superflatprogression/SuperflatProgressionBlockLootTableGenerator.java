package xen42.superflatprogression;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

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
    }
}
