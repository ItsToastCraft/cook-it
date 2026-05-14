package dev.toasttextures.cookit.client.sound;

import dev.toasttextures.cookit.block.entity.CookingStatus;
import dev.toasttextures.cookit.block.entity.FryerEntity;
import dev.toasttextures.cookit.registries.CookItSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class FryerSoundInstance extends AbstractTickableSoundInstance {
    private final FryerEntity fryer;

    public FryerSoundInstance(FryerEntity fryer) {
        super(CookItSounds.FRYER_WORKING, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.fryer = fryer;
        this.looping = true;
        this.delay = 24;
        this.volume = 0.75F;
        BlockPos pos = fryer.getBlockPos();
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
    }

    @Override
    public boolean canPlaySound() {
        return fryer.getStatus() == CookingStatus.PROCESSING;
    }

    @Override
    public void tick() {
        this.volume = Mth.clamp(1 - fryer.getProgressRatio(), 0.6f, 1.0f);

        if (this.fryer.isRemoved()) {
            this.stop();
        }
    }
}