package dev.toasttextures.cookit.block.food.vanilla_vines;

import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.InteractionResult;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.Block.box;

public interface VanillaVines {
    VoxelShape EAST = box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    VoxelShape WEST = box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    VoxelShape SOUTH = box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    VoxelShape NORTH = box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    default VoxelShape getOutlineShape(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case WEST -> WEST;
            case SOUTH -> SOUTH;
            default -> EAST;
        };
    }

    EnumProperty<Stage> PLANT_STATE = EnumProperty.create("plant_state", Stage.class);

    float GROW_CHANCE = 0.125f;

    static InteractionResult removeVanilla(@Nullable Entity picker, BlockState state, Level world, BlockPos pos) {
        if (hasVanilla(state)) {
            Block.popResource(world, pos, new ItemStack(CookItItems.VANILLA_BEAN, 1));
            float f = Mth.randomBetween(world.random, 0.8F, 1.2F);
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, f);
            BlockState blockState = state.setValue(PLANT_STATE, Stage.EMPTY);
            world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(picker, blockState));
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return InteractionResult.PASS;
    }

    static boolean hasVanilla(BlockState state) {
        return state.hasProperty(PLANT_STATE) && state.getValue(PLANT_STATE) == Stage.BLOOMED;
    }

    enum Stage implements StringRepresentable {
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
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}