package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Container extends BlockEntity implements DefaultedInventory {
    public static final String CONTAINER_KEY = "Container";
    protected final DefaultedList<ItemStack> items;

    public Container(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, int size) {
        super(blockEntity, pos, state);
        this.items = DefaultedList.ofSize(size);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        items.clear();
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public ItemStack retrieve(Predicate<Item> exclusions) {
        for (ItemStack stack : items) {
            if (exclusions.test(stack.getItem())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack retrieve() {
        return retrieve(item -> true);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public ActionResult dropAsContainer(PlayerEntity player, World world, Block block, BlockPos pos) {
        ItemStack defaultStack = block.asItem().getDefaultStack();
        if (!this.isEmpty()) {
            this.setStackNbt(defaultStack);
        }
        player.getInventory().offerOrDrop(defaultStack);
        world.breakBlock(pos, false);
        return ActionResult.SUCCESS;
    }

    public static NbtList getContainerNbt(ItemStack container) {
        NbtCompound nbt = container.getSubNbt(CONTAINER_KEY);
        if (nbt == null) return new NbtList();
        return nbt.getList("Items", NbtElement.COMPOUND_TYPE);
    }

    public static boolean isContainer(ItemStack container) {
        return container.getSubNbt(CONTAINER_KEY) != null;
    }

    public static List<ItemStack> getItems(ItemStack container) {
        NbtList nbt = getContainerNbt(container);
        if (nbt.isEmpty()) return Collections.emptyList();
        List<ItemStack> stackList = new ArrayList<>();
        for (NbtElement element : nbt) {
            ItemStack stack = ItemStack.fromNbt((NbtCompound) element);
            if (!stack.isEmpty()) stackList.add(stack);
        }
        return stackList;
    }

    public static void appendToolTip(ItemStack container, List<Text> tooltip, Predicate<Item> exclusions) {
        int startSize = tooltip.size();
        for (ItemStack stack :getItems(container)) {
            if (exclusions.test(stack.getItem())) {
                tooltip.add(stack.getName().copy().formatted(Formatting.BLUE));
            }
        }

        int size = tooltip.size() - startSize;
        if (size == 0) return;

        tooltip.add(startSize, Text.literal(size > 1 ? "Items" : "Item"));
    }

    public static void writeTo(ItemStack container, List<ItemStack> stacks) {
        NbtList list = new NbtList();
        NbtCompound collected = new NbtCompound();
        for (int i = 0; i < stacks.size(); i++) {
            NbtCompound compound = new NbtCompound();
            compound.putByte("Slot", (byte) i);
            stacks.get(i).writeNbt(compound);
            list.add(compound);
        }
        collected.put("Items", list);

        container.getOrCreateSubNbt("BlockEntityTag").put(CONTAINER_KEY, collected);
    }

    public static void addTo(ItemStack container, ItemStack stack) {
        NbtCompound root = BlockItem.getBlockEntityNbt(container);
        if (root == null) return;

        NbtCompound nbt = root.getCompound(CONTAINER_KEY);
        NbtList list = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
        NbtCompound compound = new NbtCompound();
        compound.putByte("Slot", (byte) (list.size() + 1));
        stack.writeNbt(compound);
        list.add(compound);
        nbt.put("Items", list);
    }
}