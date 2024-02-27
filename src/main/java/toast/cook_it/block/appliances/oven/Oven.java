package toast.cook_it.block.appliances.oven;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Oven extends TranslucentBlock implements BlockEntityProvider {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final Property<Direction> FACING = Properties.HORIZONTAL_FACING;

    public Oven(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(ON, false).with(FACING, Direction.NORTH));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN).add(ON).add(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        boolean open = state.get(OPEN);
        OvenEntity blockEntity = (OvenEntity) world.getBlockEntity(pos);

        ItemStack heldItem = player.getStackInHand(hand);

        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            if (!open && heldItem.isEmpty()) {
                world.setBlockState(pos, state.with(OPEN, true));
                world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS);
                return ActionResult.PASS;
            }
            if (!heldItem.isEmpty()) {
                world.setBlockState(pos, state.with(OPEN, false));
                blockEntity.setStack(0, new ItemStack(heldItem.getItem(), 1));
                heldItem.decrement(1);

                world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS);
            } else if (!blockEntity.getStack(0).isEmpty()){

                player.getInventory().insertStack(blockEntity.getStack(0));
            } else {
                world.setBlockState(pos, state.with(OPEN, false));

            }
        }

        return ActionResult.SUCCESS;
    }
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public OvenEntity createBlockEntity(BlockPos pos, BlockState state) { return new OvenEntity(pos, state); }


}
