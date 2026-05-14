package dev.toasttextures.cookit;

import dev.toasttextures.cookit.block.entity.CuttingBoardEntity;
import dev.toasttextures.cookit.data.PizzaToppingReloader;
import dev.toasttextures.cookit.registries.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookIt implements ModInitializer {
    public static final String MOD_ID = "cook-it";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation idOf(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static final SimpleParticleType OIL_PARTICLE = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        CookItItems.register();
        CookItBlocks.register();
        CookItRecipes.register();
        CookItSounds.register();
        CookItTags.register();
        CookItBlockEntities.registerEntities();
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, idOf("oil"), OIL_PARTICLE);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new PizzaToppingReloader());

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (blockEntity instanceof CuttingBoardEntity cuttingBoardEntity) {
                return cuttingBoardEntity.resetRecipe(world);
            }
            return true;
        });
    }

    public static boolean isVanilla(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE);
    }
}