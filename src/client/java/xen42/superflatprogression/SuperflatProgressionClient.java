package xen42.superflatprogression;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.render.RenderLayer;

public class SuperflatProgressionClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(SuperflatProgressionBlocks.MAGIC_TORCH, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SuperflatProgressionBlocks.WALL_MAGIC_TORCH, RenderLayer.getCutout());

		ParticleFactoryRegistry.getInstance().register(SuperflatProgression.MAGIC_TORCH_PARTICLE, SuperflatProgressionParticleFactory::new);
	}
}