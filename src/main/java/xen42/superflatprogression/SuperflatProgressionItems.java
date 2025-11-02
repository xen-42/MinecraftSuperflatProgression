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
    public static final Item PARCHMENT = register("parchment", Item::new, new Item.Settings());
	public static final Item ENRICHED_BONEMEAL = register("enriched_bonemeal", Item::new, new Item.Settings());

    public static final Item SCROLL_RAIN = register("scroll_rain", Item::new, new Item.Settings());
    public static final Item SCROLL_THUNDER = register("scroll_thunder", Item::new, new Item.Settings());
    public static final Item SCROLL_TRADE = register("scroll_trade", Item::new, new Item.Settings());


    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
            itemGroup.add(ESSENCE);
            itemGroup.add(PARCHMENT);
        });

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> {
            itemGroup.add(ENRICHED_BONEMEAL);
            itemGroup.add(SCROLL_RAIN);
            itemGroup.add(SCROLL_THUNDER);
            itemGroup.add(SCROLL_TRADE);
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
