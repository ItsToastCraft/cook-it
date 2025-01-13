package dev.toasttextures.cookit.block.containers;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import dev.toasttextures.cookit.block.food_blocks.pizza.Pizza;
import org.jetbrains.annotations.Nullable;

public class PizzaPan extends BlockWithEntity implements BlockEntityProvider {

    public PizzaPan(Settings settings) {
        super(settings);
    }
    public static final MapCodec<PizzaPan> CODEC = createCodec(PizzaPan::new);

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private final VoxelShape OUTLINE = VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.0625f, 1.0f);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return OUTLINE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (world.isClient) { return ActionResult.SUCCESS; }
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        PizzaPanEntity blockEntity = (PizzaPanEntity) world.getBlockEntity(pos);
        if (blockEntity == null) { return ActionResult.FAIL; }
        if (player.isSneaking()) {
            ItemStack item = this.asItem().getDefaultStack();
            blockEntity.setStackNbt(item);
            player.getInventory().insertStack(item);
            world.breakBlock(pos,false);
            return ActionResult.SUCCESS;
        }
        ItemStack heldItem = player.getStackInHand(hand);
        boolean hasPizza = !blockEntity.isEmpty();
        if (heldItem.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof Pizza && !hasPizza) {
                blockEntity.setStack(0, heldItem.split(1));
            }
        } else if (heldItem.isEmpty() && hasPizza) {
            player.getInventory().offerOrDrop(blockEntity.getStack(0).split(1));
        } else {
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }



    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {

        return new PizzaPanEntity(pos, state);

    }
}
