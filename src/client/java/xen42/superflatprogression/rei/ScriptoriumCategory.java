package xen42.superflatprogression.rei;

import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import xen42.superflatprogression.SuperflatProgressionBlocks;

public class ScriptoriumCategory implements DisplayCategory<ScriptoriumREIDisplay> {

    @Override
    public CategoryIdentifier<? extends ScriptoriumREIDisplay> getCategoryIdentifier() {
        return SuperflatProgressionREIServerPlugin.SCRIPTORIUM_CATEGORY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(SuperflatProgressionBlocks.SCROLL_CRAFTING.getTranslationKey());
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(SuperflatProgressionBlocks.SCROLL_CRAFTING);
    }
    
}
