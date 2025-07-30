package dev.toasttextures.cookit.block.containers;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.BakingSheetEntity;
import dev.toasttextures.cookit.enums.FoodTypes;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakingSheet extends CookingContainer {
    private static final MapCodec<BakingSheet> CODEC = createCodec(BakingSheet::new);
    public BakingSheet(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(blockPos, state, state, Block.NOTIFY_LISTENERS);
        BakingSheetEntity blockEntity = (BakingSheetEntity) world.getBlockEntity(blockPos);
        if (world.isClient || blockEntity == null) {
            return ActionResult.SUCCESS;
        } else {
            if (player.isSneaking()) {
                return BlockEntityUtils.dropOnUse(this, blockEntity, player, world, blockPos);
            }
            ItemStack item = player.getStackInHand(hand);
            if (!item.isEmpty()) {
                addStack(item, blockEntity, FoodTypes.BAKING);
            } else {
                retrieveStack(player, blockEntity);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.1875, 0f, 0.0625f, 0.8125f, 0.125f, 0.9375f);
    }
    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext context) {
        BlockEntityUtils.appendTooltip(stack, tooltip);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BakingSheetEntity(pos, state);
    }
}