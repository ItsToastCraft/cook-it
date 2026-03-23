package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.PizzaPanEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import dev.toasttextures.cookit.block.food_blocks.pizza.Pizza;
import org.jetbrains.annotations.Nullable;

public class PizzaPan extends BlockWithEntity implements BlockEntityProvider {
    public PizzaPan(Settings settings) {
        super(settings);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private static final VoxelShape SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) { return ActionResult.SUCCESS; }
        PizzaPanEntity blockEntity = (PizzaPanEntity) world.getBlockEntity(pos);
        if (blockEntity == null) { return ActionResult.PASS; }

        if (player.isSneaking()) return blockEntity.dropAsContainer(player, world, this, pos);

        ItemStack heldItem = player.getStackInHand(hand);

        boolean hasPizza = !blockEntity.isEmpty();
        if (heldItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof Pizza && !hasPizza) {
            blockEntity.setStack(0, heldItem.split(1));
        } else if (heldItem.isEmpty() && hasPizza) {
            player.getInventory().offerOrDrop(blockEntity.getStack(0).split(1));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaPanEntity(pos, state);
    }
}
