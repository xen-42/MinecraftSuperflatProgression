// Made with Blockbench 5.0.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class pixie extends EntityModel<Entity> {
	private final ModelPart body;
	private final ModelPart right_wing;
	private final ModelPart left_wing;
	public pixie(ModelPart root) {
		this.body = root.getChild("body");
		this.right_wing = root.getChild("right_wing");
		this.left_wing = root.getChild("left_wing");
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
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		right_wing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		left_wing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}