package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.OvenRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class OvenEntity extends CookingBlockEntity<OvenRecipe> implements SlotProvider<OvenSlot> {
    private int[] progress = new int[2];
    private OvenSlot completed = null;

    private final List<OvenSlot> slots;
    public OvenEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.OVEN, OvenRecipe.Type.INSTANCE, pos, state, 2);
        Direction dir = state.get(HORIZONTAL_FACING);
        List<Vec3d> slots = OvenSlot.rotated(dir);

        this.slots = List.of(
            createSlot(slots.get(0), this, 0),
            createSlot(slots.get(1), this, 1)
        );
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        progress = nbt.getIntArray(PROGRESS_KEY);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putIntArray(PROGRESS_KEY, progress);
        super.writeNbt(nbt);
    }

    @Override
    public void craft(World world, OvenRecipe recipe) {
        if (completed == null) return;
        if (!items.get(completed.index).isEmpty() && completed.getStatus() == CookingStatus.DONE) {
            setStack(completed.index, recipe.craft(new SimpleInventory(items.get(completed.index)), world.getRegistryManager()));
        }
    }

    @Override
    public void reset() {
        progress[completed.index] = 0;
        completed = null;
        markDirty();
    }

    void updateStatus(OvenSlot slot) {
        completed = slot;
        CookingStatus first = slots.get(0).getStatus();
        CookingStatus second = slots.get(1).getStatus();

        if (first == second) {
            status = first;
            return;
        }

        List<CookingStatus> statuses = List.of(first, second);
        if (statuses.contains(CookingStatus.PROCESSING)) {
            status = CookingStatus.PROCESSING;
        } else if (statuses.contains(CookingStatus.DONE)) {
            status = CookingStatus.DONE;
        } else {
            status = CookingStatus.IDLE;
        }
    }

    @Override
    public List<OvenSlot> getSlots() {
        return slots;
    }

    @Override
    public <E extends BlockEntity> OvenSlot createSlot(Vec3d pos, E entity, int index) {
        return new OvenSlot(pos, (OvenEntity) entity, index);
    }

    @Override
    public OvenSlot getSlotAt(Vec3d entityPos, Vec3d clickPos) {
        return null;
    }

    public static void tick(World world, BlockPos pos, BlockState state, OvenEntity entity) {
        for (OvenSlot slot : entity.getSlots()) {
            slot.process(world, state);
        }
    }

    public int getProgress(int index) {
        return progress[index];
    }

    // Just so that it can't be called by a random class
    public void addProgress(OvenSlot slot) {
        progress[slot.index]++;
    }
}

//private int[] progress = new int[2];
//private boolean done = false;
//
//public OvenEntity(BlockPos pos, BlockState state) {
//    super(CookItBlockEntities.OVEN, pos, state, 2);
//}
//
//@Override
//public void readNbt(NbtCompound nbt) {
//    super.readNbt(nbt);
//    this.progress = nbt.getIntArray(PROGRESS_KEY);
//}
//
//@Override
//public void writeNbt(NbtCompound nbt) {
//    nbt.putIntArray(PROGRESS_KEY, progress);
//    super.writeNbt(nbt);
//}
//
//public void tick(World world, BlockPos pos, BlockState state) {
//    if (world.isClient()) {
//        return;
//    }
//    world.setBlockState(pos, state.with(DONE, !this.getItems().isEmpty() && this.done));
//    if (this.isEmpty()) { this.done = false; return;}
//    if (state.get(OPEN)) { return; }
//
//    for (int i = 0; i < this.size(); i++) {
//        ItemStack item = this.getStack(i);
//        if(item.isEmpty()) { break; }
//
//        Optional<RecipeEntry<OvenRecipe>> recipe = getCurrentRecipe(item);
//        if (item.isOf(CookItBlocks.MUFFIN_TIN.asItem())) {
//            this.processMuffinRecipe(world, pos, state, i);
//        } else if (recipe.isPresent()) {
//            if (recipe.get().value().getMaxProgress() >= this.progress[i]) {
//                this.progress[i]++;
//                this.done = false;
//            } else {
//                craftRecipe(i);
//                this.markDirty();
//                this.done = true;
//                world.setBlockState(pos, state.with(DONE, true));
//
//
//                this.progress[i] = 0;
//            }
//        } else { this.done = true; break; }
//    }
//
//}
//
//private void processMuffinRecipe(World world, BlockPos pos, BlockState state, int slot) {
//    if (this.done) return;
//    ItemStack muffinTin = this.getStack(slot);
//
//    NbtList nbtList = new NbtList();
//    ArrayList<ItemStack> containerItems = CookingBlockEntity.getContainerItems(muffinTin);
//
//    if (400 >= this.progress[slot]) {
//        this.progress[slot]++;
//        this.done = false;
//    } else {
//        for (int i = 0; i < containerItems.size(); i++) {
//            NbtCompound nbtCompound = new NbtCompound(); // Create a new compound for each iteration
//            nbtCompound.putByte("Slot", (byte) i);
//            ItemStack stack = containerItems.get(i);
//            if (stack.isOf(CookItItems.GOOP)) {
//                ItemStack muffin = ItemStack.fromNbt(stack.getSubNbt("output"));
//                muffin.writeNbt(nbtCompound);
//            } else {
//                containerItems.get(i).writeNbt(nbtCompound);
//            }
//            nbtList.add(nbtCompound);
//        }
//        muffinTin.getOrCreateSubNbt("BlockEntityTag").put("Items", nbtList);
//        this.markDirty();
//        this.done = true;
//        world.setBlockState(pos, state.with(DONE, true));
//        this.progress[slot] = 0;
//        world.playSound(null, this.getPos(), SoundEvent.of(new Identifier("block.note_block.xylophone")), SoundCategory.BLOCKS, 3.0f, 1.5f);
//        world.playSound(null, this.getPos(), SoundEvent.of(new Identifier("block.note_block.xylophone")), SoundCategory.BLOCKS, 3.0f, 2f);
//        world.playSound(null, this.getPos(), SoundEvent.of(new Identifier("block.note_block.xylophone")), SoundCategory.BLOCKS, 3.0f, 2.5f);
//
//    }
//
//
//}
//
//private void craftRecipe(int index) {
//    ItemStack item = this.getStack(index);
//    if (CookingBlockEntity.isContainer(item)) {
//        ArrayList<ItemStack> containerItems = CookingBlockEntity.getContainerItems(item);
//        NbtList nbtList = new NbtList();
//
//        for (int i = 0; i < containerItems.size(); i++) {
//            NbtCompound nbtCompound = new NbtCompound(); // Create a new compound for each iteration
//            nbtCompound.putByte("Slot", (byte) i);
//
//            Optional<RecipeEntry<OvenRecipe>> recipe = getCurrentRecipe(containerItems.get(i));
//            if (recipe.isPresent()) {
//                if (!containerItems.get(i).isEmpty() && recipe.get().value().getMaxProgress() <= this.progress[index]) {
//                    ItemStack output = recipe.get().value().craft(new SimpleInventory(item), this.world.getRegistryManager());
//                    if (containerItems.get(i).getNbt() != null) {
//                        output.setNbt(containerItems.get(i).getNbt());
//                    }
//                    output.writeNbt(nbtCompound);
//                }
//            } else {
//                containerItems.get(i).writeNbt(nbtCompound);
//            }
//            nbtList.add(nbtCompound);
//        }
//        item.getOrCreateSubNbt("BlockEntityTag").put("Items", nbtList);
//    } else {
//        Optional<RecipeEntry<OvenRecipe>> recipe = getCurrentRecipe(item);
//        NbtCompound nbtCompound = item.getOrCreateNbt();
//        this.removeStack(index, 1);
//        ItemStack result = recipe.get().value().craft(new SimpleInventory(item), this.world.getRegistryManager());
//        result.setNbt(nbtCompound);
//        this.setStack(index, result);
//    }
//    assert world != null;
//    world.playSound(null, this.getPos(), SoundEvent.of(new Identifier("block.note_block.xylophone")), SoundCategory.BLOCKS, 3.0f, 1.5f);
//}
//
//public static void tick(World world, BlockPos pos, BlockState state, OvenEntity entity) {
//    if (world.isClient) return;
//    ItemStack first = entity.items.getFirst();
//    if (first.isEmpty()) {
//        entity.status = CookingStatus.IDLE;
//    } else if (!first.isOf(CookItItems.FRYER_BASKET)) {
//        entity.status = CookingStatus.INVALID;
//    } else if (first == entity.cachedItem) {
//        entity.tickRecipe(world, state);
//    } else {
//        entity.loadRecipe(world, state);
//    }
//}