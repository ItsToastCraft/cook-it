package dev.toasttextures.cookit.registry.component;

import com.mojang.serialization.Codec;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record CookingComponent(List<ItemStack> stacks) {
    public static final CookingComponent DEFAULT = new CookingComponent(List.of());
    public static final Codec<CookingComponent> CODEC = ItemStack.CODEC.listOf().xmap(CookingComponent::new, component -> component.stacks);
    public static final PacketCodec<RegistryByteBuf, CookingComponent> PACKET_CODEC = ItemStack.PACKET_CODEC
            .collect(PacketCodecs.toList())
            .xmap(CookingComponent::new, component -> component.stacks);


    public ItemStack get(int index) {
        if (index < 0 || index >= stacks.size()) return ItemStack.EMPTY;
        return this.stacks.get(index);
    }

    public int size() {
        return this.stacks.size();
    }

    public boolean isEmpty() {
        return this.stacks.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o instanceof CookingComponent(List<ItemStack> stacks1) && ItemStack.stacksEqual(this.stacks, stacks1);
        }
    }

    public String toString() {
        return "Contains: " + this.stacks;
    }
}
