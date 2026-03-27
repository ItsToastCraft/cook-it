package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.Container;
import dev.toasttextures.cookit.block.entity.PlateEntity;
import dev.toasttextures.cookit.item.ItemStorage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import dev.toasttextures.cookit.registries.CookItItems;

import java.util.List;

public class Plate extends BlockWithEntity {
    public static final IntProperty COUNT = IntProperty.of("count", 1, 4);

    private final DyeColor color;
    public Plate(Settings settings, DyeColor color) {
        super(settings);
        this.color = color;
        setDefaultState(getDefaultState().with(COUNT, 1));
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COUNT);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return createCuboidShape(4.0, 0.0, 4.0, 12.0, state.get(COUNT), 12.0);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        PlateEntity blockEntity = (PlateEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return ActionResult.SUCCESS;

        int plateAmount = state.get(COUNT);
        ItemStack heldItem = player.getStackInHand(hand);

        // If there is no item in the player's hand and there is more than one plate, give one plate
        // Otherwise give back whatever is on the plate (because there's only one sooo)

        ItemStack first = blockEntity.retrieve();
        if (heldItem.isEmpty()) {
            if (player.isSneaking()) {
                // Why did it take me 80 billion years to remember this exists
                blockEntity.dropAsContainer(player, world, this, pos);
                decreasePlates(state, world, pos);
            } else if (!first.isEmpty()) {
                player.getInventory().offerOrDrop(first);
            } else {
                player.getInventory().offerOrDrop(this.getPickStack(world, pos, state));
                decreasePlates(state, world, pos);
            }
            return ActionResult.SUCCESS;
        }

        if (heldItem.isOf(CookItItems.FRYER_BASKET)) return ActionResult.PASS;

        // Add another plate if the player is holding one of the same type and there aren't already 4 on there.
        if (heldItem.isOf(this.asItem()) && plateAmount < 4 && first.isEmpty()) {
            ItemStack stored = ItemStorage.getStoredItem(heldItem);
            if (!stored.isEmpty()) {
                blockEntity.setStack(0, heldItem.split(1));
            }
            world.playSound(null, pos, SoundEvents.BLOCK_COPPER_PLACE, SoundCategory.BLOCKS, 1, 1.75f);
            world.setBlockState(pos, state.with(COUNT, plateAmount + 1));
        // Add whatever is in the player's hand, as long as it's food (sorry)
        } else if (first.isEmpty() && heldItem.isFood()) {
            blockEntity.setStack(0, heldItem.split(1));
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1, 1.0f);
            world.updateListeners(pos, state, state, NOTIFY_LISTENERS);
        }

        return ActionResult.SUCCESS;
    }
    private void decreasePlates(BlockState state, World world, BlockPos pos) {
        int plateAmount = state.get(COUNT) - 1;

        if (plateAmount == 0) {
            world.breakBlock(pos, false);
            return;
        }
        world.setBlockState(pos, state.with(COUNT, plateAmount));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof PlateEntity plateEntity && !plateEntity.getFirst().isEmpty()) {
            dropStack(world, pos, plateEntity.getFirst().split(1));
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        Container.appendTooltip(stack, tooltip, item -> item != Items.AIR);
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlateEntity(pos, state);
    }
}