package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.block.food.pizza.PizzaTopping;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
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

public class PizzaEntity extends BlockEntity {
    private static final String SLICE_COUNT_KEY = "Slices";
    private static final String COOKED_KEY = "Cooked";
    private NbtList toppings = new NbtList();
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
        int slices = stack.isOf(CookItItems.PIZZA_SLICE) ? 1 : 4;
        this.toppings.clear();

        NbtCompound compound = stack.getNbt();
        if (compound != null) {
            NbtList toppings = PizzaTopping.parse(compound);
            if (toppings != null) {
                this.toppings = toppings;
            }
            if (compound.contains(SLICE_COUNT_KEY, NbtElement.INT_TYPE)) {
                slices = compound.getInt(SLICE_COUNT_KEY);
            }
        }

        this.isCooked = stack.getItem() != CookItBlocks.UNCOOKED_PIZZA.asItem();
        this.sliceCount = slices;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.toppings = nbt.getList(PizzaTopping.TOPPINGS_KEY, NbtElement.STRING_TYPE);
        this.isCooked = nbt.getBoolean(COOKED_KEY);
        this.sliceCount = nbt.getInt(SLICE_COUNT_KEY);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put(PizzaTopping.TOPPINGS_KEY, toppings);
        nbt.putBoolean(COOKED_KEY, isCooked);
        nbt.putInt(SLICE_COUNT_KEY, sliceCount);
        super.writeNbt(nbt);
    }

    public NbtList getToppings() {
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
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}