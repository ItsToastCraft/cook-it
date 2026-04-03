package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.PizzaPanEntity;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import dev.toasttextures.cookit.block.food.pizza.Pizza;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PizzaPan extends BaseEntityBlock implements EntityBlock {
    public PizzaPan(Properties settings) {
        super(settings);
    }

    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    private static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) { return InteractionResult.SUCCESS; }

        if (!(world.getBlockEntity(pos) instanceof PizzaPanEntity blockEntity)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) return blockEntity.dropAsContainer(player, world, this, pos);

        ItemStack heldItem = player.getItemInHand(hand);
        boolean hasPizza = !blockEntity.isEmpty();

        if (Block.byItem(heldItem.getItem()) instanceof Pizza && !hasPizza) {
            blockEntity.setItem(0, heldItem.split(1));
        } else if (heldItem.isEmpty() && hasPizza) {
            player.getInventory().placeItemBackInInventory(blockEntity.getItem(0).split(1));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaPanEntity(pos, state);
    }
}