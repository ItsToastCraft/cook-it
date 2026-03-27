package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.Container;
import dev.toasttextures.cookit.block.entity.PlateEntity;
import dev.toasttextures.cookit.item.ItemStorage;
import net.minecraft.block.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import dev.toasttextures.cookit.registries.CookItItems;

import java.util.List;

public class Plate extends BaseEntityBlock {
    public static final IntegerProperty COUNT = IntegerProperty.create("count", 1, 4);

    private final DyeColor color;
    public Plate(Properties settings, DyeColor color) {
        super(settings);
        this.color = color;
        registerDefaultState(defaultBlockState().setValue(COUNT, 1));
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COUNT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return box(4.0, 0.0, 4.0, 12.0, state.getValue(COUNT), 12.0);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        PlateEntity blockEntity = (PlateEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return InteractionResult.SUCCESS;

        int plateAmount = state.getValue(COUNT);
        ItemStack heldItem = player.getItemInHand(hand);

        // If there is no item in the player's hand and there is more than one plate, give one plate
        // Otherwise give back whatever is on the plate (because there's only one sooo)

        ItemStack first = blockEntity.retrieve();
        if (heldItem.isEmpty()) {
            if (player.isShiftKeyDown()) {
                // Why did it take me 80 billion years to remember this exists
                blockEntity.dropAsContainer(player, world, this, pos);
                decreasePlates(state, world, pos);
            } else if (!first.isEmpty()) {
                player.getInventory().placeItemBackInInventory(first);
            } else {
                player.getInventory().placeItemBackInInventory(this.getCloneItemStack(world, pos, state));
                decreasePlates(state, world, pos);
            }
            return InteractionResult.SUCCESS;
        }

        if (heldItem.is(CookItItems.FRYER_BASKET)) return InteractionResult.PASS;

        // Add another plate if the player is holding one of the same type and there aren't already 4 on there.
        if (heldItem.is(this.asItem()) && plateAmount < 4 && first.isEmpty()) {
            ItemStack stored = ItemStorage.getStoredItem(heldItem);
            if (!stored.isEmpty()) {
                blockEntity.setItem(0, heldItem.split(1));
            }
            world.playSound(null, pos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 1, 1.75f);
            world.setBlockAndUpdate(pos, state.setValue(COUNT, plateAmount + 1));
        // Add whatever is in the player's hand, as long as it's food (sorry)
        } else if (first.isEmpty() && heldItem.isEdible()) {
            blockEntity.setItem(0, heldItem.split(1));
            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1, 1.0f);
            world.sendBlockUpdated(pos, state, state, UPDATE_CLIENTS);
        }

        return InteractionResult.SUCCESS;
    }
    private void decreasePlates(BlockState state, Level world, BlockPos pos) {
        int plateAmount = state.getValue(COUNT) - 1;

        if (plateAmount == 0) {
            world.destroyBlock(pos, false);
            return;
        }
        world.setBlockAndUpdate(pos, state.setValue(COUNT, plateAmount));
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.getBlockEntity(pos) instanceof PlateEntity plateEntity && !plateEntity.getFirst().isEmpty()) {
            popResource(world, pos, plateEntity.getFirst().split(1));
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        Container.appendTooltip(stack, tooltip, item -> item != Items.AIR);
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlateEntity(pos, state);
    }
}