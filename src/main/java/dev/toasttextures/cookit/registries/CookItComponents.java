package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.CookIt;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CookItComponents {
    public static DataComponentType<List<ItemStack>> COOKING_COMPONENT;
    public static DataComponentType<ArrayList<String>> TOPPING_COMPONENT;
    public static DataComponentType<ItemStack> SINGLE_COOKING_COMPONENT;
    public static DataComponentType<Integer> SLICE_COUNT_COMPONENT;
    public static DataComponentType<Integer> COLOR_COMPONENT;
    public static void registerComponents() {

        COOKING_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "items"),
                DataComponentType.<List<ItemStack>>builder().packetCodec(ItemStack.LIST_PACKET_CODEC).build()
        );
        SINGLE_COOKING_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "output"),
                DataComponentType.<ItemStack>builder().packetCodec(ItemStack.PACKET_CODEC).build()
        );

        TOPPING_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "toppings"),
                DataComponentType.<ArrayList<String>>builder().packetCodec(PacketCodecs.STRING.collect(PacketCodecs.toCollection(ArrayList::new))).build()
        );
        SLICE_COUNT_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "slice_count"),
                DataComponentType.<Integer>builder().packetCodec(PacketCodecs.INTEGER).build()
        );
        COLOR_COMPONENT = register(
                Identifier.of(CookIt.MOD_ID, "color"),
                DataComponentType.<Integer>builder().packetCodec(PacketCodecs.INTEGER).build()
        );

    }
    public static <T> DataComponentType<T> register(Identifier id, DataComponentType<T> type) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                id,
                type
        );
    }
}
