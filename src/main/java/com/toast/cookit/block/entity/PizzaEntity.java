package com.toast.cookit.block.entity;

import com.toast.cookit.registries.CookItBlockEntities;
import com.toast.cookit.registries.CookItBlocks;
import com.toast.cookit.registries.CookItItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

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
        int slices = 4;
        this.toppings.clear();
        if (!stack.isEmpty() && stack.getNbt() != null && stack.getNbt().contains("BlockEntityTag")) {
            NbtCompound tag = stack.getNbt().getCompound("BlockEntityTag");
            if (tag.contains("toppings", NbtElement.LIST_TYPE)) {
                this.toppings = tag.getList("toppings", NbtElement.STRING_TYPE).copy();
            }
            if (tag.contains("sliceCount", NbtElement.INT_TYPE)) {
                slices = tag.getInt("sliceCount");
            }
        }

        if (stack.getItem() == CookItItems.PIZZA_SLICE) {
            slices = 1;
            NbtCompound tag = stack.getNbt();
            if (tag != null && tag.contains("toppings", NbtElement.LIST_TYPE)) {
                this.toppings = tag.getList("toppings", NbtElement.STRING_TYPE).copy();
            }
        }

        this.isCooked = stack.getItem() != CookItBlocks.UNCOOKED_PIZZA.asItem();
        this.sliceCount = slices;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.toppings = nbt.getList("toppings", NbtElement.STRING_TYPE);
        this.isCooked = nbt.getBoolean("isCooked");
        this.sliceCount = nbt.getInt("sliceCount");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("toppings", toppings);
        nbt.putBoolean("isCooked", isCooked);
        nbt.putInt("sliceCount", sliceCount);
        super.writeNbt(nbt);
    }

    public NbtList getToppings() {
        return toppings;
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
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}

