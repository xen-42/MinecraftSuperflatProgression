package xen42.superflatprogression.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jetbrains.annotations.VisibleForTesting;

import it.unimi.dsi.fastutil.chars.CharSet;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.BaseMapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionItems;

public class ScrollCraftingRecipe implements Recipe<ScrollCraftingRecipeInput> {
	final Identifier id;
	final RawRecipe raw;
	public final ItemStack result;
	final OptionalInt cost;
	final String group;

	public ScrollCraftingRecipe(Identifier id, String group, RawRecipe raw, ItemStack result) {
		this(id, group, raw, result, OptionalInt.empty());
	}

	public ScrollCraftingRecipe(Identifier id, String group, RawRecipe raw, ItemStack result, Optional<Integer> cost) {
		this(id, group, raw, result, cost.stream().mapToInt(i -> i).findFirst());
	}

	public ScrollCraftingRecipe(Identifier id, String group, RawRecipe raw, ItemStack result, OptionalInt cost) {
		this.id = id;
		this.group = group;
		this.raw = raw;
		this.result = result;
		this.cost = cost;
	}

	@Override
	public RecipeType<ScrollCraftingRecipe> getType() {
		return SuperflatProgression.SCROLL_CRAFTING_RECIPE_TYPE;
	}

	@Override
	public RecipeSerializer<? extends ScrollCraftingRecipe> getSerializer() {
		return SuperflatProgression.SCROLL_CRAFTING_RECIPE_SERIALIZER;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return false;
	}

	@Override
	public boolean showNotification() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		DefaultedList<Ingredient> defaultedList = this.getIngredients();
		return defaultedList.isEmpty()
			|| defaultedList.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(ingredient -> ingredient.getMatchingStacks().length == 0);
	}

	public DefaultedList<ItemStack> getRecipeRemainders(ScrollCraftingRecipeInput input) {
		return collectRecipeRemainders(input);
	}

	public static DefaultedList<ItemStack> collectRecipeRemainders(ScrollCraftingRecipeInput input) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			defaultedList.set(i, input.getStackInSlot(i));
		}

		return defaultedList;
	}

	@Override
	@VisibleForTesting
	public DefaultedList<Ingredient> getIngredients() {
		return DefaultedList.copyOf(Ingredient.EMPTY, this.raw.getIngredient());
	}

	public boolean matches(ScrollCraftingRecipeInput input, World world) {
		return this.raw.matches(input);
	}

	public ItemStack craft(ScrollCraftingRecipeInput input, DynamicRegistryManager registryManager) {
		return this.result.copy();
	}

	public ItemStack result() {
		return this.result;
	}
	
	public OptionalInt getCost() {
		return cost;
	}
	
	public int getCostOrDefault() {
		return getCost().orElse(5);
	}
	
	public Optional<Integer> getBoxedCost() {
		return cost.stream().boxed().findFirst();
	}

	public static class Serializer implements RecipeSerializer<ScrollCraftingRecipe> {
		private static final Codec<Item> ITEM_CODEC = Codecs.validate(
			Registries.ITEM.getCodec(), item -> item == Items.AIR ? DataResult.error(() -> "Item must not be minecraft:air") : DataResult.success(item)
		);
		public static final Codec<ItemStack> RECIPE_RESULT_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					ITEM_CODEC.fieldOf("item").forGetter(ItemStack::getItem),
					createStrictOptionalFieldCodec(Codecs.POSITIVE_INT, "count", 1).forGetter(ItemStack::getCount)
				)
				.apply(instance, ItemStack::new)
			);
		public static final Codec<ScrollCraftingRecipe> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Identifier.CODEC.fieldOf("id").forGetter(recipe -> recipe.id),
					Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
					RawRecipe.CODEC.forGetter(recipe -> recipe.raw),
					RECIPE_RESULT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
					Codec.INT.optionalFieldOf("cost").forGetter(ScrollCraftingRecipe::getBoxedCost)
				)
				.apply(instance, ScrollCraftingRecipe::new)
		);

		public Codec<ScrollCraftingRecipe> codec() {
			return CODEC;
		}

		public ScrollCraftingRecipe read(Identifier id, PacketByteBuf buf) {
			String string = buf.readString();
			RawRecipe rawRecipe = RawRecipe.readFromBuf(buf);
			ItemStack result = buf.readItemStack();
			var value = buf.readVarInt();
			OptionalInt cost = value == 0 ? OptionalInt.empty() : OptionalInt.of(value - 1);
			return new ScrollCraftingRecipe(id, string, rawRecipe, result, cost);
		}

		@Override
		public ScrollCraftingRecipe read(Identifier id, JsonObject json) {
			// Read optional group
			String group = json.has("group") ? json.get("group").getAsString() : "";

			// Read optional cost
			OptionalInt cost = OptionalInt.empty();
			if (json.has("cost")) {
				cost = OptionalInt.of(json.get("cost").getAsInt());
			}

			JsonObject ingredientObj = JsonHelper.getObject(json, "input");
			var ingredient = Ingredient.fromJson(JsonHelper.getElement(ingredientObj, "item"));

			RawRecipe rawRecipe = RawRecipe.create(ingredient);

			// Parse result item stack
			JsonObject resultObj = JsonHelper.getObject(json, "result");
			Item item = Registries.ITEM.get(new Identifier(JsonHelper.getString(resultObj, "item")));
			if (item == Items.AIR) throw new JsonParseException("Result item cannot be AIR");

			int count = JsonHelper.getInt(resultObj, "count", 1);
			ItemStack result = new ItemStack(item, count);

			return new ScrollCraftingRecipe(id, group, rawRecipe, result, cost);
		}

		public void write(PacketByteBuf buf, ScrollCraftingRecipe recipe) {
			buf.writeString(recipe.group);
			recipe.raw.writeToBuf(buf);
			buf.writeItemStack(recipe.result());
			buf.writeVarInt(recipe.cost.isPresent() ? recipe.cost.getAsInt() + 1 : 0);
		}
	}
	

	public static final class RawRecipe {
		private static final int MAX_WIDTH_AND_HEIGHT = 3;
		private static final int MAX_WIDTH_END = 1;
		public static final char SPACE = ' ';
		public static final MapCodec<RawRecipe> CODEC = RawRecipe.Data.CODEC
			.flatXmap(
				RawRecipe::fromData,
				recipe -> (DataResult<RawRecipe.Data>)recipe.data.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
			);

		public void writeToBuf(PacketByteBuf buf) {
			this.ingredient.write(buf);
		}

		public static RawRecipe readFromBuf(PacketByteBuf buf) {
			return new RawRecipe(Ingredient.fromPacket(buf), Optional.empty());
		}
		
		private final Ingredient ingredient;
		private final Optional<RawRecipe.Data> data;
		private final Ingredient essence, parchment;
	
		public RawRecipe(Ingredient ingredient, Optional<RawRecipe.Data> data) {
			this.ingredient = ingredient;
			essence = Ingredient.ofItems(SuperflatProgressionItems.ESSENCE);
			parchment = Ingredient.ofItems(SuperflatProgressionItems.PARCHMENT);
			this.data = data;
		}
	
		public static RawRecipe create(Ingredient ingredient) {
			return new RawRecipe(ingredient, Optional.empty());
		}
	
		private static DataResult<RawRecipe> fromData(RawRecipe.Data data) {
			return !data.ingredient.isEmpty()
				? DataResult.error(() -> "Bad recipe")
				: DataResult.success(new RawRecipe(data.ingredient, Optional.of(data)));
		}
	
		/**
		 * Removes empty space from around the recipe pattern.
		 * 
		 * <p>Turns patterns such as:
		 * <pre>
		 * {@code
		 * "   o"
		 * "   a"
		 * "	"
		 * }
		 * </pre>
		 * Into:
		 * <pre>
		 * {@code
		 * "o"
		 * "a"
		 * }
		 * </pre>
		 * 
		 * @return a new recipe pattern with all leading and trailing empty rows/columns removed
		 */
		@VisibleForTesting
		static String[] removePadding(List<String> pattern) {
			// Trim each line
			List<String> trimmedLines = pattern.stream()
				.map(String::trim)
				.toList();

			// Remove leading empty lines
			int start = 0;
			while (start < trimmedLines.size() && trimmedLines.get(start).isEmpty()) {
				start++;
			}

			// Remove trailing empty lines
			int end = trimmedLines.size();
			while (end > start && trimmedLines.get(end - 1).isEmpty()) {
				end--;
			}

			// Return the cleaned pattern
			return trimmedLines.subList(start, end).toArray(new String[0]);
		}
	
		public boolean matches(ScrollCraftingRecipeInput input) {
			if (!input.getStackInSlot(0).isOf(SuperflatProgressionItems.PARCHMENT)) {
				return false;
			}
			if (!input.getStackInSlot(1).isOf(SuperflatProgressionItems.ESSENCE)) {
				return false;
			}
			if (!this.ingredient.test(input.getStackInSlot(2))) {
				return false;
			}
			return true;
		}
	
		public Ingredient getIngredient() {
			return this.ingredient;
		}
	
		public record Data(Ingredient ingredient) {
			private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(pattern -> {
				if (pattern.size() > MAX_WIDTH_AND_HEIGHT) {
					return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
				} else if (pattern.isEmpty()) {
					return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
				} else {
					for (int i = 0; i < pattern.size(); i++) {
						final int fi = i + 1;
						String string = pattern.get(i);
						int length = string.length();
						if (fi == MAX_WIDTH_AND_HEIGHT) { // is end
							if (length > MAX_WIDTH_END) {
								return DataResult.error(() -> "Invalid pattern: too many columns for row #" + fi + ", 1 is maximum and minimum");
							}
							else if (length < MAX_WIDTH_END) {
								return DataResult.error(() -> "Invalid pattern: too little columns for row #" + fi + ", 1 is maximum and minimum");
							}
						}
						else {
							if (length > MAX_WIDTH_AND_HEIGHT) {
								return DataResult.error(() -> "Invalid pattern: too many columns for row #" + fi + ", 3 is maximum and minimum");
							}
							else if (length < MAX_WIDTH_AND_HEIGHT) {
								return DataResult.error(() -> "Invalid pattern: too little columns for row #" + fi + ", 3 is maximum and minimum");
							}
						}
					}
	
					return DataResult.success(pattern);
				}
			}, Function.identity());
			private static final Codec<Character> KEY_ENTRY_CODEC = Codec.STRING.comapFlatMap(keyEntry -> {
				if (keyEntry.length() != 1) {
					return DataResult.error(() -> "Invalid key entry: '" + keyEntry + "' is an invalid symbol (must be 1 character only).");
				} else {
					return " ".equals(keyEntry) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(keyEntry.charAt(0));
				}
			}, String::valueOf);
			private static final Codec<Ingredient> KEY_CODEC = Codec.STRING.comapFlatMap(key -> {
				Item item = (Item)Registries.ITEM.getOrEmpty(Identifier.tryParse(key)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + key + "'"));
				if (item == Items.AIR) {
					return DataResult.error(() -> "Empty ingredient not allowed here");
				} else {
					return DataResult.success(Ingredient.ofItems(item));
				}
			}, String::valueOf);
			public static final MapCodec<RawRecipe.Data> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						strictUnboundedMap(KEY_ENTRY_CODEC, KEY_CODEC).fieldOf("key").forGetter(data -> data.key),
						PATTERN_CODEC.fieldOf("pattern").forGetter(data -> data.pattern)
					)
					.apply(instance, RawRecipe.Data::new)
			);
		}
	}

	@Override
	public boolean fits(int width, int height) {
		if (width == RawRecipe.MAX_WIDTH_AND_HEIGHT) {
			return height <= RawRecipe.MAX_WIDTH_AND_HEIGHT - RawRecipe.MAX_WIDTH_END;
		}
		else {
			return width >= RawRecipe.MAX_WIDTH_AND_HEIGHT && height >= RawRecipe.MAX_WIDTH_AND_HEIGHT;
		}
	}

	@Override
	public ItemStack getOutput(DynamicRegistryManager registryManager) {
		return result();
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public static <A> MapCodec<A> createStrictOptionalFieldCodec(Codec<A> codec, String field, A fallback) {
		return createStrictOptionalFieldCodec(codec, field)
			.xmap(value -> value.orElse(fallback), value -> Objects.equals(value, fallback) ? Optional.empty() : Optional.of(value));
	}

	public static <A> MapCodec<Optional<A>> createStrictOptionalFieldCodec(Codec<A> codec, String field) {
		return new StrictOptionalField<>(field, codec);
	}

	static final class StrictOptionalField<A> extends MapCodec<Optional<A>> {
		private final String field;
		private final Codec<A> codec;

		public StrictOptionalField(String field, Codec<A> codec) {
			this.field = field;
			this.codec = codec;
		}

		@Override
		public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
			T object = input.get(this.field);
			return object == null ? DataResult.success(Optional.empty()) : this.codec.parse(ops, object).map(Optional::of);
		}

		public <T> RecordBuilder<T> encode(Optional<A> optional, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
			return optional.isPresent() ? recordBuilder.add(this.field, this.codec.encodeStart(dynamicOps, (A)optional.get())) : recordBuilder;
		}

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.of(ops.createString(this.field));
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else {
				return !(o instanceof StrictOptionalField<?> strictOptionalField)
					? false
					: Objects.equals(this.field, strictOptionalField.field) && Objects.equals(this.codec, strictOptionalField.codec);
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field, this.codec});
		}

		public String toString() {
			return "StrictOptionalFieldCodec[" + this.field + ": " + this.codec + "]";
		}
	}

	public static <K, V> StrictUnboundedMapCodec<K, V> strictUnboundedMap(Codec<K> keyCodec, Codec<V> elementCodec) {
		return new StrictUnboundedMapCodec<>(keyCodec, elementCodec);
	}

	public record StrictUnboundedMapCodec<K, V>(Codec<K> keyCodec, Codec<V> elementCodec) implements Codec<Map<K, V>>, BaseMapCodec<K, V> {
		@Override
		public <T> DataResult<Map<K, V>> decode(DynamicOps<T> ops, MapLike<T> input) {
			ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();

			for (Pair<T, T> pair : input.entries().toList()) {
				DataResult<K> dataResult = this.keyCodec().parse(ops, pair.getFirst());
				DataResult<V> dataResult2 = this.elementCodec().parse(ops, pair.getSecond());
				DataResult<Pair<K, V>> dataResult3 = dataResult.apply2stable(Pair::of, dataResult2);
				if (dataResult3.error().isPresent()) {
					return DataResult.error(() -> {
						PartialResult<Pair<K, V>> partialResult = (PartialResult<Pair<K, V>>)dataResult3.error().get();
						String string;
						if (dataResult.result().isPresent()) {
							string = "Map entry '" + dataResult.result().get() + "' : " + partialResult.message();
						} else {
							string = partialResult.message();
						}

						return string;
					});
				}

				if (!dataResult3.result().isPresent()) {
					return DataResult.error(() -> "Empty or invalid map contents are not allowed");
				}

				Pair<K, V> pair2 = (Pair<K, V>)dataResult3.result().get();
				builder.put(pair2.getFirst(), pair2.getSecond());
			}

			Map<K, V> map = builder.build();
			return DataResult.success(map);
		}

		@Override
		public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
			return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> this.decode(ops, map)).map(map -> Pair.of(map, input));
		}

		public <T> DataResult<T> encode(Map<K, V> map, DynamicOps<T> dynamicOps, T object) {
			return this.encode(map, dynamicOps, dynamicOps.mapBuilder()).build(object);
		}

		public String toString() {
			return "StrictUnboundedMapCodec[" + this.keyCodec + " -> " + this.elementCodec + "]";
		}
	}
}