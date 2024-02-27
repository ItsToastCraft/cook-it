package toast.cook_it.registries;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;
import toast.cook_it.screen.MicrowaveScreenHandler;

public class CookItScreenHandlers {
    public static final ScreenHandlerType<MicrowaveScreenHandler> MICROWAVE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CookIt.MOD_ID, "microwaving"),
                    new ExtendedScreenHandlerType<>(MicrowaveScreenHandler::new));

    public static void registerScreenHandlers() {
        CookIt.LOGGER.info("Cookin' Screen Handlers with " + CookIt.MOD_ID);
    }
}