package xen42.superflatprogression;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SuperflatProgressionAdvancementsProvider extends FabricAdvancementProvider {

    protected SuperflatProgressionAdvancementsProvider(FabricDataOutput output) {
        super(output);
    }

    public static Advancement PIXIE_DUST;
    public static Advancement ENRICHED_BONEMEAL;
    public static Advancement CHARCOAL;
    public static Advancement MAGIC_TORCH;
    public static Advancement SCRIPTORIUM;
    public static Advancement SCROLL;
    public static Advancement PULVERIZER;
    public static Advancement END_PORTAL_FRAME_GENERATOR;

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        PIXIE_DUST = register("get_pixie_dust", AdvancementFrame.TASK, null, SuperflatProgressionItems.ESSENCE, consumer);
        ENRICHED_BONEMEAL = register("get_enriched_bone_meal", AdvancementFrame.TASK, PIXIE_DUST, SuperflatProgressionItems.ENRICHED_BONEMEAL, consumer);
        CHARCOAL = register("get_charcoal", AdvancementFrame.TASK, PIXIE_DUST, SuperflatProgressionBlocks.CHARCOAL_BLOCK.asItem(), Items.CHARCOAL, consumer);
        MAGIC_TORCH = register("get_magic_torch", AdvancementFrame.TASK, CHARCOAL, SuperflatProgressionItems.MAGIC_TORCH, consumer);
        SCRIPTORIUM = register("get_scriptorium", AdvancementFrame.TASK, ENRICHED_BONEMEAL, SuperflatProgressionBlocks.SCROLL_CRAFTING, consumer);
        PULVERIZER = register("get_pulverizer", AdvancementFrame.TASK, MAGIC_TORCH, SuperflatProgressionBlocks.GRINDER, consumer);
        END_PORTAL_FRAME_GENERATOR = register("get_end_portal_frame_generator", AdvancementFrame.CHALLENGE, PULVERIZER, SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR, consumer);
        SCROLL = register("get_magic_scroll", AdvancementFrame.CHALLENGE, SCRIPTORIUM, SuperflatProgressionTags.ItemTags.MAGIC_SCROLLS, SuperflatProgressionItems.SCROLL_THUNDER, consumer);
    }

    private Advancement register(String id, AdvancementFrame type, Advancement parent, Block blockItem, Consumer<Advancement> consumer) {
        return register(id, type, parent, blockItem.asItem(), consumer);
    }

    private Advancement register(String id, AdvancementFrame type, Advancement parent, ItemConvertible item, Consumer<Advancement> consumer) {
        return register(id, type, parent, item, null, consumer);
    }

    private Advancement register(String id, AdvancementFrame type, Advancement parent, ItemConvertible item, Item overrideDisplayItem, Consumer<Advancement> consumer) {
        var identifier = Identifier.of(SuperflatProgression.MOD_ID, id);
        var displayItem = overrideDisplayItem == null ? item : overrideDisplayItem;
        var builder = Advancement.Builder.create().display(
            displayItem, 
            Text.translatable(getTitleKey(id)), 
            Text.translatable(getDescriptionKey(id)),
            parent != null ? null : Identifier.of("minecraft", "textures/gui/advancements/backgrounds/stone.png"),
            type,
            false, true, false);
        if (parent != null) {
            builder.parent(parent);
        }
        builder.criterion("has_item_" + id, InventoryChangedCriterion.Conditions.items(item));
        return builder.build(consumer, identifier.toString());
    }

    private Advancement register(String id, AdvancementFrame type, Advancement parent, TagKey<Item> item, Item overrideDisplayItem, Consumer<Advancement> consumer) {
        var identifier = Identifier.of(SuperflatProgression.MOD_ID, id);
        var builder = Advancement.Builder.create().display(
            overrideDisplayItem, 
            Text.translatable(getTitleKey(id)), 
            Text.translatable(getDescriptionKey(id)),
            parent != null ? null : Identifier.of("minecraft", "textures/gui/advancements/backgrounds/stone.png"),
            type,
            false, true, false);
        if (parent != null) {
            builder.parent(parent);
        }
        ItemPredicate predicate = ItemPredicate.Builder.create().tag(item).build();
        builder.criterion("has_tag_" + id, InventoryChangedCriterion.Conditions.items(predicate));
        return builder.build(consumer, identifier.toString());
    }

    public static String getTitleKey(String root) {
        return "advancement.title." + SuperflatProgression.MOD_ID + "." + root;
    }

    public static String getDescriptionKey(String root) {
        return "advancement.description." + SuperflatProgression.MOD_ID + "." + root;
    }
}
