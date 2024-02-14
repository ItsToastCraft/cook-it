package toast.cook_it;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toast.cook_it.registries.CookItBlockEntities;
import toast.cook_it.registries.CookItBlocks;
import toast.cook_it.registries.CookItItems;

public class CookIt implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "cook-it";
    public static final Logger LOGGER = LoggerFactory.getLogger("cook-it");

    public static Identifier locate(String name) {
        return new Identifier(MOD_ID, name);
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Baking pastries...");
        CookItItems.registerItems();
        CookItBlocks.registerBlocks();
        CookItBlockEntities.registerEntities();

    }
}