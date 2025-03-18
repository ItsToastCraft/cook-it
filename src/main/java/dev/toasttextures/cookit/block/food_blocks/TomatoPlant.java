package dev.toasttextures.cookit.block.food_blocks;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.registry.CookItItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TomatoPlant extends CropBlock {
    public TomatoPlant(Settings settings) {
        super(settings);
    }
    public static final MapCodec<TomatoPlant> CODEC = createCodec(TomatoPlant::new);
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F),
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 3.0F, 16.0F),
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 4.0F, 16.0F),
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 5.0F, 16.0F),
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 6.0F, 16.0F),
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 7.0F, 16.0F),
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 8.0F, 16.0F),
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 9.0F, 16.0F)};

    @Override
    public MapCodec<TomatoPlant> getCodec() {
        return CODEC;
    }

    protected ItemConvertible getSeedsItem() {
        return CookItItems.TOMATO_SEEDS;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[this.getAge(state)];
    }
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int age = state.get(AGE);
        if (age == 7) {
            dropStack(world, pos, new ItemStack(CookItItems.TOMATO, 1));
            world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlockState(pos, state.with(AGE, 4), Block.NOTIFY_LISTENERS);
            return ActionResult.success(world.isClient);
        } else {
            return super.onUse(state, world, pos, player, hit);
        }
    }
}
