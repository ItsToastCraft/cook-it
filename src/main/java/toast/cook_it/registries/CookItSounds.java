package toast.cook_it.registries;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;

public class CookItSounds {
    public static final SoundEvent MICROWAVE_EVENT = registerSound(new Identifier(CookIt.MOD_ID, "microwave"));


    private static SoundEvent registerSound(Identifier id) {
        SoundEvent soundEvent = SoundEvent.of(id);
        return Registry.register(Registries.SOUND_EVENT, id, soundEvent);

    }
    public static void registerSounds() {

    }
}
