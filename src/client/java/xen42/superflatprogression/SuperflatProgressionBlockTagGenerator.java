package xen42.superflatprogression;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;

public class SuperflatProgressionBlockTagGenerator extends BlockTagProvider {
    public SuperflatProgressionBlockTagGenerator(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "PeacefulModBlockTagGenerator";
    }
    
    @Override
    protected void configure(WrapperLookup wrapperLookup) {
        this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
            .add(SuperflatProgressionBlocks.CHARCOAL_BLOCK)
            .add(SuperflatProgressionBlocks.MAGIC_LANTERN)
            .add(SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR)
            .add(Blocks.END_PORTAL_FRAME)
            .add(SuperflatProgressionBlocks.GRINDER);

        this.getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
            .add(SuperflatProgressionBlocks.SCROLL_CRAFTING);

        this.getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
            .add(SuperflatProgressionBlocks.DIRT_SLAB)
            .add(SuperflatProgressionBlocks.GRASS_SLAB);

        this.getOrCreateTagBuilder(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_ON_SOUL_SAND)
        .add(Blocks.NETHER_WART);

        this.getOrCreateTagBuilder(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_MUSHROOM)
            .add(Blocks.RED_MUSHROOM)
            .add(Blocks.BROWN_MUSHROOM);
        
        this.getOrCreateTagBuilder(SuperflatProgressionTags.BlockTags.MUSHROOM_BLOCK_PLACEABLE)
            .add(Blocks.STONE)
            .add(Blocks.DIRT)
            .add(Blocks.MYCELIUM)
            .add(Blocks.COBBLESTONE);

        this.getOrCreateTagBuilder(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_ON_GRASS)
            .add(Blocks.GRASS)
            .add(Blocks.TALL_GRASS)
            .add(Blocks.FERN)
            .add(Blocks.OAK_SAPLING)
            .add(Blocks.SUNFLOWER)
            .add(Blocks.ROSE_BUSH)
            .add(Blocks.LILAC)
            .add(Blocks.PEONY)
            .addOptional(Identifier.of("peaceful-items", "flax_crop"));

        this.getOrCreateTagBuilder(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_ON_SAND)
            .add(Blocks.DEAD_BUSH)
            .add(Blocks.SUGAR_CANE)
            .add(Blocks.CACTUS);

        this.getOrCreateTagBuilder(SuperflatProgressionTags.BlockTags.ENRICHED_BONE_MEAL_UNDER_WATER)
            .add(Blocks.SEAGRASS)
            .add(Blocks.SEA_PICKLE)
            .add(Blocks.FIRE_CORAL_BLOCK)
            .add(Blocks.HORN_CORAL_BLOCK)
            .add(Blocks.TUBE_CORAL_BLOCK)
            .add(Blocks.BRAIN_CORAL_BLOCK)
            .add(Blocks.BUBBLE_CORAL_BLOCK)
            .add(Blocks.FIRE_CORAL_FAN)
            .add(Blocks.HORN_CORAL_FAN)
            .add(Blocks.TUBE_CORAL_FAN)
            .add(Blocks.BRAIN_CORAL_FAN)
            .add(Blocks.BUBBLE_CORAL_FAN)
            .add(Blocks.KELP);
    }
}