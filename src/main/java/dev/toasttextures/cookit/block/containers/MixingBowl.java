package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.Container;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MixingBowl extends BaseEntityBlock implements EntityBlock {
    private static final VoxelShape SHAPE = box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    public static BooleanProperty CONTAINS_LIQUID = BooleanProperty.create("liquid");

    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public MixingBowl(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CONTAINS_LIQUID, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTAINS_LIQUID);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof MixingBowlEntity blockEntity)) return InteractionResult.PASS;

        ItemStack heldItem = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) return blockEntity.dropAsContainer(player, world, this, pos);

        if (blockEntity.getFirst().is(CookItItems.GOOP)) { return InteractionResult.CONSUME; }

        if (heldItem.is(CookItItems.WHISK)) {
            if (blockEntity.process(world)) {
                world.setBlockAndUpdate(pos, state.setValue(CONTAINS_LIQUID, true));
            }
        } else if ((heldItem.is(Items.MILK_BUCKET) || heldItem.is(Items.WATER_BUCKET)) && blockEntity.getLiquid() == Items.AIR) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.5f, 1.25f);
            blockEntity.setItem(blockEntity.getContainerSize() - 1, heldItem.split(1));
            player.setItemInHand(hand, new ItemStack(Items.BUCKET, 1));
        } else if (!heldItem.isEmpty()) {
            blockEntity.fillFirst(player, heldItem);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide) return;
        if (!(world.getBlockEntity(pos) instanceof MixingBowlEntity blockEntity)) return;

        Container.onPlaced(world, pos, itemStack);

        CompoundTag nbt = itemStack.getTag();
        if (nbt != null && nbt.contains(MixingBowlEntity.COLOR_KEY)) {
           blockEntity.setGoopColor(nbt.getInt(MixingBowlEntity.COLOR_KEY));
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MixingBowlEntity(pos, state);
    }
}