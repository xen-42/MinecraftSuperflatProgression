package xen42.superflatprogression.entity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.GlowSquidEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionClient;
import xen42.superflatprogression.entities.PixieEntity;

public class PixieEntityRenderer extends MobEntityRenderer<PixieEntity, PixieEntityModel> {

    public PixieEntityRenderer(Context context) {
        super(context, new PixieEntityModel(context.getPart(SuperflatProgressionClient.MODEL_PIXIE_LAYER)), 0.2f);
    }

    @Override
    public Identifier getTexture(PixieEntity state) {
        return Identifier.of(SuperflatProgression.MOD_ID, "textures/entity/pixie/pixie.png");
    }

    @Override
    public void scale(PixieEntity state, MatrixStack matrixStack, float f) {
        var scaleFactor = state.getScaleFactor();
        matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
        super.scale(state, matrixStack, f);
    }

    protected int getBlockLight(PixieEntityRenderer glowSquidEntity, BlockPos blockPos) {
		return 12;
	}
}
