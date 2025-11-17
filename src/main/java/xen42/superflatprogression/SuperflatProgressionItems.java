package xen42.superflatprogression;

import java.util.function.Consumer;
import java.util.function.Function;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.NetherStarItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
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
    public static final Item END_DUST = register("end_dust", Item::new, new Item.Settings());
    public static final Item ENDER_STAR = register("ender_star", NetherStarItem::new, new Item.Settings().rarity(Rarity.UNCOMMON).fireproof());
    public static final Item DIAMOND_SHARD = register("diamond_shard", Item::new, new Item.Settings());
    public static final Item PARCHMENT = register("parchment", Item::new, new Item.Settings());
	public static final Item ENRICHED_BONEMEAL = register("enriched_bonemeal", EnrichedBoneMealItem::new, new Item.Settings());
	public static final Item FIRE_STARTER = register("fire_starter", FireStarterItem::new, new Item.Settings());
	public static final Item MAGIC_TORCH = register("magic_torch", (settings) -> new VerticallyAttachableBlockItem(SuperflatProgressionBlocks.MAGIC_TORCH,
		SuperflatProgressionBlocks.WALL_MAGIC_TORCH, settings, Direction.DOWN), new Item.Settings());
	public static final Item PIXIE_SPAWN_EGG = register("pixie_spawn_egg", (settings) -> 
        new DispensibleSpawnEggItem(SuperflatProgression.PIXIE_ENTITY, 0x6F4B6F, 0x2B1E2B, settings), new Item.Settings());

    public static final Item SCROLL_RAIN = registerScroll("scroll_rain",  (ServerPlayerEntity user) -> {
			var world = (ServerWorld)(user.getWorld());
			var duration = ServerWorld.RAIN_WEATHER_DURATION_PROVIDER.get(world.getRandom());
			world.setWeather(0, duration, true, false);
		});

    public static final Item SCROLL_THUNDER = registerScroll("scroll_thunder", (ServerPlayerEntity user) -> {
			var world = (ServerWorld)(user.getWorld());
			var duration = ServerWorld.THUNDER_WEATHER_DURATION_PROVIDER.get(world.getRandom());
			world.setWeather(0, duration, true, true);
		});

	public static final Item SCROLL_CLEAR_WEATHER = registerScroll("scroll_clear_weather", (ServerPlayerEntity user) -> {
			var world = (ServerWorld)(user.getWorld());
			var duration = ServerWorld.CLEAR_WEATHER_DURATION_PROVIDER.get(world.getRandom());
			world.setWeather(duration, 0, false, false);
		});

    public static final Item SCROLL_TRADE = registerScroll("scroll_trade", MobSpawnerHelper::spawnWanderingTrader);
    public static final Item SCROLL_PIG = registerScroll("scroll_pig", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.PIG));
    public static final Item SCROLL_COW = registerScroll("scroll_cow", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.COW));
    public static final Item SCROLL_CHICKEN = registerScroll("scroll_chicken", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.CHICKEN));
    public static final Item SCROLL_SHEEP = registerScroll("scroll_sheep", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.SHEEP));
    public static final Item SCROLL_CAT = registerScroll("scroll_cat", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.CAT));
    public static final Item SCROLL_WOLF = registerScroll("scroll_wolf", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.WOLF));

    public static final Item SCROLL_ZOMBIE = registerScroll("scroll_zombie", (ServerPlayerEntity user) -> 
		MobSpawnerHelper.spawnMob(user, user.getWorld().random.nextFloat() < 0.1 ? EntityType.ZOMBIE_VILLAGER : EntityType.ZOMBIE));
    public static final Item SCROLL_SKELETON = registerScroll("scroll_skeleton", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.SKELETON));
    public static final Item SCROLL_WITCH = registerScroll("scroll_witch", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.WITCH));
    public static final Item SCROLL_ENDERMAN = registerScroll("scroll_enderman", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.ENDERMAN));
    public static final Item SCROLL_SLIME = registerScroll("scroll_slime", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.SLIME));
    public static final Item SCROLL_MAGMA_CUBE = registerScroll("scroll_magma_cube", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.MAGMA_CUBE));
    public static final Item SCROLL_BLAZE = registerScroll("scroll_blaze", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.BLAZE));
    public static final Item SCROLL_SPIDER = registerScroll("scroll_spider", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, EntityType.SPIDER));
    
	//public static final Item SCROLL_BEAVER = registerScroll("scroll_beaver", (ServerPlayerEntity user) -> MobSpawnerHelper.spawnMob(user, Registries.ENTITY_TYPE.get(Identifier.of("canadamod", "beaver"))), "canadamod");

	private static final Item registerScroll(String name, Consumer<ServerPlayerEntity> onUse) {
		return registerScroll(name, onUse, null);
	}

	private static final Item registerScroll(String name, Consumer<ServerPlayerEntity> onUse, String optionalModID) {
		return register(name, (settings) -> new ScrollItem(settings, onUse)
		, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON), optionalModID);
	}

	public static final Item BONE_SWORD = register("bone_sword", (settings) ->
		new SwordItem(SuperflatProgressionTools.BONE, 3, -2.4f, settings),
		new Item.Settings().maxCount(1));

	public static final Item BONE_SHOVEL = register("bone_shovel", (settings) ->
		new ShovelItem(SuperflatProgressionTools.BONE, 1.5f, -3f, settings),
		new Item.Settings().maxCount(1));

	public static final Item BONE_PICKAXE = register("bone_pickaxe", (settings) ->
		new PickaxeItem(SuperflatProgressionTools.BONE, 1, -2.8f, settings),
		new Item.Settings().maxCount(1));

	public static final Item BONE_AXE = register("bone_axe", (settings) ->
		new AxeItem(SuperflatProgressionTools.BONE, 6, -3.1f, settings),
		new Item.Settings().maxCount(1));

	public static final Item BONE_HOE = register("bone_hoe", (settings) ->
		new HoeItem(SuperflatProgressionTools.BONE, -2, -1f, settings),
		new Item.Settings().maxCount(1));

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
            itemGroup.add(ESSENCE);
            itemGroup.add(END_DUST);
            itemGroup.add(ENDER_STAR);
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

			itemGroup.add(SCROLL_PIG);
			itemGroup.add(SCROLL_COW);
			itemGroup.add(SCROLL_CHICKEN);
			itemGroup.add(SCROLL_SHEEP);
			itemGroup.add(SCROLL_CAT);
			itemGroup.add(SCROLL_WOLF);

			itemGroup.add(SCROLL_ZOMBIE);
			itemGroup.add(SCROLL_SKELETON);
			itemGroup.add(SCROLL_WITCH);
			itemGroup.add(SCROLL_ENDERMAN);
			itemGroup.add(SCROLL_SLIME);
			itemGroup.add(SCROLL_MAGMA_CUBE);
			itemGroup.add(SCROLL_BLAZE);
			itemGroup.add(SCROLL_SPIDER);
			
            itemGroup.add(BONE_SHOVEL);
            itemGroup.add(BONE_PICKAXE);
            itemGroup.add(BONE_AXE);
            itemGroup.add(BONE_HOE);
        });

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> {
			itemGroup.add(BONE_SWORD);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register((itemGroup) -> {
            itemGroup.add(SuperflatProgressionBlocks.CHARCOAL_BLOCK.asItem());
            itemGroup.add(SuperflatProgressionBlocks.DIRT_SLAB.asItem());
            itemGroup.add(SuperflatProgressionBlocks.GRASS_SLAB.asItem());
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register((itemGroup) -> {
            itemGroup.add(PIXIE_SPAWN_EGG);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> {
            itemGroup.add(MAGIC_TORCH);
            itemGroup.add(SuperflatProgressionBlocks.MAGIC_LANTERN);
            itemGroup.add(SuperflatProgressionBlocks.SCROLL_CRAFTING);
            itemGroup.add(SuperflatProgressionBlocks.GRINDER);
            itemGroup.add(SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR);
		});

		FuelRegistry.INSTANCE.add(SuperflatProgressionBlocks.CHARCOAL_BLOCK, 16000);

    }

	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		return register(name, itemFactory, settings, null);
	}

	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings, String optionalModID) {
		// Create the item instance.
		Item item = itemFactory.apply(settings);
		
		if (optionalModID == null || FabricLoader.getInstance().isModLoaded(optionalModID)) {
			// Create the item key.
			RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SuperflatProgression.MOD_ID, name));

			// Register the item.
			Registry.register(Registries.ITEM, itemKey, item);
		}

		return item;
	}
}
