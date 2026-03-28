package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.CookIt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;

public class Container extends BlockEntity implements DefaultedInventory {
    public static final String CONTAINER_KEY = "Container";

    protected final NonNullList<ItemStack> items;

    public Container(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, int size) {
        super(blockEntity, pos, state);
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public void load(CompoundTag nbt) {
        items.clear();
        super.load(nbt);
        ContainerHelper.loadAllItems(nbt, items);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        ContainerHelper.saveAllItems(nbt, items);
        super.saveAdditional(nbt);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    /**
     * Finds the index of the first empty slot.
     * Returns -1 if not found.
     */
    public int firstEmpty() {
        if (isEmpty()) return 0;
        for (int i = 0; i < getItems().size(); i++) {
            if (getItem(i).isEmpty()) return i;
        }
        return -1;
    }

    public boolean fillFirst(Player player, ItemStack stack) {
        if (stack.isEmpty()) return false;
        int available = firstEmpty();
        if (available == -1) return false;
        ItemStack inserted = player.isCreative() ? stack.copyWithCount(1) : stack.split(1);
        setItem(available, inserted);
        return true;
    }

    public ItemStack retrieve() {
        return retrieve(item -> item != Items.AIR);
    }

    public ItemStack retrieve(Predicate<Item> exclusions) {
        for (int i = items.size() - 1; i >= 0; i--) {
            ItemStack stack = getItem(i);
            if (exclusions.test(stack.getItem())) {
                this.setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), UPDATE_CLIENTS);
                }
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public InteractionResult dropAsContainer(Player player, Level world, Block block, BlockPos pos) {
        ItemStack defaultStack = block.asItem().getDefaultInstance();
        if (!this.isEmpty()) {
            this.saveToItem(defaultStack);
        }
        player.getInventory().placeItemBackInInventory(defaultStack);
        world.destroyBlock(pos, false);
        return InteractionResult.SUCCESS;
    }

    // Puts the Container NBT out into CONTAINER_KEY
    @Override
    public void saveToItem(ItemStack stack) {
        CompoundTag nbt = this.saveWithoutMetadata();

        ListTag items = nbt.getList("Items",  Tag.TAG_COMPOUND).copy();
        nbt.remove("Items");

        stack.getOrCreateTagElement(CONTAINER_KEY).put("Items", items);
        BlockItem.setBlockEntityData(stack, this.getType(), nbt);
    }

    @Nullable
    public static CompoundTag getContainerNbt(ItemStack container) {
        return container.getTagElement(CONTAINER_KEY);
    }

    public static ListTag getItemList(ItemStack container) {
        CompoundTag nbt = getContainerNbt(container);
        if (nbt == null) return new ListTag();
        return nbt.getList("Items", Tag.TAG_COMPOUND);
    }

    public static NonNullList<ItemStack> getItems(ItemStack container) {
        CompoundTag nbt = container.getTagElement(CONTAINER_KEY);

        if (nbt == null) return NonNullList.withSize(1, ItemStack.EMPTY);
        CookIt.LOGGER.info(nbt.toString());

        ListTag containerNbt = getItemList(container);
        NonNullList<ItemStack> items = NonNullList.withSize(containerNbt.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, items);
        CookIt.LOGGER.info(items.toString());
        return items;
    }

    public static final Component SINGLE_ITEM = Component.literal("Item:").withStyle(ChatFormatting.GRAY);
    public static final Component MULTIPLE_ITEMS = Component.literal("Items:").withStyle(ChatFormatting.GRAY);

    public static void appendTooltip(ItemStack container, List<Component> tooltip) {
        appendTooltip(container, tooltip, item -> item);
    }

    public static void appendTooltip(ItemStack container, List<Component> tooltip, UnaryOperator<Item> exclusions) {
        int startSize = tooltip.size();
        for (ItemStack stack : getItems(container)) {
            if (exclusions.apply(stack.getItem()) != null) {
                tooltip.add(stack.getHoverName().copy().withStyle(ChatFormatting.BLUE));
            }
        }

        int size = tooltip.size() - startSize;
        if (size == 0) return;

    tooltip.add(startSize, size > 1 ? SINGLE_ITEM : MULTIPLE_ITEMS);
    }

    public static void writeTo(ItemStack container, List<ItemStack> stacks) {
        ContainerHelper.saveAllItems(container.getOrCreateTagElement(CONTAINER_KEY), NonNullList.of(ItemStack.EMPTY, stacks.toArray(new ItemStack[0])));
    }

    public static void addTo(ItemStack container, ItemStack stack) {
        if (stack.isEmpty()) return;

        ListTag list = getItemList(container);
        CompoundTag compound = new CompoundTag();

        compound.putByte("Slot", (byte) (list.size() + 1));
        stack.save(compound);
        list.add(compound);
        container.getOrCreateTagElement(CONTAINER_KEY).put("Items", list);
    }

    public static void onPlaced(Level world, BlockPos pos, ItemStack itemStack) {
        if (world.isClientSide) return;
        Container entity = (Container) world.getBlockEntity(pos);
        if (entity == null) return;
        CompoundTag nbt = getContainerNbt(itemStack);
        if (nbt != null) {
            ContainerHelper.loadAllItems(nbt, entity.items);
        }
    }
}