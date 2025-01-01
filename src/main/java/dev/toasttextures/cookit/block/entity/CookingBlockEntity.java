package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.block.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class CookingBlockEntity extends BlockEntity implements ImplementedInventory {
    protected DefaultedList<ItemStack> items;


    public CookingBlockEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, int invSize) {
        super(blockEntity, pos, state);
        this.items = DefaultedList.ofSize(invSize, ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(DefaultedList<ItemStack> items) { this.items = items;}

    @Override
    public void readNbt(NbtCompound nbt) {
        this.items.clear();
        super.readNbt(nbt);

        Inventories.readNbt(nbt, this.items);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.items);

        super.writeNbt(nbt);
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

    public boolean isContainer(ItemStack item) {
        NbtList nbtList = item.getOrCreateSubNbt("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE);

        return !nbtList.isEmpty();
    }

    public ArrayList<ItemStack> getContainerItems(ItemStack container) {

        ArrayList<ItemStack> itemStackList = new ArrayList<>();
        NbtCompound nbt = container.getSubNbt("BlockEntityTag");
        if (nbt != null && nbt.contains("Items")) {
            NbtList itemsTag = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            for (int j = 0; j < itemsTag.size(); j++) {
                NbtCompound itemTag = itemsTag.getCompound(j);
                ItemStack itemStack = ItemStack.fromNbt(itemTag);

                itemStackList.add(itemStack);
            }
        }
        return itemStackList;
    }
}