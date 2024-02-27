package toast.cook_it.block.containers.baking_sheet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BakingSheet extends Block implements BlockEntityProvider {

    public BakingSheet(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BakingSheetEntity(pos, state);
    }

    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_LISTENERS);
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BakingSheetEntity blockEntity = (BakingSheetEntity) world.getBlockEntity(blockPos);

            if (!player.getStackInHand(hand).isEmpty()) {
                // Check what is the first open slot and put an item from the player's hand there
                for (int i = 0; i < blockEntity.getItems().size(); i++) {
                    if (blockEntity.getStack(i).isEmpty()) {
                        // Put the stack the player is holding into the inventory
                        blockEntity.setStack(i, new ItemStack(player.getStackInHand(hand).getItem(), 1));
                        player.getStackInHand(hand).decrement(1);
                        break;
                    }
                }
            } else {
                // If the player is not holding anything, give them the items in the block entity one by one
                for (int i = blockEntity.getItems().size() - 1; i >= 0; i--) {
                    // Find the first slot that has an item and give it to the player
                    if (!blockEntity.getStack(i).isEmpty()) {
                        // Give the player the stack in the inventory
                        player.getInventory().offerOrDrop(blockEntity.getStack(i));
                        // Remove the stack from the inventory
                        blockEntity.setStack(i, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }


        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.25f, 0f, 0.125f, 0.75f, 0.4375f, 0.875f);
    }
}