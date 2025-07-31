package dev.toasttextures.cookit.block.containers;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.CookingBlockEntity;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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

import java.util.List;

public class MixingBowl extends CookingContainer {
    private static final MapCodec<MixingBowl> CODEC = createCodec(MixingBowl::new);
    public static final List<Item> MIXING_BOWL_LIQUIDS = List.of(Items.WATER_BUCKET, Items.MILK_BUCKET);
    public static BooleanProperty HAS_GOOP = BooleanProperty.of("has_goop");

    public MixingBowl(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(HAS_GOOP, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_GOOP);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.125f, 0f, 0.125f, 0.875f, 0.5f, 0.875f);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        MixingBowlEntity entity = (MixingBowlEntity) world.getBlockEntity(pos);

        if (world.isClient() || entity == null) {
            return ActionResult.PASS;
        }

        ItemStack item = player.getStackInHand(hand);
        if (player.isSneaking()) {
            return BlockEntityUtils.dropOnUse(this, entity, player, world, pos);
        }
        if (entity.getStack(0).isOf(CookItItems.GOOP)) {
            return ActionResult.SUCCESS;
        }
        if (item.isOf(CookItItems.WHISK)) {
            if (entity.processRecipe()) {
                world.setBlockState(pos, state.with(HAS_GOOP, true));
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            }
            return ActionResult.SUCCESS;
        }
        if (MIXING_BOWL_LIQUIDS.contains(item.getItem()) && entity.getLiquid().equals(Items.AIR)) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1.25f);
            entity.setStack(entity.size() - 1, item.split(1));
            player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1));
            return ActionResult.SUCCESS;
        }

        if (!item.isEmpty()) {
            addStack(item, entity, null);
        }
        return ActionResult.FAIL;
    }

    // Meant for transferring outputs to other containers, so it only uses the first slot
    public static void transferTo(ItemStack bowl, CookingBlockEntity to, int stack) {
        NbtCompound nbt = bowl.getSubNbt("BlockEntityTag");
        if (nbt == null || !nbt.contains("Items", NbtElement.LIST_TYPE)) return;
        NbtCompound compound = nbt.getList("Items", NbtElement.COMPOUND_TYPE).getCompound(0);
        ItemStack item = ItemStack.fromNbt(compound);

        to.setStack(stack, item);
        if (item.isOf(CookItItems.GOOP)) {
            compound.putInt("Count", item.getCount() - 1);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        MixingBowlEntity entity = (MixingBowlEntity) world.getBlockEntity(pos);

        NbtCompound nbt = itemStack.getSubNbt("BlockEntityTag");
        if (world.isClient() || nbt == null || entity == null) {
            return;
        }

        entity.setGoopColor(nbt.getInt("color"));
        NbtList list = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

        if (list != null && ItemStack.fromNbt(list.getCompound(0)).isOf(CookItItems.GOOP)) {
            world.scheduleBlockTick(pos, state.getBlock(), 1);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        world.setBlockState(pos, state.with(HAS_GOOP, true));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MixingBowlEntity(pos, state);
    }
}