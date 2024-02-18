package toast.cook_it;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import toast.cook_it.block.ModBlocks;
import toast.cook_it.block.baking_sheet.BakingSheetEntityRenderer;
import toast.cook_it.screen.MicrowaveScreen;
import toast.cook_it.screen.MicrowaveScreenHandler;
import toast.cook_it.screen.ModScreenHandlers;

@Environment(EnvType.CLIENT)
public class CookItClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Here we will put client-only registration code
        BlockEntityRendererRegistry.register(ModBlocks.BAKING_SHEET_ENTITY, BakingSheetEntityRenderer::new);

        HandledScreens.register(ModScreenHandlers.MICROWAVE_SCREEN_HANDLER, MicrowaveScreen::new);
    }
}