package toast.cook_it.block.food_blocks.pizza;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import toast.cook_it.registries.CookItBlocks;

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

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {

        return VoxelShapes.cuboid(0.25f, 0f, 0.25f, 0.75f, 0.0625f, 0.75f);
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
                    player.getInventory().offerOrDrop(new ItemStack(CookItBlocks.PIZZA));
                    world.setBlockState(pos, state.with(PIZZA_AMOUNT, pizzaAmount - 1));
                } else {
                    world.breakBlock(pos, false);
                }
            }
        }
        return ActionResult.PASS;
    }

}
