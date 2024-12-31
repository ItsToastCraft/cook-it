package com.toast.cookit;

import com.toast.cookit.block.containers.cutting_board.CuttingBoard;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.toast.cookit.registries.*;

import java.util.List;

public class CookIt implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "cook-it";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier locate(String name) {
        return new Identifier(MOD_ID, name);
    }

    public static final DefaultParticleType OIL_PARTICLE = FabricParticleTypes.simple();

    public static final List<String> SUPPORTED_WOOD_TYPES = List.of(new String[]{"oak", "acacia", "cherry", "crimson", "dark_oak", "spruce", "jungle", "birch", "mangrove", "warped"});
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Baking pastries...");
        CookItItems.registerItems();
        CookItBlocks.registerBlocks();
        CookItRecipes.registerRecipes();
        CookItSounds.registerSounds();
        CookItBlockEntities.registerEntities();
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "oil"), OIL_PARTICLE);

        Registry.register(Registries.ITEM_GROUP, new Identifier(CookIt.MOD_ID, "items"), CookItItems.COOK_IT_GROUP);
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() instanceof CuttingBoard cuttingBoard) {
                return cuttingBoard.pickUpCookingBoardItems(state, world, pos, player);
            }
            return false;
        });
    }
}