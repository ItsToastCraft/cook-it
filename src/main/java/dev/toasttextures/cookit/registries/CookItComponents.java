package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.CookIt;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CookItComponents {
    public static ComponentType<List<ItemStack>> COOKING_COMPONENT;
    public static ComponentType<ArrayList<String>> TOPPING_COMPONENT;
    public static ComponentType<ItemStack> SINGLE_COOKING_COMPONENT;
    public static ComponentType<Integer> SLICE_COUNT_COMPONENT;
    public static ComponentType<Integer> COLOR_COMPONENT;
    public static void registerComponents() {

        COOKING_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "items"),
                ComponentType.<List<ItemStack>>builder().packetCodec(ItemStack.LIST_PACKET_CODEC).build()
        );
        SINGLE_COOKING_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "output"),
                ComponentType.<ItemStack>builder().packetCodec(ItemStack.PACKET_CODEC).build()
        );

        TOPPING_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "toppings"),
                ComponentType.<ArrayList<String>>builder().packetCodec(PacketCodecs.STRING.collect(PacketCodecs.toCollection(ArrayList::new))).build()
        );
        SLICE_COUNT_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "slice_count"),
                ComponentType.<Integer>builder().packetCodec(PacketCodecs.INTEGER).build()
        );
        COLOR_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "color"),
                ComponentType.<Integer>builder().packetCodec(PacketCodecs.INTEGER).build()
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
