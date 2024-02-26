package toast.cook_it.block.appliances.microwave;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import toast.cook_it.registries.CookItBlockEntities;
import toast.cook_it.registries.CookItSounds;

public class Microwave extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final BooleanProperty ON = BooleanProperty.of("on");

    public Microwave(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(ON, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN).add(ON);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL /*that does somthing*/ ;
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


                world.playSound(null, pos, CookItSounds.MICROWAVE_EVENT, SoundCategory.BLOCKS);
                world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS);
            } else if (!blockEntity.getStack(0).isEmpty()){
                player.getInventory().insertStack(blockEntity.getStack(0));
            } else {

                world.setBlockState(pos, state.with(OPEN, false));

            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, CookItBlockEntities.MICROWAVE_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity));
    }


    @Nullable
    @Override
    public MicrowaveEntity createBlockEntity(BlockPos pos, BlockState state) { return new MicrowaveEntity(pos, state); }

}
