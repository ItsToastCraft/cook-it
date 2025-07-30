package dev.toasttextures.cookit.block.food_blocks.pizza;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.PizzaEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Pizza extends BlockWithEntity implements BlockEntityProvider {
    protected static final VoxelShape FULL = VoxelShapes.cuboid(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.125f, 0.9375f);

    public Pizza(Settings settings) {
        super(settings);
    }
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
        Pizza.addTooltip(stack.getSubNbt("BlockEntityTag"), tooltip);
    }

    public static void addTooltip(NbtCompound nbt, List<Text> tooltip) {
        if (nbt == null) {
            return;
        }
        List<PizzaTopping> toppings = PizzaTopping.fromNbt(nbt);

        tooltip.add((Text.literal("Toppings:").formatted(Formatting.GRAY)));

        for (PizzaTopping topping : toppings) {
            tooltip.add(topping.getTranslationKey().formatted(Formatting.BLUE));
        }
    }
}
