package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.block.food.pizza.PizzaTopping;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PizzaEntity extends BlockEntity {
    private static final String SLICE_COUNT_KEY = "Slices";
    private static final String COOKED_KEY = "Cooked";

    private ListTag toppings = new ListTag();
    private boolean isCooked;
    private int sliceCount = 4;

    public PizzaEntity(BlockPos pos, BlockState state) {
        this(pos, state, false);
    }

    public PizzaEntity(BlockPos pos, BlockState state, boolean isCooked) {
        super(CookItBlockEntities.PIZZA, pos, state);
        this.isCooked = isCooked;
    }

    public void readFromItemStack(ItemStack stack) {
        int slices = stack.is(CookItItems.PIZZA_SLICE) ? 1 : 4;

        CompoundTag compound = stack.getTag();
        if (compound != null) {
            this.toppings = PizzaTopping.parse(compound);
            if (compound.contains(SLICE_COUNT_KEY, Tag.TAG_INT)) {
                slices = compound.getInt(SLICE_COUNT_KEY);
            }
        } else {
            toppings.clear();
        }

        this.isCooked = stack.getItem() != CookItBlocks.UNCOOKED_PIZZA.asItem();
        this.sliceCount = slices;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.toppings = nbt.getList(PizzaTopping.TOPPINGS_KEY, Tag.TAG_STRING);
        this.isCooked = nbt.getBoolean(COOKED_KEY);
        this.sliceCount = nbt.getInt(SLICE_COUNT_KEY);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.put(PizzaTopping.TOPPINGS_KEY, toppings);
        nbt.putBoolean(COOKED_KEY, isCooked);
        nbt.putInt(SLICE_COUNT_KEY, sliceCount);
        super.saveAdditional(nbt);
    }

    public ListTag getToppings() {
        return toppings.copy();
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
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}