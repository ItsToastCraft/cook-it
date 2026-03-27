package dev.toasttextures.cookit.block.food.pizza;

import dev.toasttextures.cookit.block.entity.PizzaEntity;
import net.minecraft.block.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

public class Pizza extends BaseEntityBlock implements EntityBlock {
    public Pizza(Properties settings) {
        super(settings);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return Slices.FULL.shape;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(world, pos, state);
        if (world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity) {
            pizzaEntity.saveToItem(stack);
        }
        return stack;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaEntity(pos, state, false);
    }

    protected enum Slices {
        ONE_SLICE(box(1.0, 0.0, 1.0, 8.0, 2.0, 8.0)),
        TWO_SLICES(box(1.0, 0.0, 1.0, 15.0, 2.0, 8.0)),
        THREE_SLICES(Shapes.or(TWO_SLICES.shape, box(8.0, 0.0, 8.0, 15.0, 2.0, 15.0))),
        FULL(box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0));

        public final VoxelShape shape;

        Slices(VoxelShape shape) {
            this.shape = shape;
        }
    }
}