// Made with Blockbench 5.0.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class pixie extends EntityModel<Entity> {
	private final ModelPart body;
	private final ModelPart wing_base;
	private final ModelPart left_wing;
	private final ModelPart right_wing;
	public pixie(ModelPart root) {
		this.body = root.getChild("body");
		this.wing_base = this.body.getChild("wing_base");
		this.left_wing = this.wing_base.getChild("left_wing");
		this.right_wing = this.wing_base.getChild("right_wing");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(12, 3).cuboid(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData wing_base = body.addChild("wing_base", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -5.0F, -0.8727F, 0.0F, 0.0F));

		ModelPartData left_wing = wing_base.addChild("left_wing", ModelPartBuilder.create().uv(-10, 22).cuboid(-7.0F, 1.0F, -5.0F, 7.0F, 0.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		ModelPartData right_wing = wing_base.addChild("right_wing", ModelPartBuilder.create().uv(-10, 22).mirrored().cuboid(0.0F, 1.0F, 2.0F, 7.0F, 0.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.0F, -6.0F, -7.0F, 0.0F, 0.0F, -0.7854F));
		return TexturedModelData.of(modelData, 32, 32);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}