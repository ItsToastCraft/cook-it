package dev.toasttextures.cookit.registries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class OilParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    protected OilParticle(ClientLevel level, double x, double y, double z,
                          SpriteSet spriteSet) {
        super(level, x, y, z, 0.0, 0.0, 0.0);

        this.friction = 0.1F;

        this.quadSize *= 0.125F;
        this.lifetime = 10;
        this.gravity = 0.25f;
        this.spriteSet = spriteSet;
        this.setSpriteFromAge(this.spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(this.spriteSet);
        super.tick();
    }


    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new OilParticle(level, x, y, z, this.sprites);
        }
    }
}