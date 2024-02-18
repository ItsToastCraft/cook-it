package toast.cook_it.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import toast.cook_it.CookIt;
import toast.cook_it.block.microwave.MicrowaveEntity;

public class ModScreenHandlers {
    public static final ScreenHandlerType<MicrowaveScreenHandler> MICROWAVE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CookIt.MOD_ID, "microwaving"),
                    new ExtendedScreenHandlerType<>(MicrowaveScreenHandler::new));

    public static void registerScreenHandlers() {
        CookIt.LOGGER.info("Cookin' Screen Handlers with " + CookIt.MOD_ID);
    }
}