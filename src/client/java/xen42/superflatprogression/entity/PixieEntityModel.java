package xen42.superflatprogression.entity;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import xen42.superflatprogression.entities.PixieEntity;

public class PixieEntityModel extends EntityModel<PixieEntity> {
	private final ModelPart body;
	private final ModelPart right_wing;
	private final ModelPart left_wing;

	private float bodyStartY;
	private float wingStartY;

	public PixieEntityModel(ModelPart root) {
		this.body = root.getChild("body");
		this.right_wing = root.getChild("right_wing");
		this.left_wing = root.getChild("left_wing");

		bodyStartY = this.body.pivotY;
		wingStartY = this.right_wing.pivotY;
	}
	
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(8, 1).cuboid(-3.0F, -15.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F))
		.uv(0, 16).cuboid(-4.0F, -16.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData right_wing = modelPartData.addChild("right_wing", ModelPartBuilder.create().uv(-7, 0).mirrored().cuboid(0.0F, 0.0F, 0.0F, 5.0F, 0.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(2.0F, 9.0F, 0.0F));

		ModelPartData left_wing = modelPartData.addChild("left_wing", ModelPartBuilder.create().uv(-7, 0).cuboid(-5.0F, 0.0F, 0.0F, 5.0F, 0.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 9.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		right_wing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		left_wing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void setAngles(PixieEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float k = animationProgress * 120.32113F * (float) (Math.PI / 180.0);
		this.right_wing.yaw = 0.0F;
		this.right_wing.roll = MathHelper.cos(k) * (float) Math.PI * 0.15F;
		this.left_wing.yaw = this.right_wing.yaw;
		this.left_wing.roll = -this.right_wing.roll;

		var offset = (1f * (float)Math.sin(k / 6f)) - 8f;
		body.pivotY = offset + bodyStartY;
		right_wing.pivotY = offset + wingStartY;
		left_wing.pivotY = offset + wingStartY;
	}
}
