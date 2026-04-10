package dev.toasttextures.cookit.block.food.pizza;

import dev.toasttextures.cookit.block.entity.PizzaEntity;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

public class Pizza extends BaseEntityBlock implements EntityBlock {
    protected static final VoxelShape[] SLICES = new VoxelShape[4];

    public Pizza(Properties settings) {
        super(settings);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SLICES[3];
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(world, pos, state);
        if (world.getBlockEntity(pos) instanceof PizzaEntity blockEntity) {
            blockEntity.saveToItem(stack);
        }
        return stack;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaEntity(pos, state, false);
    }

    static {
        SLICES[0] = box(1.0, 0.0, 1.0, 8.0, 2.0, 8.0);
        SLICES[1] = box(1.0, 0.0, 1.0, 15.0, 2.0, 8.0);
        SLICES[2] = Shapes.or(SLICES[1], box(8.0, 0.0, 8.0, 15.0, 2.0, 15.0));
        SLICES[3] = box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0);
    }
}