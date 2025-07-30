package dev.toasttextures.cookit.block.containers;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.PlateEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import dev.toasttextures.cookit.registries.CookItItems;

public class Plate extends CookingContainer {

    public static final IntProperty PLATES_AMOUNT = IntProperty.of("plate_amount", 1, 4);

    private final DyeColor color;
    public Plate(Settings settings, DyeColor color) {
        super(settings);
        this.color = color;
        setDefaultState(getDefaultState().with(PLATES_AMOUNT, 1));
    }

    public String getColor() {
        return color.getName();
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PLATES_AMOUNT);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        int plateAmount = state.get(PLATES_AMOUNT);
        if (isLargePlate((Plate) state.getBlock())) {
            return VoxelShapes.cuboid(0.125f, 0f, 0.125f, 0.875f, 0.0625f * plateAmount, 0.875f);
        } else {
            return VoxelShapes.cuboid(0.25f, 0f, 0.25f, 0.75f, 0.0625f * plateAmount, 0.75f);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        PlateEntity blockEntity = (PlateEntity) world.getBlockEntity(pos);
        int plateAmount = state.get(PLATES_AMOUNT);
        ItemStack heldItem = player.getStackInHand(hand);

        if (world.isClient || blockEntity == null) {
            return ActionResult.SUCCESS;
        }
        // Let custom processing for fryer basket occur
        if (heldItem.getItem().equals(CookItItems.FRYER_BASKET)) {
            return ActionResult.PASS;
        }
        if (heldItem.isEmpty()) {

            if (!blockEntity.getStack(0).isEmpty()) {
                player.getInventory().offerOrDrop(blockEntity.getStack(0));
                return ActionResult.SUCCESS;
            }
            if (player.isSneaking()) {
                ItemStack item = new ItemStack(this.asItem(), 1);
                if (!blockEntity.getStack(0).isEmpty()) {
                    blockEntity.setStackNbt(item); // Omg the name of this is so confusing
                    blockEntity.removeStack(0);
                }
                removePlate(state, world, pos, player, item);
            }
                return ActionResult.SUCCESS;
        } else {
            // Add another plate if the player is holding one of the same type and there aren't already 4 on there.
            if (heldItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock().equals(this.asBlock()) && plateAmount < 4) {
                addPlate(heldItem, blockEntity, world, pos, state);
            } else if (blockEntity.getStack(0).isEmpty()) {
                blockEntity.setStack(0, heldItem.split(1));
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1, 1.0f);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    private void addPlate(ItemStack item, PlateEntity blockEntity, World world, BlockPos pos, BlockState state) {
        NbtCompound tag = item.getSubNbt("BlockEntityTag");
        if (tag != null && tag.contains("Items")) {
            ItemStack stackInPlate = ItemStack.fromNbt(tag.getList("Items", NbtElement.COMPOUND_TYPE).getCompound(0));
            if (blockEntity.getStack(0).isEmpty()) {
                blockEntity.setStack(0, stackInPlate);
            } else {
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stackInPlate));
            }
        }
        item.decrement(1);
        world.playSound(null, pos, SoundEvents.BLOCK_COPPER_PLACE, SoundCategory.BLOCKS, 1, 1.75f);
        world.setBlockState(pos, state.with(PLATES_AMOUNT, state.get(PLATES_AMOUNT) + 1));

    }
    private void removePlate(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack item) {
        int plateAmount = state.get(PLATES_AMOUNT);
        if (!item.isEmpty()) {
            player.getInventory().offerOrDrop(item);
        }

        if (plateAmount == 1) {
            world.breakBlock(pos, false);
            return;
        }
        world.setBlockState(pos, state.with(PLATES_AMOUNT, plateAmount - 1));
    }
    public static boolean isLargePlate(Plate plate) {
        return Registries.BLOCK.getId(plate).getPath().contains("large_plate");
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof PlateEntity plateEntity) {
            if (!plateEntity.getStack(0).isEmpty()) {
                dropStack(world, pos, plateEntity.getStack(0).split(1));
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new PlateEntity(pos, state); }
}