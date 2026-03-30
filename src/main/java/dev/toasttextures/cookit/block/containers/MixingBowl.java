package dev.toasttextures.cookit.block.containers;

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
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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

        MixingBowlEntity entity = (MixingBowlEntity) world.getBlockEntity(pos);
        if (entity == null) return InteractionResult.PASS;
        ItemStack item = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            return entity.dropAsContainer(player, world, this, pos);
        }

        if (entity.getItem(0).is(CookItItems.GOOP)) { return InteractionResult.CONSUME; }
        if (item.is(CookItItems.WHISK)) {
            boolean hasGoop = entity.process(world);
            if (hasGoop) {
                world.setBlockAndUpdate(pos, state.setValue(CONTAINS_LIQUID, true));
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
            }
            return InteractionResult.SUCCESS;
        }
        if ((item.is(Items.MILK_BUCKET) || item.is(Items.WATER_BUCKET)) && entity.getLiquid() == Items.AIR) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.5f, 1.25f);
            entity.setItem(entity.getContainerSize() - 1, item.split(1));
            player.setItemInHand(hand, new ItemStack(Items.BUCKET, 1));
            return InteractionResult.SUCCESS;
        }

        if (!item.isEmpty()) {
            for(int i = 0; i < entity.getContainerSize() - 1; i++) {
                ItemStack stack = entity.getItem(i);
                if (stack.isEmpty()) {
                    entity.setItem(i, item.split(1));
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        MixingBowlEntity entity = (MixingBowlEntity) world.getBlockEntity(pos);

        CompoundTag nbt = itemStack.getTagElement("BlockEntityTag");
        if (nbt == null) return;
        if (entity != null && !world.isClientSide) {
            entity.setGoopColor(nbt.getInt("color"));
        }
        if (nbt.contains("Items", Tag.TAG_LIST)) {
            if (ItemStack.of(nbt.getList("Items", Tag.TAG_COMPOUND).getCompound(0)).is(CookItItems.GOOP)) {
                world.scheduleTick(pos, state.getBlock(), 1);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        world.setBlockAndUpdate(pos, state.setValue(CONTAINS_LIQUID, true));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MixingBowlEntity(pos, state);
    }
}