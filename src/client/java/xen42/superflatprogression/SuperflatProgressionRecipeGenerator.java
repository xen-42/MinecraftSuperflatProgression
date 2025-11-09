package xen42.superflatprogression;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.mojang.datafixers.types.templates.Tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.recipe.GrinderRecipeJsonBuilder;
import xen42.superflatprogression.recipe.ScrollCraftingRecipeJsonBuilder;

public class SuperflatProgressionRecipeGenerator extends FabricRecipeProvider {
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    
    public SuperflatProgressionRecipeGenerator(FabricDataOutput generator, CompletableFuture<WrapperLookup> registriesFuture) {
        super(generator);
        registryLookupFuture = registriesFuture;
    }

    @Override
    public String getName() {
        return "SuperflatProgressionRecipeGenerator";
    }

    @Override
    public Identifier getRecipeIdentifier(Identifier identifier) {
        return Identifier.of(SuperflatProgression.MOD_ID, identifier.getPath());
    }

    public static Identifier getItemId(ItemConvertible item) {
		return Identifier.of(SuperflatProgression.MOD_ID, Registries.ITEM.getId(item.asItem()).getPath());
	}
    
    public static void offerTo(CraftingRecipeJsonBuilder builder, Consumer<RecipeJsonProvider> exporter) {
        builder.offerTo(exporter, getItemId(builder.getOutputItem()));
    }

    public static void offerTo(CraftingRecipeJsonBuilder builder, Consumer<RecipeJsonProvider> exporter, String recipePath) {
        Identifier identifier = Identifier.of(SuperflatProgression.MOD_ID, recipePath);
        builder.offerTo(exporter, identifier);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        getRecipeGenerator(registryLookupFuture.join(), exporter).generate();
    }

    protected RecipeGenerator getRecipeGenerator(WrapperLookup registryLookup, Consumer<RecipeJsonProvider> exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            public void offerSmelting(List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
                this.fixedOfferMultipleOptions(RecipeSerializer.SMELTING, inputs, category, output, experience, cookingTime, group, "_from_smelting");
            }

            @SuppressWarnings("unused")
            public void offerBlasting(List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
                this.fixedOfferMultipleOptions(RecipeSerializer.BLASTING, inputs, category, output, experience, cookingTime, group, "_from_blasting");
            }

            public void offerCampfire(List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
                var suffix = "_from_campfire";
                for (var input : inputs) {
                    offerTo(CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(input), category, output, experience, cookingTime)
                        .group(group)
                        .criterion(hasItem(input), conditionsFromItem(input))
                        , exporter, getItemPath(output) + suffix + "_" + getItemPath(input));
                }
            }

            @Override
            public void offerStonecuttingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input, int count) {
                offerTo(SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), category, output, count)
                        .criterion(hasItem(input), conditionsFromItem(input))
                        , exporter, convertBetween(output, input) + "_stonecutting");
            }
            
            public final <T extends AbstractCookingRecipe> void fixedOfferMultipleOptions(
            		RecipeSerializer<T> serializer,
                    List<ItemConvertible> inputs,
                    RecipeCategory category,
                    ItemConvertible output,
                    float experience,
                    int cookingTime,
                    String group,
                    String suffix
                ) {
                    for (ItemConvertible itemConvertible : inputs) {
                        offerTo(CookingRecipeJsonBuilder.create(Ingredient.ofItems(itemConvertible), category, output, experience, cookingTime, serializer)
                            .group(group)
                            .criterion(hasItem(itemConvertible), conditionsFromItem(itemConvertible))
                            , exporter, getItemPath(output) + suffix + "_" + getItemPath(itemConvertible));
                    }
            }
            
            @Override
            public void generate() {
                offerTo(createShaped(RecipeCategory.MISC, SuperflatProgressionItems.PARCHMENT)
                        .pattern("XX")
                        .input('X', Items.LEATHER)
                        // Advancement that gives the recipe
                        .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                        , exporter);
                
                offerTo(createShapeless(RecipeCategory.MISC, Items.BOOK)
                        .input(Items.LEATHER) 
                        .input(SuperflatProgressionItems.PARCHMENT) 
                        .input(SuperflatProgressionItems.PARCHMENT) 
                        .input(SuperflatProgressionItems.PARCHMENT) 
                        // Advancement that gives the recipe
                        .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                        .criterion(hasItem(SuperflatProgressionItems.PARCHMENT), conditionsFromItem(SuperflatProgressionItems.PARCHMENT))
                        , exporter);

                offerTo(createShaped(RecipeCategory.MISC, SuperflatProgressionBlocks.CHARCOAL_BLOCK)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .input('X', Items.CHARCOAL)
                        // Advancement that gives the recipe
                        .criterion(hasItem(Items.CHARCOAL), conditionsFromItem(Items.CHARCOAL))
                        , exporter);
                
                offerTo(createShapeless(RecipeCategory.MISC, Items.CHARCOAL, 9)
                        .input(SuperflatProgressionBlocks.CHARCOAL_BLOCK) 
                        // Advancement that gives the recipe
                        .criterion(hasItem(Items.CHARCOAL), conditionsFromItem(Items.CHARCOAL))
                        , exporter);
                
                offerTo(createShapeless(RecipeCategory.MISC, SuperflatProgressionItems.ENRICHED_BONEMEAL, 1)
                        .input(Items.BONE_MEAL) 
                        .input(SuperflatProgressionItems.ESSENCE) 
                        .criterion(hasItem(SuperflatProgressionItems.ESSENCE), conditionsFromItem(SuperflatProgressionItems.ESSENCE))
                        , exporter);

                offerTo(createShapeless(RecipeCategory.MISC, SuperflatProgressionItems.FIRE_STARTER, 1)
                        .input(Items.STICK) 
                        .input(Items.STICK) 
                        .input(Items.STRING) 
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                        .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
                        , exporter);
                
                offerTo(createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.SOUL_SOIL, 1)
                        .input(Blocks.MUD) 
                        .input(SuperflatProgressionItems.ESSENCE) 
                        .criterion(hasItem(SuperflatProgressionItems.ESSENCE), conditionsFromItem(SuperflatProgressionItems.ESSENCE))
                        , exporter);

                offerTo(createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF, 2)
                        .input(Blocks.ANDESITE) 
                        .input(Blocks.DIORITE) 
                        .criterion(hasItem(Blocks.ANDESITE), conditionsFromItem(Blocks.ANDESITE))
                        .criterion(hasItem(Blocks.DIORITE), conditionsFromItem(Blocks.DIORITE))
                        , exporter);

                offerTo(createShaped(RecipeCategory.MISC, SuperflatProgressionItems.MAGIC_TORCH)
                        .pattern("X")
                        .pattern("Y")
                        .pattern("Z")
                        .input('X', SuperflatProgressionItems.ESSENCE)
                        .input('Y', ItemTags.COALS)
                        .input('Z', Items.STICK)
                        // Advancement that gives the recipe
                        .criterion(hasItem(SuperflatProgressionItems.ESSENCE), conditionsFromItem(SuperflatProgressionItems.ESSENCE))
                        , exporter);

                offerTo(createShaped(RecipeCategory.MISC, SuperflatProgressionBlocks.MAGIC_LANTERN)
                        .pattern("XXX")
                        .pattern("XYX")
                        .pattern("XXX")
                        .input('X', Items.IRON_NUGGET)
                        .input('Y', SuperflatProgressionItems.MAGIC_TORCH)
                        // Advancement that gives the recipe
                        .criterion(hasItem(SuperflatProgressionItems.MAGIC_TORCH), conditionsFromItem(SuperflatProgressionItems.MAGIC_TORCH))
                        .criterion(hasItem(Items.IRON_NUGGET), conditionsFromItem(Items.IRON_NUGGET))
                        , exporter);

                offerTo(createShaped(RecipeCategory.MISC, SuperflatProgressionBlocks.SCROLL_CRAFTING)
                        .pattern("X")
                        .pattern("Y")
                        .pattern("Z")
                        .input('X', SuperflatProgressionTags.ItemTags.PARCHMENTS)
                        .input('Y', ItemTags.WOODEN_SLABS)
                        .input('Z', ItemTags.PLANKS)
                        // Advancement that gives the recipe
                        .criterion(hasItem(SuperflatProgressionItems.PARCHMENT), conditionsFromTag(SuperflatProgressionTags.ItemTags.PARCHMENTS))
                        .criterion(hasItem(Blocks.OAK_SLAB), conditionsFromTag(ItemTags.WOODEN_SLABS))
                        .criterion(hasItem(Blocks.OAK_PLANKS), conditionsFromTag(ItemTags.PLANKS))
                        , exporter);
                
                offerTo(createShaped(RecipeCategory.MISC, SuperflatProgressionBlocks.GRINDER)
                        .pattern(" X ")
                        .pattern("GYG")
                        .pattern("ZZZ")
                        .input('X', Blocks.HOPPER)
                        .input('G', Items.GOLD_INGOT)
                        .input('Y', Blocks.STONECUTTER)
                        .input('Z', Blocks.COBBLESTONE)
                        // Advancement that gives the recipe
                        .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                        .criterion(hasItem(Blocks.COBBLESTONE), conditionsFromItem(Items.COBBLESTONE))
                        .criterion(hasItem(Blocks.HOPPER), conditionsFromItem(Items.HOPPER))
                        .criterion(hasItem(Blocks.STONECUTTER), conditionsFromItem(Items.STONECUTTER))
                        , exporter);
                
                offerTo(createShaped(RecipeCategory.MISC, Items.DIAMOND)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .input('X', SuperflatProgressionItems.DIAMOND_SHARD)
                        // Advancement that gives the recipe
                        .criterion(hasItem(SuperflatProgressionItems.DIAMOND_SHARD), conditionsFromItem(SuperflatProgressionItems.DIAMOND_SHARD))
                        , exporter);     
                
                offerTo(createShapeless(RecipeCategory.MISC, SuperflatProgressionItems.DIAMOND_SHARD, 9)
                    .input(Items.DIAMOND)
                    .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                    , exporter);

                offerTo(createShaped(RecipeCategory.MISC, SuperflatProgressionBlocks.DIRT_SLAB, 6)
                        .pattern("XXX")
                        .input('X', Blocks.DIRT)
                        // Advancement that gives the recipe
                        .criterion(hasItem(Blocks.DIRT), conditionsFromItem(Blocks.DIRT))
                        , exporter); 
                
                offerTo(createShaped(RecipeCategory.MISC, SuperflatProgressionBlocks.GRASS_SLAB, 6)
                        .pattern("XXX")
                        .input('X', Blocks.GRASS_BLOCK)
                        // Advancement that gives the recipe
                        .criterion(hasItem(Blocks.GRASS_BLOCK), conditionsFromItem(Blocks.GRASS_BLOCK))
                        , exporter); 

                offerTo(createShaped(RecipeCategory.MISC, Blocks.DIRT)
                        .pattern("X")
                        .pattern("X")
                        .input('X', SuperflatProgressionBlocks.DIRT_SLAB)
                        // Advancement that gives the recipe
                        .criterion(hasItem(SuperflatProgressionBlocks.DIRT_SLAB), conditionsFromItem(SuperflatProgressionBlocks.DIRT_SLAB))
                        , exporter); 

                offerTo(createShaped(RecipeCategory.MISC, Blocks.GRASS_BLOCK)
                        .pattern("X")
                        .pattern("X")
                        .input('X', SuperflatProgressionBlocks.GRASS_SLAB)
                        // Advancement that gives the recipe
                        .criterion(hasItem(SuperflatProgressionBlocks.GRASS_SLAB), conditionsFromItem(SuperflatProgressionBlocks.GRASS_SLAB))
                        , exporter); 

                offerTo(createShaped(RecipeCategory.MISC, Blocks.BUDDING_AMETHYST)
                        .pattern("XXX")
                        .pattern("XYX")
                        .pattern("XXX")
                        .input('X', SuperflatProgressionItems.ESSENCE)
                        .input('Y', Blocks.AMETHYST_BLOCK)
                        // Advancement that gives the recipe
                        .criterion(hasItem(SuperflatProgressionItems.ESSENCE), conditionsFromItem(SuperflatProgressionItems.ESSENCE))
                        .criterion(hasItem(Items.AMETHYST_BLOCK), conditionsFromItem(Items.AMETHYST_BLOCK))
                        , exporter); 

                offerTo(createShaped(RecipeCategory.MISC, SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR)
                        .pattern("CCC")
                        .pattern("DSD")
                        .pattern("XXX")
                        .input('X', Items.END_STONE)
                        .input('D', Items.DIAMOND)
                        .input('S', Items.NETHER_STAR)
                        .input('C', Items.OBSIDIAN)
                        // Advancement that gives the recipe
                        .criterion(hasItem(Items.END_STONE), conditionsFromItem(Items.END_STONE))
                        , exporter); 

                offerTo(createShapeless(RecipeCategory.MISC, Items.PURPLE_DYE).input(SuperflatProgressionItems.ESSENCE)
                    .criterion(hasItem(SuperflatProgressionItems.ESSENCE), conditionsFromItem(SuperflatProgressionItems.ESSENCE)),
                    exporter);
                    
                offerTools(SuperflatProgressionItems.BONE_AXE, SuperflatProgressionItems.BONE_HOE, SuperflatProgressionItems.BONE_SHOVEL,
                    SuperflatProgressionItems.BONE_PICKAXE, SuperflatProgressionItems.BONE_SWORD, Items.BONE);

                createScroll(SuperflatProgressionItems.SCROLL_RAIN, Items.BUCKET, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_THUNDER, Items.LIGHTNING_ROD, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_CLEAR_WEATHER, Items.SUNFLOWER, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_TRADE, Items.BELL, 5).offerTo(exporter);

                createScroll(SuperflatProgressionItems.SCROLL_PIG, Items.CARROT, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_COW, Items.WHEAT, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_CHICKEN, Items.WHEAT_SEEDS, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_SHEEP, ItemTags.WOOL, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_CAT, ItemTags.FISHES, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_WOLF, Items.BONE, 5).offerTo(exporter);

                createScroll(SuperflatProgressionItems.SCROLL_ZOMBIE, Items.ROTTEN_FLESH, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_SKELETON, Items.ARROW, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_WITCH, Items.CAULDRON, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_ENDERMAN, Items.ENDER_PEARL, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_SLIME, Items.SLIME_BALL, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_MAGMA_CUBE, Items.MAGMA_CREAM, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_BLAZE, Items.BLAZE_ROD, 5).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_SPIDER, Items.SPIDER_EYE, 5).offerTo(exporter);

                createGrinder(Blocks.STONE.asItem(), Blocks.COBBLESTONE.asItem(), false).offerTo(exporter);
                createGrinder(Blocks.COBBLESTONE.asItem(), Blocks.GRAVEL.asItem(), false).offerTo(exporter);
                createGrinder(Blocks.GRAVEL.asItem(), Blocks.SAND.asItem(), false).offerTo(exporter);
                createGrinder(Blocks.MAGMA_BLOCK.asItem(), Items.LAVA_BUCKET, true).offerTo(exporter);
                createGrinder(Blocks.SOUL_SOIL.asItem(), Items.SOUL_SAND, false).offerTo(exporter);
                createGrinder(SuperflatProgressionTags.ItemTags.DIAMOND_GEAR, SuperflatProgressionItems.DIAMOND_SHARD, false).offerTo(exporter);
                createGrinder(SuperflatProgressionTags.ItemTags.BONE_GEAR, Items.BONE_MEAL, false).offerTo(exporter);

                // Extra recipes to flesh it out
                createGrinder(ItemTags.WOOL, Items.STRING, false).setCount(4).offerTo(exporter); 

                createGrinder(SuperflatProgressionTags.ItemTags.ICES, Items.SNOWBALL, false).offerTo(exporter);
                createGrinder(Items.SNOW_BLOCK, Items.POWDER_SNOW_BUCKET, true).offerTo(exporter);

                createGrinder(Items.SUGAR_CANE, Items.SUGAR, false).setCount(2).offerTo(exporter); 
                createGrinder(Items.BONE, Items.BONE_MEAL, false).setCount(4).offerTo(exporter); 
                createGrinder(Items.FURNACE, Items.COBBLESTONE, false).setCount(8).offerTo(exporter); 
                // Can get from piglin bartering
                createGrinder(Items.SPECTRAL_ARROW, Items.GLOWSTONE_DUST, false).setCount(2).offerTo(exporter); 
                createGrinder(Items.FIRE_CHARGE, Items.BLAZE_POWDER, false).setCount(1).offerTo(exporter); 

                createGrinder(ItemTags.DIAMOND_ORES, Items.DIAMOND, false).setCount(2).offerTo(exporter); 
                createGrinder(ItemTags.GOLD_ORES, Items.GOLD_ORE, false).setCount(2).offerTo(exporter); 
                createGrinder(ItemTags.IRON_ORES, Items.IRON_INGOT, false).setCount(2).offerTo(exporter); 
                createGrinder(ItemTags.COPPER_ORES, Items.COPPER_INGOT, false).setCount(6).offerTo(exporter); 
                createGrinder(ItemTags.COAL_ORES, Items.COAL, false).setCount(2).offerTo(exporter); 
                createGrinder(ItemTags.EMERALD_ORES, Items.EMERALD, false).setCount(2).offerTo(exporter); 
                createGrinder(ItemTags.LAPIS_ORES, Items.LAPIS_LAZULI, false).setCount(6).offerTo(exporter); 
                createGrinder(ItemTags.REDSTONE_ORES, Items.REDSTONE, false).setCount(6).offerTo(exporter); 

                createGrinder(Items.GLOWSTONE, Items.GLOWSTONE_DUST, false).setCount(4).offerTo(exporter); 

                createGrinder(Items.CACTUS, Items.GREEN_DYE, false).setCount(2).offerTo(exporter); 
                createGrinder(Items.SEA_PICKLE, Items.LIME_DYE, false).setCount(2).offerTo(exporter); 

                createGrinder(Items.BONE_MEAL, Items.WHITE_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.LILY_OF_THE_VALLEY, Items.WHITE_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.AZURE_BLUET, Items.LIGHT_GRAY_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.OXEYE_DAISY, Items.LIGHT_GRAY_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.WHITE_TULIP, Items.LIGHT_GRAY_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.INK_SAC, Items.BLACK_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.WITHER_ROSE, Items.BLACK_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.COCOA_BEANS, Items.BROWN_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.POPPY, Items.RED_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.RED_TULIP, Items.RED_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.ROSE_BUSH, Items.RED_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.BEETROOT, Items.RED_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.ORANGE_TULIP, Items.ORANGE_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.TORCHFLOWER, Items.ORANGE_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.DANDELION, Items.YELLOW_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.SUNFLOWER, Items.YELLOW_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.PITCHER_PLANT, Items.CYAN_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.BLUE_ORCHID, Items.LIGHT_BLUE_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.LAPIS_LAZULI, Items.BLUE_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.CORNFLOWER, Items.BLUE_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.ALLIUM, Items.MAGENTA_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.LILAC, Items.MAGENTA_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.PINK_TULIP, Items.PINK_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.PEONY, Items.PINK_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(Items.PINK_PETALS, Items.PINK_DYE, false).setCount(2).offerTo(exporter);
                createGrinder(SuperflatProgressionItems.ESSENCE, Items.PURPLE_DYE, false).setCount(2).offerTo(exporter);

                createGrinder(Items.SEA_LANTERN, Items.PRISMARINE_CRYSTALS, false).setCount(5).offerTo(exporter);
                createGrinder(Items.PRISMARINE_BRICKS, Items.PRISMARINE_SHARD, false).setCount(9).offerTo(exporter);
                createGrinder(Items.DARK_PRISMARINE, Items.PRISMARINE_SHARD, false).setCount(8).offerTo(exporter);
                createGrinder(Items.PRISMARINE, Items.PRISMARINE_SHARD, false).setCount(4).offerTo(exporter);

                offerSmelting(List.of(Items.GLASS_BOTTLE), RecipeCategory.MISC, Blocks.GLASS, 0f, 200, Items.GLASS_BOTTLE.getName().toString());
                offerSmelting(List.of(Items.ROTTEN_FLESH), RecipeCategory.MISC, Items.LEATHER, 0.35f, 200, Items.ROTTEN_FLESH.getName().toString());
                offerCampfire(List.of(Items.ROTTEN_FLESH), RecipeCategory.MISC, Items.LEATHER, 0.35f, 200, Items.ROTTEN_FLESH.getName().toString());
            }

            public ScrollCraftingRecipeJsonBuilder createScroll(ItemConvertible output, Item input, int cost) {
                return new ScrollCraftingRecipeJsonBuilder(registryLookup.getWrapperOrThrow(RegistryKeys.ITEM), output, Ingredient.ofItems(input), cost)
                    .criterion(hasItem(SuperflatProgressionItems.ESSENCE), conditionsFromItem(SuperflatProgressionItems.ESSENCE))
                    .criterion(hasItem(SuperflatProgressionItems.PARCHMENT), conditionsFromTag(SuperflatProgressionTags.ItemTags.PARCHMENTS))
                    .criterion(hasItem(input), conditionsFromItem(input));
            }

            public ScrollCraftingRecipeJsonBuilder createScroll(ItemConvertible output, TagKey<Item> input, int cost) {
                return new ScrollCraftingRecipeJsonBuilder(registryLookup.getWrapperOrThrow(RegistryKeys.ITEM), output, Ingredient.fromTag(input), cost)
                    .criterion(hasItem(SuperflatProgressionItems.ESSENCE), conditionsFromItem(SuperflatProgressionItems.ESSENCE))
                    .criterion(hasItem(SuperflatProgressionItems.PARCHMENT), conditionsFromTag(SuperflatProgressionTags.ItemTags.PARCHMENTS))
                    .criterion("has_" + input.toString(), conditionsFromTag(input));
            }

            public GrinderRecipeJsonBuilder createGrinder(Item input, ItemConvertible output, boolean needsBucket) {
                return new GrinderRecipeJsonBuilder(registryLookup.getWrapperOrThrow(RegistryKeys.ITEM), 
                    input, output, needsBucket)
                    .criterion(hasItem(input), conditionsFromItem(input));
            }

            public GrinderRecipeJsonBuilder createGrinder(TagKey<Item> input, ItemConvertible output, boolean needsBucket) {
                return new GrinderRecipeJsonBuilder(registryLookup.getWrapperOrThrow(RegistryKeys.ITEM), 
                    input, output, needsBucket)
                    .criterion("has_" + input.toString(), conditionsFromTag(input));
            }

            public void offerTools(Item axe, Item hoe, Item shovel, Item pickaxe, Item sword, Item material) {
                offerTo(createShaped(RecipeCategory.TOOLS, axe)
                    .pattern("XX")
                    .pattern("XY")
                    .pattern(" Y")
                    .input('X', material)
                    .input('Y', Items.STICK)
                    .criterion(hasItem(material), conditionsFromItem(material)),
                    exporter
                );

                offerTo(createShaped(RecipeCategory.TOOLS, hoe)
                    .pattern("XX")
                    .pattern(" Y")
                    .pattern(" Y")
                    .input('X', material)
                    .input('Y', Items.STICK)
                    .criterion(hasItem(material), conditionsFromItem(material)),
                    exporter
                );

                offerTo(createShaped(RecipeCategory.TOOLS, shovel)
                    .pattern("X")
                    .pattern("Y")
                    .pattern("Y")
                    .input('X', material)
                    .input('Y', Items.STICK)
                    .criterion(hasItem(material), conditionsFromItem(material)),
                    exporter
                );

                offerTo(createShaped(RecipeCategory.TOOLS, pickaxe)
                    .pattern("XXX")
                    .pattern(" Y ")
                    .pattern(" Y ")
                    .input('X', material)
                    .input('Y', Items.STICK)
                    .criterion(hasItem(material), conditionsFromItem(material)),
                    exporter
                );

                offerTo(createShaped(RecipeCategory.TOOLS, sword)
                    .pattern("X")
                    .pattern("X")
                    .pattern("Y")
                    .input('X', material)
                    .input('Y', Items.STICK)
                    .criterion(hasItem(material), conditionsFromItem(material)),
                    exporter
                );
            }
        };
    }
    
    public abstract class RecipeGenerator {
        protected final WrapperLookup registryLookup;
        protected final Consumer<RecipeJsonProvider> exporter;

        public RecipeGenerator(WrapperLookup registryLookup, Consumer<RecipeJsonProvider> exporter) {
            this.registryLookup = registryLookup;
            this.exporter = exporter;
        }

        public ShapedRecipeJsonBuilder createShaped(RecipeCategory category, ItemConvertible output) {
            return ShapedRecipeJsonBuilder.create(category, output);
        }

        public ShapedRecipeJsonBuilder createShaped(RecipeCategory category, ItemConvertible output, int count) {
            return ShapedRecipeJsonBuilder.create(category, output, count);
        }

        public ShapelessRecipeJsonBuilder createShapeless(RecipeCategory category, ItemStack output) {
            return ShapelessRecipeJsonBuilder.create(category, output.getItem(), output.getCount());
        }

        public ShapelessRecipeJsonBuilder createShapeless(RecipeCategory category, ItemConvertible output) {
            return ShapelessRecipeJsonBuilder.create(category, output);
        }

        public ShapelessRecipeJsonBuilder createShapeless(RecipeCategory category, ItemConvertible output, int count) {
            return ShapelessRecipeJsonBuilder.create(category, output, count);
        }

    	public void offerStonecuttingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
    		this.offerStonecuttingRecipe(category, output, input, 1);
    	}

    	public abstract void offerStonecuttingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input, int count);

        public abstract void generate();
    }
}

