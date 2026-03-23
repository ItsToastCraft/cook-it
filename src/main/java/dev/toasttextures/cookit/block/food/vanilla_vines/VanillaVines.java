package dev.toasttextures.cookit.block.food.vanilla_vines;

import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.Block.createCuboidShape;

public interface VanillaVines {
    VoxelShape EAST = createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    VoxelShape WEST = createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    VoxelShape SOUTH = createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    VoxelShape NORTH = createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    default VoxelShape getOutlineShape(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case WEST -> WEST;
            case SOUTH -> SOUTH;
            default -> EAST;
        };
    }

    EnumProperty<Stage> PLANT_STATE = EnumProperty.of("plant_state", Stage.class);

    float GROW_CHANCE = 0.125f;

    static ActionResult removeVanilla(@Nullable Entity picker, BlockState state, World world, BlockPos pos) {
        if (hasVanilla(state)) {
            Block.dropStack(world, pos, new ItemStack(CookItItems.VANILLA_BEAN, 1));
            float f = MathHelper.nextBetween(world.random, 0.8F, 1.2F);
            world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, f);
            BlockState blockState = state.with(PLANT_STATE, Stage.EMPTY);
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(picker, blockState));
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    static boolean hasVanilla(BlockState state) {
        return state.contains(PLANT_STATE) && state.get(PLANT_STATE) == Stage.BLOOMED;
    }

    enum Stage implements StringIdentifiable {
        EMPTY,
        BLOOMED,
        HARVESTABLE;

        public Stage increment() {
            return switch (this) {
                case EMPTY -> BLOOMED;
                case BLOOMED -> HARVESTABLE;
                default -> this;
            };
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }
}
