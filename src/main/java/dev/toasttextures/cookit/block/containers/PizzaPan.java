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
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import dev.toasttextures.cookit.block.food_blocks.pizza.Pizza;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        PizzaPanEntity entity = (PizzaPanEntity) world.getBlockEntity(pos);
        if (world.isClient || entity == null) {
            return ItemActionResult.SUCCESS;
        }

        if (player.isSneaking()) {
            return BlockEntityUtils.dropOnUse(this, entity, player, world, pos);
        }
        boolean hasPizza = !entity.isEmpty();
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof Pizza && !hasPizza) {
                entity.setStack(0, stack.splitUnlessCreative(1, player));
            }
        } else if (stack.isEmpty() && hasPizza) {
            player.getInventory().offerOrDrop(entity.getStack(0));
        } else {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        PizzaPanEntity entity = (PizzaPanEntity) world.getBlockEntity(pos);
        BlockEntityUtils.convertCookingComponent(world, entity, itemStack);
    }
    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return BlockEntityUtils.dropWithComponent(this, builder);
    }
    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return BlockEntityUtils.getPickStack(this, (PizzaPanEntity) world.getBlockEntity(pos));
    }
    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaPanEntity(pos, state);
    }
}
