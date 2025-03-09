package dev.toasttextures.cookit.registry;

import com.mojang.serialization.Codec;
import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.registry.component.CookingComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class CookItComponents {
    public static ComponentType<List<String>> TOPPING_COMPONENT;
    public static ComponentType<Integer> SLICE_COUNT_COMPONENT;
    public static ComponentType<Integer> COLOR_COMPONENT;
    public static ComponentType<Integer> CLICKS_COMPONENT;
    public static ComponentType<CookingComponent> COOKING_COMPONENT;
    public static void registerComponents() {
        COOKING_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "items"),
                ComponentType.<CookingComponent>builder().codec(CookingComponent.CODEC).packetCodec(CookingComponent.PACKET_CODEC).build()
        );
        TOPPING_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "toppings"),
                ComponentType.<List<String>>builder().codec(Codec.STRING.listOf()).build()
        );
        SLICE_COUNT_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "slice_count"),
                ComponentType.<Integer>builder().codec(Codec.INT).build()
        );
        COLOR_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "color"),
                ComponentType.<Integer>builder().codec(Codec.INT).build()
        );
        CLICKS_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "clicks"),
                ComponentType.<Integer>builder().codec(Codec.INT).build()
        );

    }
    public static <T> ComponentType<T> register(Identifier id, ComponentType<T> type) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                id,
                type
        );
    }
}
