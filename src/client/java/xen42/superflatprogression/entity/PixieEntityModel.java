package xen42.superflatprogression.entity;

import org.joml.Vector3f;

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
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart right_wing;
	private final ModelPart left_wing;
	private final ModelPart wing_base;

	private float bodyStartY;
	private float bodyStartZ;

	public PixieEntityModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.wing_base = this.body.getChild("wing_base");
		this.left_wing = wing_base.getChild("left_wing");
		this.right_wing = wing_base.getChild("right_wing");

		bodyStartY = this.body.pivotY;
		bodyStartZ = this.body.pivotZ;
	}
	
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(12, 3).cuboid(-2.0F, -4.0F, 1.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, -4.0F));

		ModelPartData wing_base = body.addChild("wing_base", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -2.0F, -0.8727F, 0.0F, 0.0F));

		ModelPartData left_wing = wing_base.addChild("left_wing", ModelPartBuilder.create().uv(-10, 22).cuboid(-7.0F, 1.0F, -5.0F, 7.0F, 0.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		ModelPartData right_wing = wing_base.addChild("right_wing", ModelPartBuilder.create().uv(-10, 22).mirrored().cuboid(0.0F, 1.0F, 2.0F, 7.0F, 0.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.0F, -6.0F, -7.0F, 0.0F, 0.0F, -0.7854F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void setAngles(PixieEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float k = animationProgress * 2f * (float)Math.PI / 20; // 1 cycle per second
		this.right_wing.roll = 0.2f * (float)Math.sin(k) - (float)Math.PI / 4f;
		this.left_wing.roll = -this.right_wing.roll;

		body.xScale = 0.5f;
		body.yScale = 0.5f;
		body.zScale = 0.5f;
	}
}
