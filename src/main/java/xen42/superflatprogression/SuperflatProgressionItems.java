package xen42.superflatprogression;

import java.util.function.Function;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class SuperflatProgressionItems {
    public static final Item ESSENCE = register("essence", Item::new, new Item.Settings());

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> {
            itemGroup.add(ESSENCE);
        });
    }

	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		// Create the item key.
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SuperflatProgression.MOD_ID, name));

		// Create the item instance.
		Item item = itemFactory.apply(settings);

		// Register the item.
		Registry.register(Registries.ITEM, itemKey, item);

		return item;
	}
}
