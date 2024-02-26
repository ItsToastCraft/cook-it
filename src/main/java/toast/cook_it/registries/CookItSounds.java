package toast.cook_it.registries;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;

public class CookItSounds {
    public static final Identifier MICROWAVE_SOUND = new Identifier(CookIt.MOD_ID, "microwave");
    public static final SoundEvent MICROWAVE_SOUND_EVENT = registerSound(MICROWAVE_SOUND);
    public static final Identifier MICROWAVE_BEEP = new Identifier(CookIt.MOD_ID, "microwave_beep");
    public static final SoundEvent MICROWAVE_BEEP_EVENT = registerSound(MICROWAVE_BEEP);


    private static SoundEvent registerSound(Identifier id) {
        SoundEvent soundEvent = SoundEvent.of(id);
        return Registry.register(Registries.SOUND_EVENT, id, soundEvent);

    }
    public static void registerSounds() {

    }
}
