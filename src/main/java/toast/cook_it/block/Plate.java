package toast.cook_it.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import org.jetbrains.annotations.Nullable;

public class Plate extends Block implements BlockEntityProvider {

    public static final IntProperty PLATES_AMOUNT = IntProperty.of("plate_amount", 1, 4);

    public Plate(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(PLATES_AMOUNT, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PLATES_AMOUNT);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        int plateAmount = state.get(PLATES_AMOUNT);
        return VoxelShapes.cuboid(0.25f, 0f, 0.25f, 0.75f, 0.0625f * plateAmount, 0.75f);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            PlateEntity blockEntity = (PlateEntity) world.getBlockEntity(pos);
            ItemStack heldItem = player.getStackInHand(hand);
            int plateAmount = state.get(PLATES_AMOUNT);

            if (heldItem.getItem() instanceof BlockItem heldBlockItem) {
                Block heldBlock = heldBlockItem.getBlock();
                if (heldBlock == CookItBlocks.PLATE && plateAmount < 4) {
                    heldItem.decrement(1);
                    world.setBlockState(pos, state.with(PLATES_AMOUNT, plateAmount + 1));
                    return ActionResult.SUCCESS;
                }
            } else {
                if (heldItem.getItem() == Items.AIR && plateAmount == 1) {
                    player.getInventory().offerOrDrop(blockEntity.getStack(0));
                    blockEntity.setStack(0, ItemStack.EMPTY);
                } else if (blockEntity.getStack(0).isEmpty() && plateAmount == 1) {
                    blockEntity.setStack(0, new ItemStack(player.getStackInHand(hand).getItem(), 1));
                    player.getStackInHand(hand).decrement(1);
                }
            }

            return ActionResult.PASS;
        }
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlateEntity(pos, state);
    }
}