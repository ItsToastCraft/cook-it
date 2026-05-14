package dev.toasttextures.cookit.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import dev.toasttextures.cookit.CookIt;

public class CookItSounds {
    public static final SoundEvent MICROWAVE_WORKING = registerSound("microwave_working");
    public static final SoundEvent MICROWAVE_BEEP = registerSound("microwave_beep");
    public static final SoundEvent MICROWAVE_OPEN = registerSound("microwave_open");
    public static final SoundEvent MICROWAVE_CLOSE = registerSound("microwave_close");

    public static final SoundEvent FRYER_WORKING = registerSound("fryer_working");

    private static SoundEvent registerSound(String name) {
        ResourceLocation id = CookIt.idOf(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void register() {}
}