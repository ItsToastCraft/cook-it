package dev.toasttextures.cookit.block.containers;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.PizzaPanEntity;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
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

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.0625f, 1.0f);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) { return ItemActionResult.SUCCESS; }
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        PizzaPanEntity blockEntity = (PizzaPanEntity) world.getBlockEntity(pos);
        if (blockEntity == null) { return ItemActionResult.FAIL; }
        if (player.isSneaking()) {
            return BlockEntityUtils.dropOnUse(this, blockEntity, player, world, pos);
        }
        ItemStack heldItem = player.getStackInHand(hand);
        boolean hasPizza = !blockEntity.isEmpty();
        if (heldItem.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof Pizza && !hasPizza) {
                blockEntity.setStack(0, heldItem.splitUnlessCreative(1, player));
            }
        } else if (heldItem.isEmpty() && hasPizza) {
            player.getInventory().offerOrDrop(blockEntity.getStack(0).split(1));
        } else {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        PizzaPanEntity entity = (PizzaPanEntity) world.getBlockEntity(pos);
        BlockEntityUtils.convertSingleCookingComponent(world, entity, itemStack);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaPanEntity(pos, state);
    }
}
