package xen42.superflatprogression.compat;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.SuperflatProgression;

// Stubs for recipe data generation
public final class ModCompatDatagen {
    public static final Item CANADIAN_CONTENT_FLOUR = registerStubItemIfMissing(Identifier.of(SuperflatProgression.CANADIAN_CONTENT, "flour"));

    public static Item registerStubItemIfMissing(Identifier id) {
        if (!SuperflatProgression.isDatagenRunning()) {
            return null;
        }
        
        Item existing = Registries.ITEM.get(id);
        if (existing != Items.AIR) {
            return existing;
        }

        return Registry.register(Registries.ITEM, id, new Item(new Item.Settings()));
    }

    public static Block registerStubBlockIfMissing(Identifier id) {
        if (!SuperflatProgression.isDatagenRunning()) {
            return null;
        }
        
        Block existing = Registries.BLOCK.get(id);
        if (existing != Blocks.AIR) {
            return existing;
        }

        return Registry.register(Registries.BLOCK, id, new Block(AbstractBlock.Settings.create()));
    }

    public static Item registerStubBlockItemIfMissing(Identifier id, Block block) {
        if (!SuperflatProgression.isDatagenRunning()) {
            return null;
        }
        
        Item existing = Registries.ITEM.get(id);
        if (existing != Items.AIR) {
            return existing;
        }

        return Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
    }
}