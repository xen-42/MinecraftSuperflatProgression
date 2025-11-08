package xen42.superflatprogression;

import java.util.function.Function;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.blocks.GrinderBlock;
import xen42.superflatprogression.blocks.MagicTorchBlock;
import xen42.superflatprogression.blocks.MagicTorchBlockEntity;
import xen42.superflatprogression.blocks.ScrollCraftingBlock;
import xen42.superflatprogression.blocks.WallMagicTorchBlock;

public class SuperflatProgressionBlocks {
    public static void initialize() { 
        FlammableBlockRegistry.getDefaultInstance().add(CHARCOAL_BLOCK, 5, 5);
    }

    public static final Block CHARCOAL_BLOCK = register(
		"charcoal_block",
		Block::new,
        AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),
		true
	);

	public static final Block MAGIC_TORCH = register(
		"magic_torch",
		(settings) -> new MagicTorchBlock(settings, SuperflatProgression.MAGIC_TORCH_PARTICLE),
		AbstractBlock.Settings.create().noCollision().breakInstantly().luminance(state -> 10).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY),
		false
	);

	public static final Block WALL_MAGIC_TORCH = register(
		"wall_magic_torch",
		(settings) -> new WallMagicTorchBlock(settings, SuperflatProgression.MAGIC_TORCH_PARTICLE),
		AbstractBlock.Settings.create().noCollision().breakInstantly().luminance(state -> 10).sounds(BlockSoundGroup.WOOD).dropsLike(MAGIC_TORCH).pistonBehavior(PistonBehavior.DESTROY),
		false
	);

	public static final Block MAGIC_LANTERN = register(
		"magic_lantern", 
		(settings) -> new LanternBlock(settings), 
		AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).solid().requiresTool().strength(3.5F).sounds(BlockSoundGroup.LANTERN)
				.luminance(state -> 10).nonOpaque().pistonBehavior(PistonBehavior.DESTROY),
		true
	);

	public static final BlockEntityType<MagicTorchBlockEntity> MAGIC_TORCH_ENTITY = registerBlockEntityType(
		"magic_torch_entity",
		FabricBlockEntityTypeBuilder.create(MagicTorchBlockEntity::new, 
			new Block[] { MAGIC_TORCH, WALL_MAGIC_TORCH, MAGIC_LANTERN }).build()
	);

	public static final DefaultParticleType MAGIC_TORCH_PARTICLE = FabricParticleTypes.simple();

	public static final ScrollCraftingBlock SCROLL_CRAFTING = (ScrollCraftingBlock)register(
		"scroll_crafting", ScrollCraftingBlock::new, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).nonOpaque(), true);

	public static final GrinderBlock GRINDER = (GrinderBlock)register(
		"grinder", GrinderBlock::new, AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque(), true);

	public static final SlabBlock DIRT_SLAB = (SlabBlock)register("dirt_slab", SlabBlock::new, AbstractBlock.Settings.copy(Blocks.DIRT), true);

	private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
		// Create a registry key for the block
		RegistryKey<Block> blockKey = keyOfBlock(name);
		// Create the block instance
		Block block = blockFactory.apply(settings);

		// Sometimes, you may not want to register an item for the block.
		// Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
		if (shouldRegisterItem) {
			// Items need to be registered with a different type of registry key, but the ID
			// can be the same.
			RegistryKey<Item> itemKey = keyOfItem(name);

			BlockItem blockItem = new BlockItem(block, new Item.Settings());
			Registry.register(Registries.ITEM, itemKey, blockItem);
		}

		return Registry.register(Registries.BLOCK, blockKey, block);
	}

	private static RegistryKey<Block> keyOfBlock(String name) {
		return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SuperflatProgression.MOD_ID, name));
	}

	private static RegistryKey<Item> keyOfItem(String name) {
		return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SuperflatProgression.MOD_ID, name));
	}

	public static <T extends BlockEntityType<?>> T registerBlockEntityType(String path, T blockEntityType) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(SuperflatProgression.MOD_ID, path), blockEntityType);
	}
}
