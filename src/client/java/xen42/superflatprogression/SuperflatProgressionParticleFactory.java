package xen42.superflatprogression;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.particle.AbstractSlowingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class SuperflatProgressionParticleFactory implements ParticleFactory<DefaultParticleType> {

    private final FabricSpriteProvider spriteProvider;

    public SuperflatProgressionParticleFactory(FabricSpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z,
            double vx, double vy, double vz) {
        return new SuperflatProgressionParticleFactory.CustomParticle(world, x, y, z, spriteProvider);
    }    

    public static class CustomParticle extends AbstractSlowingParticle {
        CustomParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
            super(clientWorld, d, e, f, 0, 0, 0);
            setSprite(spriteProvider);
        }

        @Override
        public ParticleTextureSheet getType() {
            return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
        }

        @Override
        public void move(double dx, double dy, double dz) {
            this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
            this.repositionFromBoundingBox();
        }

        @Override
        public float getSize(float tickDelta) {
            float f = (this.age + tickDelta) / this.maxAge;
            return this.scale * (1.0F - f * f * 0.5F);
        }

        @Override
        public int getBrightness(float tint) {
            float f = (this.age + tint) / this.maxAge;
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            int i = super.getBrightness(tint);
            int j = i & 0xFF;
            int k = i >> 16 & 0xFF;
            j += (int)(f * 15.0F * 16.0F);
            if (j > 240) {
                j = 240;
            }

            return j | k << 16;
        }
    }
}
