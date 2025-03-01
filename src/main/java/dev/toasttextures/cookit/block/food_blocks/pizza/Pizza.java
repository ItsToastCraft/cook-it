package dev.toasttextures.cookit.block.food_blocks.pizza;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.PizzaEntity;
import dev.toasttextures.cookit.registries.CookItComponents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
           stack.applyComponentsFrom(pizzaEntity.createComponentMap());
        }
        return stack;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaEntity(pos, state, false);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {

        ArrayList<String> toppings = stack.getOrDefault(CookItComponents.TOPPING_COMPONENT, new ArrayList<>());
        if (!toppings.isEmpty()) return;
        tooltip.add((Text.literal("Toppings:").formatted(Formatting.GRAY)));

        for (String s : toppings) {
            MutableText topping = Objects.requireNonNull(PizzaToppings.fromName(s)).getTranslationKey();
            tooltip.add(topping.formatted(Formatting.BLUE));
        }
    }
}
