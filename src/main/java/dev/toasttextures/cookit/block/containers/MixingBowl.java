package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.CookingBlockEntity;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MixingBowl extends BlockWithEntity implements BlockEntityProvider {

    public static BooleanProperty CONTAINS_LIQUID = BooleanProperty.of("liquid");

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    public MixingBowl(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CONTAINS_LIQUID, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CONTAINS_LIQUID);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        MixingBowlEntity entity = (MixingBowlEntity) world.getBlockEntity(pos);
        if (entity == null) return ActionResult.PASS;
        ItemStack item = player.getStackInHand(hand);
        if (player.isSneaking()) {
            return entity.dropAsContainer(player, world, this, pos);
        }

        if (entity.getStack(0).isOf(CookItItems.GOOP)) { return ActionResult.CONSUME; }
        if (item.isOf(CookItItems.WHISK)) {
            boolean hasGoop = entity.process(world);
            if (hasGoop) {
                world.setBlockState(pos, state.with(CONTAINS_LIQUID, true));
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            }
            return ActionResult.SUCCESS;
        }
        if ((item.isOf(Items.MILK_BUCKET) || item.isOf(Items.WATER_BUCKET)) && entity.getLiquid() == Items.AIR) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1.25f);
            entity.setStack(entity.size() - 1, item.split(1));
            player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1));
            return ActionResult.SUCCESS;
        }

        if (!item.isEmpty()) {
            for(int i = 0; i < entity.size() - 1; i++) {
                ItemStack stack = entity.getStack(i);
                if (stack.isEmpty()) {
                    entity.setStack(i, item.split(1));
                    return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.FAIL;
    }
    public static void transferTo(ItemStack bowl, CookingBlockEntity to, int stack) {
        NbtCompound nbt = bowl.getSubNbt("BlockEntityTag");
        if (nbt == null || !nbt.contains("Items")) return;

        NbtList items = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
        ItemStack item = ItemStack.fromNbt(items.getCompound(0));
        if (item.isOf(CookItItems.GOOP)) {
            to.setStack(stack, item);
            items.getCompound(0).putInt("Count", item.getCount() - 1);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        MixingBowlEntity entity = (MixingBowlEntity) world.getBlockEntity(pos);

        NbtCompound nbt =itemStack.getSubNbt("BlockEntityTag");
        if (nbt == null) return;
        if (entity != null && !world.isClient) {
            entity.setGoopColor(nbt.getInt("color"));
        }
        if (nbt.contains("Items", NbtElement.LIST_TYPE)) {
            if (ItemStack.fromNbt(nbt.getList("Items", NbtElement.COMPOUND_TYPE).getCompound(0)).isOf(CookItItems.GOOP)) {
                world.scheduleBlockTick(pos, state.getBlock(), 1);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        world.setBlockState(pos, state.with(CONTAINS_LIQUID, true));

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