package dev.toasttextures.cookit.client.sound;

import dev.toasttextures.cookit.block.entity.CookingStatus;
import dev.toasttextures.cookit.block.entity.MicrowaveEntity;
import dev.toasttextures.cookit.registries.CookItSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;

@Environment(EnvType.CLIENT)
public class MicrowaveSoundInstance extends AbstractTickableSoundInstance {
    private final MicrowaveEntity microwave;

    public MicrowaveSoundInstance(MicrowaveEntity microwave) {
        super(CookItSounds.MICROWAVE_WORKING, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.microwave = microwave;
        this.looping = true;
        this.delay = 128;
        this.volume = 0.75F;
        BlockPos pos = microwave.getBlockPos();
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
    }

    @Override
    public boolean canPlaySound() {
        return microwave.getStatus() == CookingStatus.PROCESSING;
    }

    @Override
    public void tick() {
        if (this.microwave.isRemoved()) {
            this.stop();
        }
    }
}