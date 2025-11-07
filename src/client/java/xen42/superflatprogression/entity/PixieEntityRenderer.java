package xen42.superflatprogression.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionClient;
import xen42.superflatprogression.entities.PixieEntity;

public class PixieEntityRenderer extends MobEntityRenderer<PixieEntity, PixieEntityModel> {

    public PixieEntityRenderer(Context context) {
        super(context, new PixieEntityModel(context.getPart(SuperflatProgressionClient.MODEL_PIXIE_LAYER)), 0.3f);
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

    @Override
    public void render(PixieEntity pixieEntity, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();
		this.model.handSwingProgress = this.getHandSwingProgress(pixieEntity, tickDelta);
		this.model.riding = pixieEntity.hasVehicle();
		this.model.child = pixieEntity.isBaby();
		float h = MathHelper.lerpAngleDegrees(tickDelta, pixieEntity.prevBodyYaw, pixieEntity.bodyYaw);
		float j = MathHelper.lerpAngleDegrees(tickDelta, pixieEntity.prevHeadYaw, pixieEntity.headYaw);
		float k = j - h;
		if (pixieEntity.hasVehicle() && pixieEntity.getVehicle() instanceof LivingEntity) {
			LivingEntity livingEntity2 = (LivingEntity)pixieEntity.getVehicle();
			h = MathHelper.lerpAngleDegrees(tickDelta, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
			k = j - h;
			float l = MathHelper.wrapDegrees(k);
			if (l < -85.0F) {
				l = -85.0F;
			}

			if (l >= 85.0F) {
				l = 85.0F;
			}

			h = j - l;
			if (l * l > 2500.0F) {
				h += l * 0.2F;
			}

			k = j - h;
		}

		float m = MathHelper.lerp(tickDelta, pixieEntity.prevPitch, pixieEntity.getPitch());
		if (shouldFlipUpsideDown(pixieEntity)) {
			m *= -1.0F;
			k *= -1.0F;
		}

		if (pixieEntity.isInPose(EntityPose.SLEEPING)) {
			Direction direction = pixieEntity.getSleepingDirection();
			if (direction != null) {
				float n = pixieEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
				matrixStack.translate(-direction.getOffsetX() * n, 0.0F, -direction.getOffsetZ() * n);
			}
		}

		float animationProgress = this.getAnimationProgress(pixieEntity, tickDelta);
		this.setupTransforms(pixieEntity, matrixStack, animationProgress, h, tickDelta);
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		this.scale(pixieEntity, matrixStack, tickDelta);
		matrixStack.translate(0.0F, -1.501F, 0.0F);
		float n = 0.0F;
		float o = 0.0F;
		if (!pixieEntity.hasVehicle() && pixieEntity.isAlive()) {
			n = pixieEntity.limbAnimator.getSpeed(tickDelta);
			o = pixieEntity.limbAnimator.getPos(tickDelta);
			if (pixieEntity.isBaby()) {
				o *= 3.0F;
			}

			if (n > 1.0F) {
				n = 1.0F;
			}
		}

		this.model.animateModel(pixieEntity, o, n, tickDelta);
		this.model.setAngles(pixieEntity, o, n, animationProgress, k, m);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		boolean bl = this.isVisible(pixieEntity);
		boolean bl2 = !bl && !pixieEntity.isInvisibleTo(minecraftClient.player);
		boolean bl3 = minecraftClient.hasOutline(pixieEntity);

        // All this just to insert this part into it here
        RenderLayer translucent = RenderLayer.getEntityTranslucent(this.getTexture(pixieEntity));
        VertexConsumer consumer = vertexConsumerProvider.getBuffer(translucent);
        var alpha = 0.4f * (float)Math.sin(animationProgress / 6f) + 0.5f;
        this.getModel().render(matrixStack, consumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, alpha);

		matrixStack.pop();

        if (this.hasLabel(pixieEntity)) {
			this.renderLabelIfPresent(pixieEntity, pixieEntity.getDisplayName(), matrixStack, vertexConsumerProvider, light);
		}
    }
}
