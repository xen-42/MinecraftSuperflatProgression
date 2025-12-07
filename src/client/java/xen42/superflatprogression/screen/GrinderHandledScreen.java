package xen42.superflatprogression.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionNetworking;
import xen42.superflatprogression.blocks.GrinderBlock;

public class GrinderHandledScreen extends HandledScreen<GrinderScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(SuperflatProgression.MOD_ID, "textures/gui/grinder_gui.png");

    private ButtonWidget button;

    public GrinderHandledScreen(GrinderScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
 
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0.0F, 0.0F, 
            this.backgroundWidth, this.backgroundHeight, 256, 256);
    }
 
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        button.active = handler.hasOutput();
        button.setFocused(false);
    }
 
    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        this.button = ButtonWidget.builder(Text.translatable(SuperflatProgression.MOD_ID + ".grind_button"), b -> {
            ClientPlayNetworking.send(SuperflatProgressionNetworking.GRIND_BUTTON_PRESSED, PacketByteBufs.empty());
        }).dimensions(this.x + 109, this.y + 23, 48, 18).build();
        this.addDrawableChild(button);
    }
}