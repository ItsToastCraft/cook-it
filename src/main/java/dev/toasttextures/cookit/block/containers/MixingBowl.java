package dev.toasttextures.cookit.block.containers;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.CookingBlockEntity;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.registry.CookItComponents;
import dev.toasttextures.cookit.registry.CookItItems;
import dev.toasttextures.cookit.registry.component.CookingComponent;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MixingBowl extends BlockWithEntity implements BlockEntityProvider {
    public static BooleanProperty HAS_GOOP = BooleanProperty.of("has_goop");

    public MixingBowl(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(HAS_GOOP, false));
    }

    public static final MapCodec<MixingBowl> CODEC = createCodec(MixingBowl::new);

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MixingBowl.HAS_GOOP);
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        MixingBowlEntity entity = (MixingBowlEntity) world.getBlockEntity(pos);
        if (world.isClient || entity == null) {
            return ItemActionResult.SUCCESS;
        }
        if (player.isSneaking()) {
            return BlockEntityUtils.dropOnUse(this, entity, player, world, pos);
        }
        if (entity.getStack(0).isOf(CookItItems.GOOP)) {
            return ItemActionResult.FAIL;
        }
        if (stack.isOf(CookItItems.WHISK)) {
            boolean hasGoop = entity.processRecipe();
            if (hasGoop) {
                world.setBlockState(pos, state.with(HAS_GOOP, true));
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            }
            return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        } else if ((stack.isOf(Items.MILK_BUCKET) || stack.isOf(Items.WATER_BUCKET)) && entity.getLiquid().equals(Items.AIR)) {
            world.playSound(null, pos, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1.25f);
            entity.setStack(entity.size() - 1, stack.split(1));
            player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1));
        }

        if (!stack.isEmpty()) {
            for(int i = 0; i < entity.size() - 1; i++) {
                ItemStack entityStack = entity.getStack(i);
                if (entityStack.isEmpty()) {
                    entity.setStack(i, stack.split(1));
                    return ItemActionResult.FAIL;
                }
            }
        }
        return ItemActionResult.SUCCESS;
    }

    // Gives the contents of the first slot (output) to whatever wants it ig
    public static void transferTo(ItemStack bowl, CookingBlockEntity to, World world, int stack) {
        if (!world.isClient) {
            List<ItemStack> itemStacks = bowl.getOrDefault(CookItComponents.COOKING_COMPONENT, CookingComponent.DEFAULT).stacks();

            if (itemStacks.size() != 1) return; // Ensure there's only one output

            ItemStack item = itemStacks.getFirst().copy();

            if (item.isOf(CookItItems.GOOP)) {
                to.setStack(stack, item.copyWithCount(1));
                item.decrement(1);

                if (item.isEmpty()) {
                    bowl.remove(CookItComponents.COOKING_COMPONENT);
                    bowl.remove(CookItComponents.COLOR_COMPONENT);
                } else {
                    bowl.set(CookItComponents.COOKING_COMPONENT, new CookingComponent(List.of(item)));
                }
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        MixingBowlEntity entity = (MixingBowlEntity) world.getBlockEntity(pos);

        if (entity != null && !world.isClient) {
            List<ItemStack> items = itemStack.getOrDefault(CookItComponents.COOKING_COMPONENT, CookingComponent.DEFAULT).stacks();
            if (!items.isEmpty()){
                ArrayList<ItemStack> otherItems = new ArrayList<>(items);
                if (items.size() == 7) {
                    entity.setStack(entity.size() - 1, items.get(6));
                    otherItems.removeLast();
                }
                entity.setItems(otherItems);

            }
            entity.setGoopColor(itemStack.getOrDefault(CookItComponents.COLOR_COMPONENT,0));
            ItemStack newStack = itemStack.copy();
            newStack.remove(CookItComponents.COOKING_COMPONENT);
            newStack.remove(CookItComponents.COLOR_COMPONENT);
            entity.readComponents(newStack);
            ItemStack first = entity.getStack(0);
            if (!first.isEmpty() && first.isOf(CookItItems.GOOP)) {
                world.scheduleBlockTick(pos, state.getBlock(), 1);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        world.setBlockState(pos, state.with(HAS_GOOP, true));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.125f, 0f, 0.125f, 0.875f, 0.5f, 0.875f);
    }
    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return BlockEntityUtils.dropWithComponent(this, builder);
    }
    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return BlockEntityUtils.getPickStack(this, (MixingBowlEntity) world.getBlockEntity(pos));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MixingBowlEntity(pos, state);
    }
}