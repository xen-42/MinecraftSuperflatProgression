package xen42.superflatprogression;

import net.minecraft.advancement.Advancement;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.biome.Biome;

public class SuperflatProgressionUtils {

	public static String getTranslationKey(Fluid fluid) {
		return "block." + getFullId(fluid);
	}

	public static String getTranslationKey(RegistryKey<VillagerProfession> key) {
		return "entity.minecraft.villager." + getPath(key);
	}

	public static String getTranslationKey(TagKey<?> key) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("tag.");

		Identifier registryIdentifier = key.registry().getValue();
		Identifier tagIdentifier = key.id();

		if (!registryIdentifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
			stringBuilder.append(registryIdentifier.getNamespace())
					.append(".");
		}

		stringBuilder.append(registryIdentifier.getPath().replace("/", "."))
				.append(".")
				.append(tagIdentifier.getNamespace())
				.append(".")
				.append(tagIdentifier.getPath().replace("/", ".").replace(":", "."));

		return stringBuilder.toString();
	}

	// IDENTIFIER

	public static Identifier getId(Item item) {
		return Registries.ITEM.getId(item);
	}

	public static Identifier getId(ItemConvertible item) {
		return getId(item.asItem());
	}

	public static Identifier getId(Block block) {
		return Registries.BLOCK.getId(block);
	}

	public static Identifier getId(Fluid fluid) {
		return Registries.FLUID.getId(fluid);
	}

	public static Identifier getId(EntityType<?> entity) {
		return EntityType.getId(entity);
	}

	public static Identifier getId(Potion potion) {
		return Registries.POTION.getId(potion);
	}

	public static Identifier getId(SoundEvent soundEvent) {
		return soundEvent.getId();
	}

	public static Identifier getId(TagKey<?> tag) {
		return tag.id();
	}

	public static Identifier getId(RegistryKey<?> key) {
		return key.getValue();
	}

	public static Identifier getId(Advancement advancement) {
		return advancement.getId();
	}

	public static Identifier getId(RegistryEntry<?> entry) {
		return getId(entry.getKey().orElseThrow());
	}

	// NAMESPACE

	public static String getNamespace(Item item) {
		return getId(item).getNamespace();
	}

	public static String getNamespace(ItemConvertible item) {
		return getNamespace(item.asItem());
	}

	public static String getNamespace(Block block) {
		return getId(block).getNamespace();
	}

	public static String getNamespace(Fluid fluid) {
		return getId(fluid).getNamespace();
	}

	public static String getNamespace(EntityType<?> entity) {
		return getId(entity).getNamespace();
	}

	public static String getNamespace(Potion potion) {
		return getId(potion).getNamespace();
	}

	public static String getNamespace(SoundEvent soundEvent) {
		return getId(soundEvent).getNamespace();
	}

	public static String getNamespace(Advancement advancement) {
		return getId(advancement).getNamespace();
	}

	public static String getNamespace(TagKey<?> tag) {
		return getId(tag).getNamespace();
	}

	public static String getNamespace(RegistryKey<?> key) {
		return getId(key).getNamespace();
	}

	public static <T> String getNamespace(RegistryEntry<T> entry) {
		return getId(entry).getNamespace();
	}

	// PATH

	public static String getPath(Item item) {
		return getId(item).getPath();
	}

	public static String getPath(ItemConvertible item) {
		return getPath(item.asItem());
	}

	public static String getPath(Block block) {
		return getId(block).getPath();
	}

	public static String getPath(Fluid fluid) {
		return getId(fluid).getPath();
	}

	public static String getPath(EntityType<?> entity) {
		return getId(entity).getPath();
	}

	public static String getPath(Potion potion) {
		return getId(potion).getPath();
	}

	public static String getPath(SoundEvent soundEvent) {
		return getId(soundEvent).getPath();
	}

	public static String getPath(Advancement advancement) {
		return getId(advancement).getPath();
	}

	public static String getPath(TagKey<?> tag) {
		return getId(tag).getPath();
	}

	public static String getPath(RegistryKey<?> key) {
		return getId(key).getPath();
	}

	public static <T> String getPath(RegistryEntry<T> entry) {
		return getId(entry).getPath();
	}
	
	// COMBINED (namespace.path)

	public static String getFullId(Item item) {
		return format(getId(item));
	}

	public static String getFullId(ItemConvertible item) {
		return getFullId(item.asItem());
	}

	public static String getFullId(Block block) {
		return format(getId(block));
	}

	public static String getFullId(Fluid fluid) {
		return format(getId(fluid));
	}

	public static String getFullId(EntityType<?> entity) {
		return format(getId(entity));
	}

	public static String getFullId(Potion potion) {
		return format(getId(potion));
	}

	public static String getFullId(SoundEvent soundEvent) {
		return format(getId(soundEvent));
	}

	public static String getFullId(Advancement advancement) {
		return format(getId(advancement));
	}

	public static String getFullId(TagKey<?> tag) {
		return format(getId(tag));
	}

	public static String getFullId(RegistryKey<?> key) {
		return format(getId(key));
	}

	public static <T> String getFullId(RegistryEntry<T> entry) {
		return format(getId(entry));
	}

	// helper

	private static String format(Identifier id) {
		return id.getNamespace() + "." + id.getPath();
	}
}