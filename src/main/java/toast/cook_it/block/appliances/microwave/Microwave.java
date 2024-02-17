package toast.cook_it.block.appliances.microwave;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class Microwave extends TranslucentBlock implements BlockEntityProvider {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final BooleanProperty ON = BooleanProperty.of("on");

    public Microwave(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(ON, false));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN).add(ON);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.0625f, 0f, 0.1875, 0.9375f, 0.5f, 0.8125f);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        boolean open = state.get(OPEN);

        MicrowaveEntity blockEntity = (MicrowaveEntity) world.getBlockEntity(pos);

        ItemStack heldItem = player.getStackInHand(hand);

        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            if (!open && heldItem.isEmpty()) {
                world.setBlockState(pos, state.with(OPEN, true).with(ON, false));
                world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS);
                return ActionResult.PASS;
            }
            if (!heldItem.isEmpty()) {
                world.setBlockState(pos, state.with(OPEN, false).with(ON, true));
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

    private void scheduleTick(WorldAccess world, BlockPos pos, int delay) {
        if (!world.isClient() && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.scheduleBlockTick(pos, this, delay);
        }

    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {


    }

    @Nullable
    @Override
    public MicrowaveEntity createBlockEntity(BlockPos pos, BlockState state) { return new MicrowaveEntity(pos, state); }

}
