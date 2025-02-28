package dev.toasttextures.cookit.registries;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.CookIt;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class OilParticleEffect implements ParticleEffect {
    public static final MapCodec<OilParticleEffect> CODEC = MapCodec.unit(OilParticleEffect::new);
    public static final PacketCodec<RegistryByteBuf, OilParticleEffect> PACKET_CODEC = PacketCodec.unit(new OilParticleEffect());

    @Override
    public ParticleType<?> getType() {
        return CookIt.OIL_PARTICLE;
    }
}