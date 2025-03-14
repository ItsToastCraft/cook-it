package dev.toasttextures.cookit.block.food_blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class LettucePlant extends CropBlock {
    public LettucePlant(Settings settings) {
        super(settings);
    }
    public static final MapCodec<LettucePlant> CODEC = createCodec(LettucePlant::new);
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F), Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 3.0F, 16.0F), Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 4.0F, 16.0F), Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 5.0F, 16.0F), Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 6.0F, 16.0F), Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 7.0F, 16.0F), Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 8.0F, 16.0F), Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 9.0F, 16.0F)};

    @Override
    public MapCodec<LettucePlant> getCodec() {
        return CODEC;
    }

    protected ItemConvertible getSeedsItem() {
        return Items.CARROT;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[this.getAge(state)];
    }
}
