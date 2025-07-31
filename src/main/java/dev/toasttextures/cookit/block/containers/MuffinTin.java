package dev.toasttextures.cookit.block.containers;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.CookingBlockEntity;
import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.enums.FoodTypes;
import dev.toasttextures.cookit.registries.CookItItems;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

public class MuffinTin extends CookingContainer {
    protected static final MapCodec<MuffinTin> CODEC = createCodec(MuffinTin::new);

    public MuffinTin(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.1875, 0f, 0.0625f, 0.8125f, 0.125f, 0.9375f);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(blockPos, state, state, Block.NOTIFY_LISTENERS);
        MuffinTinEntity blockEntity = (MuffinTinEntity) world.getBlockEntity(blockPos);
        if (world.isClient() || blockEntity == null) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getStackInHand(hand);
        if (player.isSneaking()) {
            return BlockEntityUtils.dropOnUse(this, blockEntity, player, world, blockPos);
        }
        if (!heldItem.isEmpty()) {
            this.addStack(heldItem, blockEntity, null);
        } else {
            retrieveStack(player, blockEntity, List.of(CookItItems.GOOP));
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void addStack(ItemStack stack, CookingBlockEntity entity, @Nullable FoodTypes foodType) {
        if (stack.isEmpty()) {
            return;
        }
        for (int i = 0; i < entity.getItems().size(); i++) {
            if (entity.getStack(i).isEmpty()) {
                if (stack.isOf(CookItBlocks.MIXING_BOWL.asItem()) && stack.getSubNbt("BlockEntityTag") != null) {
                    MixingBowl.transferTo(stack, entity, i);
                } else if (CookItItems.MUFFINS.contains(stack.getItem())) {
                    entity.setStack(i, stack.split(1));
                }
                break;
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext context) {
        ItemStack[] items = BlockEntityUtils.formatItems(stack, CookItItems.GOOP);
        for (ItemStack item : items) {
            NbtCompound tag = item.getNbt();
            if (tag != null && tag.contains("output")) {
                item = ItemStack.fromNbt(tag.getCompound("output"));
            }
        }
        BlockEntityUtils.appendTooltip(items, tooltip);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MuffinTinEntity(pos, state);
    }
}