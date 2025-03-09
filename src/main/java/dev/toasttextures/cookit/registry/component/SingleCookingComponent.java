package dev.toasttextures.cookit.registry.component;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public record SingleCookingComponent(RegistryEntry<Item> item) {
    public static final SingleCookingComponent DEFAULT = new SingleCookingComponent(RegistryEntry.of(Items.AIR));
    public static final Codec<SingleCookingComponent> CODEC = ItemStack.ITEM_CODEC.xmap(SingleCookingComponent::new, singleCookingComponent -> singleCookingComponent.item);
    public static final PacketCodec<RegistryByteBuf, SingleCookingComponent> PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.ITEM).xmap(SingleCookingComponent::new, singleCookingComponent -> singleCookingComponent.item);

    public ItemStack getItem() {
        return new ItemStack(item);
    }

    public boolean isEmpty() {
        return this.getItem().isEmpty();
    }


    public String toString() {
        return "Is: " + this.item;
    }
}
