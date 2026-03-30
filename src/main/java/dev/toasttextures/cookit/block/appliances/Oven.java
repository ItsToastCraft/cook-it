package dev.toasttextures.cookit.block.appliances;

import dev.toasttextures.cookit.block.entity.OvenEntity;
import dev.toasttextures.cookit.registries.CookItTags;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class Oven extends BaseEntityBlock implements EntityBlock {
    public Oven(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
            .setValue(HORIZONTAL_FACING, Direction.NORTH)
            .setValue(LIT, false)
            .setValue(OPEN, false)
        );
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, LIT, OPEN);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof OvenEntity blockEntity)) return InteractionResult.PASS;
        ItemStack heldItem = player.getItemInHand(hand);

        if (!state.getValue(OPEN) && heldItem.isEmpty()) {
            openOven(world, pos, state, true);
            return InteractionResult.SUCCESS;
        }

        if (heldItem.isEmpty()) {
            if (state.getValue(LIT)) {
                ItemStack retrieved = blockEntity.retrieve();
                if (!retrieved.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(retrieved);
                }
            } else {
                openOven(world, pos, state, false);
            }
        } else if (Block.byItem(heldItem.getItem()).defaultBlockState().is(CookItTags.CONTAINERS)) {
            return blockEntity.fillFirst(player, heldItem) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    public void openOven(Level world, BlockPos pos, BlockState state, boolean open) {
        SoundEvent sound = open ? SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE;
        world.playSound(null, pos, sound, SoundSource.BLOCKS);
        world.setBlockAndUpdate(pos, state.setValue(OPEN, open));
    }

    @Nullable
    @Override
    public OvenEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OvenEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CookItBlockEntities.OVEN, OvenEntity::tick);
    }
}