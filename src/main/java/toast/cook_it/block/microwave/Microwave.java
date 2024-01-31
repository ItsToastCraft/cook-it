package toast.cook_it.block.microwave;
//am cooking in notepad
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

public class Microwave extends Block implements BlockEntityProvider {


    public Microwave(Settings settings) {
        super(settings);
    }
    private string curritem = "foknull"; // gotta give myself some credits FOR DOING WHATEVER THE TOAST IS THAT
    private int sTime = 25; //in seconds idk, someday will be dependent on items
  
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MicrowaveEntity(pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) { //is that even a function 🔥🔥🔥 I copied it from some 10 year old page
        nbt.putInt("stepTime", sTime);
 
        super.writeNbt(nbt);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) { //so many things I think I didn't even import 
      player.damageItem(1); //I have no idea if that function exists
      readNbt(state.nbt); //>??????????????????????????
      if (curritem!="foknull") {
        player.sendMessage("go toast yourself im not done");
      } else {
        player.damageItem(1);
      }
    @Override
    public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
 
      curritem = nbt.getInt("currentItem");
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.4f, 0f, 0.25f, 0.5f, 0.5f, 0.475f); //I don't even know the hell's toasting there
    }
}
//nevermind im not cooking anymore the toasted oven im doing here
