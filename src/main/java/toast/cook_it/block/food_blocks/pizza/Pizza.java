package toast.cook_it.block.food_blocks.pizza;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import toast.cook_it.registries.CookItItems;

public class Pizza extends Block {
    public static final IntProperty PIZZA_AMOUNT = IntProperty.of("pizza_amount", 0, 4);


    public Pizza(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(PIZZA_AMOUNT, 4));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PIZZA_AMOUNT);
    }

    private final VoxelShape SLICE_1 = VoxelShapes.cuboid(0.0625f, 0.0f, 0.0625f, 0.5f, 0.125f, 0.5f);
    private final VoxelShape SLICE_2 = VoxelShapes.cuboid(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.125f, 0.5f);
    private final VoxelShape SLICE_3 = VoxelShapes.union(SLICE_2, createCuboidShape(8, 0.0f, 8, 15, 2, 15));
    private final VoxelShape SLICE_4 = VoxelShapes.cuboid(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.125f, 0.9375f);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        int pizzaAmount = state.get(PIZZA_AMOUNT);

        switch (pizzaAmount) {
            case (1) -> {
                return SLICE_1;
            }
            case (2) -> {
                return SLICE_2;
            }
            case (3) -> {
                return SLICE_3;
            }
            default -> {
                return SLICE_4;
            }
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        int pizzaAmount = state.get(PIZZA_AMOUNT);
        ItemStack heldItem = player.getStackInHand(hand);

        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            if (heldItem.isEmpty()) {
                if (pizzaAmount > 1) {
                    player.getInventory().offerOrDrop(new ItemStack(CookItItems.PIZZA_SLICE));
                    world.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS);
                    world.setBlockState(pos, state.with(PIZZA_AMOUNT, pizzaAmount - 1));
                } else {
                    world.breakBlock(pos, false);
                }
            }
        }
        return ActionResult.PASS;
    }


}
