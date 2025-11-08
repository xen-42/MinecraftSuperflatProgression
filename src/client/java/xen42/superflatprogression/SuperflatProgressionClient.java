package xen42.superflatprogression;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.entity.PixieEntityModel;
import xen42.superflatprogression.entity.PixieEntityRenderer;
import xen42.superflatprogression.screen.GrinderHandledScreen;
import xen42.superflatprogression.screen.ScrollCraftingHandledScreen;

public class SuperflatProgressionClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_PIXIE_LAYER = new EntityModelLayer(Identifier.of(SuperflatProgression.MOD_ID, "pixie"), "main");
	
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(SuperflatProgressionBlocks.MAGIC_TORCH, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SuperflatProgressionBlocks.WALL_MAGIC_TORCH, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SuperflatProgressionBlocks.MAGIC_LANTERN, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SuperflatProgressionBlocks.GRINDER, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SuperflatProgressionBlocks.GRASS_SLAB, RenderLayer.getCutout());

		ParticleFactoryRegistry.getInstance().register(SuperflatProgression.MAGIC_TORCH_PARTICLE, SuperflatProgressionParticleFactory::new);
		ParticleFactoryRegistry.getInstance().register(SuperflatProgression.PIXIE_PARTICLE, SuperflatProgressionParticleFactory::new);

		HandledScreens.register(SuperflatProgression.SCROLL_CRAFTING_SCREEN_HANDLER, ScrollCraftingHandledScreen::new);
		HandledScreens.register(SuperflatProgression.GRINDER_SCREEN_HANDLER, GrinderHandledScreen::new);

		EntityRendererRegistry.register(SuperflatProgression.PIXIE_ENTITY, context -> new PixieEntityRenderer(context));
		EntityModelLayerRegistry.registerModelLayer(MODEL_PIXIE_LAYER, PixieEntityModel::getTexturedModelData);

		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
			world != null && pos != null
			? BiomeColors.getGrassColor(world, pos)
			// Default grass colour
			: GrassColors.getColor(0.5, 1.0),
			SuperflatProgressionBlocks.GRASS_SLAB
		);

		ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
			GrassColors.getColor(0.5, 1.0),
			SuperflatProgressionBlocks.GRASS_SLAB
		);
	}
}