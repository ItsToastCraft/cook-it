package dev.toasttextures.cookit.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class OilParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteSet;

    protected OilParticle(ClientWorld level, double x, double y, double z, SpriteProvider spriteSet) {
        super(level, x, y, z, 0.0, 0.0, 0.0);

        this.velocityMultiplier = 0.1F;
        this.scale *= 0.125F;
        this.maxAge = 10;
        this.gravityStrength = 0.25f;
        this.spriteSet = spriteSet;
        this.setSpriteForAge(this.spriteSet);

        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public void tick() {
        this.setSpriteForAge(this.spriteSet);
        super.tick();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType effect, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new OilParticle(level, x, y, z, this.sprites);
        }
    }
}