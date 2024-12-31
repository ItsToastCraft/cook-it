package com.toast.cookit.block.food_blocks.pizza;

import com.mojang.serialization.MapCodec;
import com.toast.cookit.block.entity.PizzaEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class Pizza extends BlockWithEntity implements BlockEntityProvider {

    protected static final VoxelShape FULL = VoxelShapes.cuboid(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.125f, 0.9375f);

    public Pizza(Settings settings) {
        super(settings);
    }
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return FULL;
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        if (world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity) {
            pizzaEntity.setStackNbt(stack);
        }
        return stack;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaEntity(pos, state, false);
    }
    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getOrCreateSubNbt("BlockEntityTag");
        if (nbt == null || !nbt.contains("toppings")) return;

        NbtList toppings = nbt.getList("toppings", NbtElement.STRING_TYPE);

        if (!toppings.isEmpty()) {
            tooltip.add((Text.literal("Toppings:").formatted(Formatting.GRAY)));
        }

        for (int i = 0; i < toppings.size(); i++) {
            MutableText topping = Objects.requireNonNull(PizzaToppings.fromName(toppings.getString(i))).getTranslationKey();
                tooltip.add(topping.formatted(Formatting.BLUE));
        }
    }
}
