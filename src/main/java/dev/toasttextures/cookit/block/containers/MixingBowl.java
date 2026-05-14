package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.entity.Container;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
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

    public static BooleanProperty CONTAINS_GOOP = BooleanProperty.create("goop");
    public static BooleanProperty LIQUID_LAYER = BooleanProperty.create("liquid");

    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public MixingBowl(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CONTAINS_GOOP, false).setValue(LIQUID_LAYER, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTAINS_GOOP, LIQUID_LAYER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        Liquid liquid = Liquid.fromItem(heldItem.getItem());
        CookIt.LOGGER.info("println");
        if (!(world.getBlockEntity(pos) instanceof MixingBowlEntity blockEntity)) return InteractionResult.PASS;

        if (world.isClientSide) {

            if (liquid != Liquid.NONE && blockEntity.getLiquid() == Liquid.NONE) {
                blockEntity.updateLiquid(liquid);
            }
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) return blockEntity.dropAsContainer(player, world, this, pos);

        if (blockEntity.getFirst().is(CookItItems.GOOP)) { return InteractionResult.CONSUME; }

        if (heldItem.is(CookItItems.WHISK)) {
            if (blockEntity.process(world)) {
                world.setBlockAndUpdate(pos, state.setValue(CONTAINS_GOOP, true));
            }
        } else if (liquid != Liquid.NONE && blockEntity.getLiquid() == Liquid.NONE) {
            blockEntity.updateLiquid(liquid);
            player.setItemInHand(hand, heldItem.getRecipeRemainder());
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.5f, 1.25f);
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

    public enum Liquid implements StringRepresentable {
        NONE("none", 0x000000),
        WATER("water", 0x3B5382),
        MILK("milk", 0xFFFFFF);

        public final String name;
        private final int color;
        Liquid(String name, int color) {
            this.name = name;
            this.color = color;
        }

        public static Liquid fromItem(Item item) {
            if (item == Items.MILK_BUCKET) return MILK;
            else if (item == Items.WATER_BUCKET) return WATER;
            else return NONE;
        }

        public int getColor() {
            return color;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name.toLowerCase();
        }
    }
}