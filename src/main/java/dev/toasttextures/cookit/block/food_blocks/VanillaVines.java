package dev.toasttextures.cookit.block.food_blocks;

import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public interface VanillaVines {
    VoxelShape EAST = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    VoxelShape WEST = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    VoxelShape SOUTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    VoxelShape NORTH = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    IntProperty PLANT_STATE = IntProperty.of("plant_state", 0, 2);

    static ActionResult removeVanilla(@Nullable Entity picker, BlockState state, World world, BlockPos pos) {
        if (state.get(PLANT_STATE) == 2) {
            Block.dropStack(world, pos, new ItemStack(CookItItems.VANILLA_BEAN, 1));
            float f = MathHelper.nextBetween(world.random, 0.8F, 1.2F);
            world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, f);
            BlockState blockState = state.with(PLANT_STATE, 0);
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(picker, blockState));
            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.PASS;
        }
    }

    static boolean hasVanilla(BlockState state) {
        return state.contains(PLANT_STATE) && state.get(PLANT_STATE) == 2;
    }

}
