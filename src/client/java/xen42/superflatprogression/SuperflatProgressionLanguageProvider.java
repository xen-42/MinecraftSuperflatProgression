package xen42.superflatprogression;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.structure.Structure;

public abstract class SuperflatProgressionLanguageProvider extends FabricLanguageProvider {
	protected final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;
	
    public SuperflatProgressionLanguageProvider(FabricDataOutput output, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
    	super(output, languageCode);
    	this.registriesFuture = registryLookup;
    }

	public void generateTranslations(TranslationBuilder translationBuilder) {
		generate(registriesFuture.join(), new ModTranslationBuilder(translationBuilder));
	}
	
	public String processValue(String value) {
		return value;
	}

	public abstract void generate(RegistryWrapper.WrapperLookup registryLookup, ModTranslationBuilder translationBuilder);
	
	public class ModTranslationBuilder implements TranslationBuilder {
		private final TranslationBuilder original;
		
		public ModTranslationBuilder(TranslationBuilder original) {
			this.original = original;
		}
		
		@Override
		public void add(String key, String value) {
			original.add(key, processValue(value));
		}
		
		public void add(TagKey<?> key, String value) {
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

			add(stringBuilder.toString(), value);
		}
		
		public void addTags(String value, TagKey<?>... keys) {
			for (TagKey<?> key : keys) {
				add(key, value);
			}
		}
		
		public void add(GameRules.Key<?> key, String value) {
			add(key.getTranslationKey(), value);
		}
		
		public void add(GameRules.Key<?> key, String title, String description) {
			add(key.getTranslationKey(), title);
			add(key.getTranslationKey() + ".description", description);
		}

		@SuppressWarnings("deprecation")
		public void add(Fluid fluid, String value) {
			RegistryKey<Fluid> key = fluid.getRegistryEntry().registryKey();
			add("block." + key.getValue().getNamespace() + "." + key.getValue().getPath(), value);
		}

		public void addVillagerProfession(RegistryKey<VillagerProfession> key, String value) {
			add("entity.minecraft.villager." + key.getValue().getPath(), value);
		}

		public void addFilledMap(TagKey<Structure> structure, String value) {
			add("filled_map." + structure.id().getNamespace() + "." + structure.id().getPath(), value);
		}
	}
	
	public static class English extends SuperflatProgressionLanguageProvider {

		public English(FabricDataOutput output, String languageCode, CompletableFuture<WrapperLookup> registryLookup) {
			super(output, languageCode, registryLookup);
		}

		public English(FabricDataOutput output, CompletableFuture<WrapperLookup> registryLookup) {
			this(output, "en_us", registryLookup);
		}

		@Override
		public void generate(WrapperLookup registryLookup, ModTranslationBuilder translationBuilder) {
			translationBuilder.add(SuperflatProgressionItems.ESSENCE, "Pixie Dust");
			translationBuilder.add(SuperflatProgressionItems.ENRICHED_BONEMEAL, "Enriched Bone Meal");
			translationBuilder.add(SuperflatProgressionItems.FIRE_STARTER, "Fire Starter");
			translationBuilder.add(SuperflatProgressionItems.PARCHMENT, "Parchment");
			translationBuilder.add(SuperflatProgressionItems.SCROLL_CLEAR_WEATHER, "Clear Weather Scroll");
			translationBuilder.add(SuperflatProgressionItems.SCROLL_RAIN, "Rain Scroll");
			translationBuilder.add(SuperflatProgressionItems.SCROLL_THUNDER, "Thunder Scroll");
			translationBuilder.add(SuperflatProgressionItems.SCROLL_TRADE, "Trade Scroll");
			translationBuilder.add(SuperflatProgressionItems.DIAMOND_SHARD, "Diamond Shard");
			translationBuilder.add(SuperflatProgressionItems.PIXIE_SPAWN_EGG, "Pixie Spawn Egg");

			translationBuilder.add(SuperflatProgressionBlocks.CHARCOAL_BLOCK, "Block of Charcoal");
			translationBuilder.add(SuperflatProgressionItems.MAGIC_TORCH, "Magic Torch");
			translationBuilder.add(SuperflatProgressionBlocks.MAGIC_LANTERN, "Magic Lantern");
			translationBuilder.add(SuperflatProgressionBlocks.SCROLL_CRAFTING, "Scriptorium");
			translationBuilder.add(SuperflatProgressionBlocks.GRINDER, "Pulverizer");

			translationBuilder.add(SuperflatProgressionBlocks.DIRT_SLAB, "Dirt Slab");
			translationBuilder.add(SuperflatProgressionBlocks.GRASS_SLAB, "Grass Slab");

			translationBuilder.add(SuperflatProgressionTags.ItemTags.PARCHMENTS, "Parchments");
			translationBuilder.add(SuperflatProgressionTags.ItemTags.DIAMOND_GEAR, "Diamond Gear");
			translationBuilder.add(SuperflatProgressionTags.ItemTags.ICES, "Ices");

			translationBuilder.add(SuperflatProgression.PIXIE_ENTITY, "Pixie");

			translationBuilder.add(SuperflatProgressionStatusEffects.MAGIC_TORCH_EFFECT.value(), "Bountiful");
			translationBuilder.add("item.minecraft.potion.effect.magic_torch", "Potion of Pixie Dust");
			translationBuilder.add("item.minecraft.splash_potion.effect.magic_torch", "Splash Potion of Pixie Dust");
			translationBuilder.add("item.minecraft.lingering_potion.effect.magic_torch", "Lingering Potion of Pixie Dust");
			translationBuilder.add("item.minecraft.tipped_arrow.effect.magic_torch", "Arrow of Pixie Dust");
        }
	}
	
	public static class EnglishUpsideDown extends English {
		private static final String NORMAL_CHARS = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_,;.?!/\\'\"";
		private static final String UPSIDE_DOWN_CHARS = " ɐqɔpǝɟᵷɥᴉɾʞꞁɯuodbɹsʇnʌʍxʎzⱯᗺƆᗡƎℲ⅁HIՐꞰꞀWNOԀὉᴚS⟘∩ΛMXʎZ0⥝ᘔƐ߈ϛ9ㄥ86‾'⸵˙¿¡\\/,„";

		public EnglishUpsideDown(FabricDataOutput output, CompletableFuture<WrapperLookup> registryLookup) {
			super(output, "en_ud", registryLookup);
		}

		@Override
		public String processValue(String value) {
			return toUpsideDown(value);
		}

		private static String toUpsideDown(String name) {
			StringBuilder builder = new StringBuilder();

			for (int i = name.length() - 1; i >= 0; i--) {
				if (i > 2 && name.substring(i - 3, i + 1).equals("%1$s")) {
					builder.append(name, i - 3, i + 1);
					i -= 4;
					continue;
				}

				if (i < 0)
					continue;

				char normalChar = name.charAt(i);
				int normalIndex = NORMAL_CHARS.indexOf(normalChar);
				if (normalIndex < 0) {
					builder.append(normalChar);
				} else {
					char upsideDown = UPSIDE_DOWN_CHARS.charAt(normalIndex);
					builder.append(upsideDown);
				}
			}

			return builder.toString();
		}
	}
}
