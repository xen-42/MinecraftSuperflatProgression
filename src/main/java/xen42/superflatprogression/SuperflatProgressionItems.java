package xen42.superflatprogression;

import java.util.function.Function;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Direction;
import xen42.superflatprogression.items.DispensibleSpawnEggItem;
import xen42.superflatprogression.items.EnrichedBoneMealItem;
import xen42.superflatprogression.items.FireStarterItem;
import xen42.superflatprogression.items.ScrollItem;

public class SuperflatProgressionItems {
    public static final Item ESSENCE = register("essence", Item::new, new Item.Settings());
    public static final Item DIAMOND_SHARD = register("diamond_shard", Item::new, new Item.Settings());
    public static final Item PARCHMENT = register("parchment", Item::new, new Item.Settings());
	public static final Item ENRICHED_BONEMEAL = register("enriched_bonemeal", EnrichedBoneMealItem::new, new Item.Settings());
	public static final Item FIRE_STARTER = register("fire_starter", FireStarterItem::new, new Item.Settings());
	public static final Item MAGIC_TORCH = register("magic_torch", (settings) -> new VerticallyAttachableBlockItem(SuperflatProgressionBlocks.MAGIC_TORCH,
		SuperflatProgressionBlocks.WALL_MAGIC_TORCH, settings, Direction.DOWN), new Item.Settings());
	public static final Item PIXIE_SPAWN_EGG = register("pixie_spawn_egg", (settings) -> 
        new DispensibleSpawnEggItem(SuperflatProgression.PIXIE_ENTITY, 0x6F4B6F, 0x2B1E2B, settings), new Item.Settings());

    public static final Item SCROLL_RAIN = register("scroll_rain", (settings) ->
		new ScrollItem(settings, (ServerPlayerEntity user) -> {
			var world = (ServerWorld)(user.getWorld());
			var duration = ServerWorld.RAIN_WEATHER_DURATION_PROVIDER.get(world.getRandom());
			world.setWeather(0, duration, true, false);
		})
	, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

    public static final Item SCROLL_THUNDER = register("scroll_thunder", (settings) ->
		new ScrollItem(settings, (ServerPlayerEntity user) -> {
			var world = (ServerWorld)(user.getWorld());
			var duration = ServerWorld.THUNDER_WEATHER_DURATION_PROVIDER.get(world.getRandom());
			world.setWeather(0, duration, true, true);
		})
	, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

	public static final Item SCROLL_CLEAR_WEATHER = register("scroll_clear_weather", (settings) ->
		new ScrollItem(settings, (ServerPlayerEntity user) -> {
			var world = (ServerWorld)(user.getWorld());
			var duration = ServerWorld.CLEAR_WEATHER_DURATION_PROVIDER.get(world.getRandom());
			world.setWeather(duration, 0, false, false);
		})
	, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

    public static final Item SCROLL_TRADE = register("scroll_trade", (settings) ->
		new ScrollItem(settings, (ServerPlayerEntity user) -> {
			// Try to spawn 10 times
			var i = 0;
			while (!WanderingTraderHelper.trySpawn(user) && i < 10) {
				i++;
			}
		})
	, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));


    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
            itemGroup.add(ESSENCE);
            itemGroup.add(PARCHMENT);
            itemGroup.add(DIAMOND_SHARD);
        }); 

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> {
            itemGroup.add(ENRICHED_BONEMEAL);
            itemGroup.add(FIRE_STARTER);
            itemGroup.add(SCROLL_RAIN);
            itemGroup.add(SCROLL_THUNDER);
            itemGroup.add(SCROLL_CLEAR_WEATHER);
            itemGroup.add(SCROLL_TRADE);
        });

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register((itemGroup) -> {
            itemGroup.add(SuperflatProgressionBlocks.CHARCOAL_BLOCK.asItem());
            itemGroup.add(SuperflatProgressionBlocks.DIRT_SLAB.asItem());
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register((itemGroup) -> {
            itemGroup.add(PIXIE_SPAWN_EGG);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> {
            itemGroup.add(MAGIC_TORCH);
            itemGroup.add(SuperflatProgressionBlocks.MAGIC_LANTERN);
            itemGroup.add(SuperflatProgressionBlocks.SCROLL_CRAFTING);
            itemGroup.add(SuperflatProgressionBlocks.GRINDER);
		});

		FuelRegistry.INSTANCE.add(SuperflatProgressionBlocks.CHARCOAL_BLOCK, 16000);

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
