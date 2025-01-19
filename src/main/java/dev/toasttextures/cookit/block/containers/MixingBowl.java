package dev.toasttextures.cookit.block.containers;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MixingBowl extends BlockWithEntity implements BlockEntityProvider {

    public static BooleanProperty HAS_GOOP = BooleanProperty.of("has_goop");

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    public MixingBowl(Settings settings) {
        super(settings);
    }

    public static final MapCodec<MixingBowl> CODEC = createCodec(MixingBowl::new);

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MixingBowl.HAS_GOOP);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_LISTENERS);
        ItemStack item = player.getStackInHand(hand);
        MixingBowlEntity entity = (MixingBowlEntity) world.getBlockEntity(blockPos);
        if (world.isClient() || entity == null) {
            return ActionResult.FAIL;
        }
        if (item.isOf(CookItItems.WHISK)) {
            entity.processRecipe();
            return ActionResult.SUCCESS;
        }
        if ((item.isOf(Items.MILK_BUCKET) || item.isOf(Items.WATER_BUCKET)) && entity.getLiquid() == Items.AIR) {
            entity.setStack(4, item.split(1));
            player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1));
            return ActionResult.SUCCESS;
        }

        if (!item.isEmpty()) {
            for(int i = 0; i < entity.size() - 1; i++) {
                ItemStack stack = entity.getStack(i);
                if (stack.isEmpty()) {

                    if (item.isOf(Items.EGG)) {
                        entity.setStack(i, item.split(1));
                        return ActionResult.SUCCESS;
                    }
                    entity.setStack(i, item.split(1));
                    return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.125f, 0f, 0.125f, 0.875f, 0.5f, 0.875f);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MixingBowlEntity(pos, state);
    }
}