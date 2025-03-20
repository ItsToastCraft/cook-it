package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.registry.CookItBlockEntities;
import dev.toasttextures.cookit.registry.CookItBlocks;
import dev.toasttextures.cookit.registry.CookItComponents;
import dev.toasttextures.cookit.registry.CookItItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PizzaEntity extends BlockEntity{

    private NbtList toppings = new NbtList();
    private boolean isCooked = false;
    private int sliceCount = 4;

    public PizzaEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.PIZZA_ENTITY, pos, state);
    }

    public PizzaEntity(BlockPos pos, BlockState state, boolean isCooked) {
        super(CookItBlockEntities.PIZZA_ENTITY, pos, state);
        this.isCooked = isCooked;
    }

    public void readFromItemStack(ItemStack stack) {
        int slices;
        this.toppings.clear();
        if (!stack.isEmpty() && stack.getComponents() != null && stack.getComponents().contains(CookItComponents.TOPPING_COMPONENT)) {
            List<String> toppings = stack.getComponents().getOrDefault(CookItComponents.TOPPING_COMPONENT, new ArrayList<>());
            NbtList toppingsNbt = new NbtList();
            for (String topping : toppings) {
                toppingsNbt.add(NbtString.of(topping)); // Convert each string into NbtString
            }
            this.toppings = toppingsNbt;
        }

            if (stack.getItem() == CookItItems.PIZZA_SLICE) {
                slices = 1;
            }
            else {
                slices = stack.getOrDefault(CookItComponents.SLICE_COUNT_COMPONENT, 4);
            }

        this.isCooked = stack.getItem() != CookItBlocks.UNCOOKED_PIZZA.asItem();
        this.sliceCount = slices;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.toppings = nbt.getList("toppings", NbtElement.STRING_TYPE);
        this.isCooked = nbt.getBoolean("isCooked");
        this.sliceCount = nbt.getInt("sliceCount");
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.put("toppings", toppings);
        nbt.putBoolean("isCooked", isCooked);
        nbt.putInt("sliceCount", sliceCount);
        super.writeNbt(nbt, registryLookup);
    }

    public ArrayList<String> getToppings() {
        return toppings.stream()
                .filter(NbtString.class::isInstance) // Ensure elements are NbtString
                .map(NbtElement::asString) // Convert to String
                .collect(Collectors.toCollection(ArrayList::new)); // Collect into ArrayList
    }

    public void setToppings(List<String> toppings) {
        this.toppings.clear();
        for (String topping : toppings) {
            this.toppings.add(NbtString.of(topping));
        }
    }

    public boolean isCooked() {
        return isCooked;
    }

    public int getSliceCount() {
        return sliceCount;
    }

    public void setSliceCount(int sliceCount) {
        this.sliceCount = sliceCount;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}

