package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.block.food_blocks.pizza.PizzaTopping;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PizzaEntity extends BlockEntity {
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
        if (stack.isEmpty() || stack.getNbt() == null) {
            return;
        }
        this.toppings.clear();

        int slices = 1;
        NbtCompound tag = stack.getNbt().getCompound("BlockEntityTag");

        if (tag != null) {
            if (tag.contains("sliceCount", NbtElement.INT_TYPE)) {
                slices = tag.getInt("sliceCount");
            }
            if (tag.contains("toppings", NbtElement.LIST_TYPE)) {
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
        nbt.put("toppings", this.toppings);
        nbt.putBoolean("isCooked", this.isCooked);
        nbt.putInt("sliceCount", this.sliceCount);
        super.writeNbt(nbt);
    }

    public NbtList getToppingsNbt() {
        return this.toppings;
    }
    public List<PizzaTopping> getToppings() {
        NbtCompound tag = new NbtCompound();
        tag.put("toppings", this.toppings);
        return PizzaTopping.fromNbt(tag);
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