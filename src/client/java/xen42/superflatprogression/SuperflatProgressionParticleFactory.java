package xen42.superflatprogression;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class SuperflatProgressionParticleFactory implements ParticleFactory<DefaultParticleType> {

    private final FabricSpriteProvider spriteProvider;

    public SuperflatProgressionParticleFactory(FabricSpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z,
            double velocityX, double velocityY, double velocityZ) {
        return new SuperflatProgressionParticleFactory.CustomParticle(world, x, y, z, spriteProvider);
    }    

    public static class CustomParticle extends SpriteBillboardParticle {

        protected CustomParticle(ClientWorld clientWorld, double x, double y, double z, FabricSpriteProvider spriteProvider) {
            super(clientWorld, x, y, z);
            setSprite(spriteProvider);
        }

        @Override
        public ParticleTextureSheet getType() {
                return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
        }
    }
    
}
