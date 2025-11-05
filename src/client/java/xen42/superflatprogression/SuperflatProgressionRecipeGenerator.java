package xen42.superflatprogression;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
import net.minecraft.util.Identifier;
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

                createScroll(SuperflatProgressionItems.SCROLL_RAIN, Items.BUCKET, 2).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_THUNDER, Items.LIGHTNING_ROD, 4).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_CLEAR_WEATHER, Items.SUNFLOWER, 2).offerTo(exporter);
                createScroll(SuperflatProgressionItems.SCROLL_TRADE, Items.BELL, 4).offerTo(exporter);
            }

            public ScrollCraftingRecipeJsonBuilder createScroll(ItemConvertible output, Item input, int cost) {
                return new ScrollCraftingRecipeJsonBuilder(registryLookup.getWrapperOrThrow(RegistryKeys.ITEM), output, Ingredient.ofItems(input), cost)
                    .criterion(hasItem(SuperflatProgressionItems.ESSENCE), conditionsFromItem(SuperflatProgressionItems.ESSENCE))
                    .criterion(hasItem(SuperflatProgressionItems.PARCHMENT), conditionsFromTag(SuperflatProgressionTags.ItemTags.PARCHMENTS))
                    .criterion(hasItem(input), conditionsFromItem(input));
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

