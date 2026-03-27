package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.CookIt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.block.Block.NOTIFY_LISTENERS;

public class Container extends BlockEntity implements DefaultedInventory {
    public static final String CONTAINER_KEY = "Container";

    protected final DefaultedList<ItemStack> items;

    public Container(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, int size) {
        super(blockEntity, pos, state);
        this.items = DefaultedList.ofSize(size, ItemStack.EMPTY);
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

    /**
     * Finds the index of the first empty slot.
     * Returns -1 if not found.
     */
    public int firstEmpty() {
        for (int i = 0; i < getItems().size(); i++) {
            if (getStack(i).isEmpty()) return i;
        }
        return -1;
    }

    public boolean fillFirst(PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) return false;
        int available = firstEmpty();
        if (available == -1) return false;
        ItemStack inserted = player.isCreative() ? stack.copyWithCount(1) : stack.split(1);
        setStack(available, inserted);
        return true;
    }

    public ItemStack retrieve() {
        return retrieve(item -> item != Items.AIR);
    }

    public ItemStack retrieve(Predicate<Item> exclusions) {
        for (int i = items.size() - 1; i >= 0; i--) {
            ItemStack stack = getStack(i);
            if (exclusions.test(stack.getItem())) {
                this.markDirty();
                if (world != null) {
                    CookIt.LOGGER.info("hiiii");
                    world.updateListeners(pos, getCachedState(), getCachedState(), NOTIFY_LISTENERS);
                }
                return stack;
            }
        }
        return ItemStack.EMPTY;
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

    // Puts the Container NBT out into CONTAINER_KEY
    @Override
    public void setStackNbt(ItemStack stack) {
        NbtCompound nbt = this.createNbt();

        NbtList items = nbt.getList("Items",  NbtElement.COMPOUND_TYPE).copy();
        nbt.remove("Items");

        stack.getOrCreateSubNbt(CONTAINER_KEY).put("Items", items);
        BlockItem.setBlockEntityNbt(stack, this.getType(), nbt);
    }

    @Nullable
    public static NbtCompound getContainerNbt(ItemStack container) {
        return container.getSubNbt(CONTAINER_KEY);
    }

    public static NbtList getItemList(ItemStack container) {
        NbtCompound nbt = getContainerNbt(container);
        if (nbt == null) return new NbtList();
        return nbt.getList("Items", NbtElement.COMPOUND_TYPE);
    }

    public static DefaultedList<ItemStack> getItems(ItemStack container) {
        NbtCompound nbt = container.getSubNbt(CONTAINER_KEY);

        if (nbt == null) return DefaultedList.ofSize(1, ItemStack.EMPTY);
        CookIt.LOGGER.info(nbt.toString());

        NbtList containerNbt = getItemList(container);
        DefaultedList<ItemStack> items = DefaultedList.ofSize(containerNbt.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, items);
        CookIt.LOGGER.info(items.toString());
        return items;
    }

    public static final Text SINGLE_ITEM = Text.literal("Item:").formatted(Formatting.GRAY);
    public static final Text MULTIPLE_ITEMS = Text.literal("Items:").formatted(Formatting.GRAY);

    public static void appendTooltip(ItemStack container, List<Text> tooltip, Predicate<Item> exclusions) {
        int startSize = tooltip.size();
        for (ItemStack stack : getItems(container)) {
            if (exclusions.test(stack.getItem())) {
                tooltip.add(stack.getName().copy().formatted(Formatting.BLUE));
            }
        }

        int size = tooltip.size() - startSize;
        if (size == 0) return;

    tooltip.add(startSize, size > 1 ? SINGLE_ITEM : MULTIPLE_ITEMS);
    }

    public static void writeTo(ItemStack container, List<ItemStack> stacks) {
        Inventories.writeNbt(container.getOrCreateSubNbt(CONTAINER_KEY), DefaultedList.copyOf(ItemStack.EMPTY, stacks.toArray(new ItemStack[0])));
    }

    public static void addTo(ItemStack container, ItemStack stack) {
        if (stack.isEmpty()) return;

        NbtList list = getItemList(container);
        NbtCompound compound = new NbtCompound();

        compound.putByte("Slot", (byte) (list.size() + 1));
        stack.writeNbt(compound);
        list.add(compound);
        container.getOrCreateSubNbt(CONTAINER_KEY).put("Items", list);
    }

    public static void onPlaced(World world, BlockPos pos, ItemStack itemStack) {
        if (world.isClient) return;
        Container entity = (Container) world.getBlockEntity(pos);
        if (entity == null) return;
        NbtCompound nbt = getContainerNbt(itemStack);
        if (nbt != null) {
            Inventories.readNbt(nbt, entity.items);
        }
    }
}