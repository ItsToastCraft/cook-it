package dev.toasttextures.cookit.registries;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.CookIt;

public class CookItSounds {
    public static final SoundEvent MICROWAVE_WORKING = registerSound("microwave_working");
    public static final SoundEvent MICROWAVE_BEEP = registerSound("microwave_beep");

    private static SoundEvent registerSound(String name) {
        Identifier id = CookIt.idOf(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {}
}