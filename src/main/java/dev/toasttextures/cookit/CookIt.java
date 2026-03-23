package dev.toasttextures.cookit;

import dev.toasttextures.cookit.block.containers.CuttingBoard;
import dev.toasttextures.cookit.block.entity.CuttingBoardEntity;
import dev.toasttextures.cookit.registries.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CookIt implements ModInitializer {
    public static final String MOD_ID = "cook-it";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier idOf(String name) {
        return new Identifier(MOD_ID, name);
    }

    public static final DefaultParticleType OIL_PARTICLE = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        CookItItems.register();
        CookItBlocks.register();
        CookItRecipes.register();
        CookItSounds.register();
        CookItTags.register();
        CookItBlockEntities.registerEntities();
        Registry.register(Registries.PARTICLE_TYPE, idOf("oil"), OIL_PARTICLE);

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() instanceof CuttingBoard cuttingBoard) {
                return cuttingBoard.resetRecipe(world, (CuttingBoardEntity) blockEntity);
            }
            return true;
        });
    }

    public static final Map<Direction, Float> DIRECTION_TO_FLOAT = Map.of(
            Direction.NORTH, 0.0f,
            Direction.SOUTH, 180.0f,
            Direction.EAST, 90.0f,
            Direction.WEST, 270.0f
    );
}