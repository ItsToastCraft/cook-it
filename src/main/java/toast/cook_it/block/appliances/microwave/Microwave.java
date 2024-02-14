package toast.cook_it.block.appliances.microwave;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
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
import org.jetbrains.annotations.Nullable;

public class Microwave extends Block implements BlockEntityProvider {
    public Microwave(Settings settings) {
        super(settings);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.0625f, 0f, 0.1875f, 0.9375f, 0.5f, 0.8125f);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        MicrowaveEntity blockEntity = (MicrowaveEntity) world.getBlockEntity(pos);

        ItemStack heldItem = player.getStackInHand(hand);

        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            if (heldItem.isEmpty() && !blockEntity.getStack(0).isEmpty()) {
                    player.getInventory().offerOrDrop(blockEntity.getStack(0));
                } else if (blockEntity.getStack(0).isEmpty()) {
                    blockEntity.setStack(0, new ItemStack(heldItem.getItem()));
                    heldItem.decrement(1);
                }
            }
        return ActionResult.SUCCESS;
        }

    @Nullable
    @Override

        public MicrowaveEntity createBlockEntity(BlockPos pos, BlockState state) { return new MicrowaveEntity(pos, state); }


}
