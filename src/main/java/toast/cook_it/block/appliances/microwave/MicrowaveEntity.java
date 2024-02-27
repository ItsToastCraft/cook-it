package toast.cook_it.block.appliances.microwave;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import toast.cook_it.CookIt;
import toast.cook_it.block.ImplementedInventory;
import toast.cook_it.recipes.MicrowaveRecipe;
import toast.cook_it.registries.CookItBlockEntities;
import toast.cook_it.registries.CookItSounds;
import toast.cook_it.screen.MicrowaveScreenHandler;

import java.util.Objects;
import java.util.Optional;

import static toast.cook_it.block.appliances.microwave.Microwave.ON;
import static toast.cook_it.block.appliances.microwave.Microwave.OPEN;

public class MicrowaveEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {

    private final int INPUT_SLOT = 0;
    private final int OUTPUT_SLOT = 1;
    private static final int MICROWAVE_SOUND_INTERVAL = 111;

    protected final PropertyDelegate propertyDelegate;
    int progress = 0;
    private int maxProgress = 72; //make it dependent on item someday

    private final int invSize = 2;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(invSize, ItemStack.EMPTY); // Inventory

    public MicrowaveEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MICROWAVE_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MicrowaveEntity.this.progress;
                    case 1 -> MicrowaveEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MicrowaveEntity.this.progress = value;
                    case 1 -> MicrowaveEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }




    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }




    @Override
    public void readNbt(NbtCompound nbt) {
        //items.clear();
        super.readNbt(nbt);

        Inventories.readNbt(nbt, this.items);
        progress = nbt.getInt("microwave.progress");

    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
        nbt.putInt("microwave.progress", progress);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("panel.cook-it.microwave");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MicrowaveScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }


    public void tick(World world, BlockPos pos, BlockState state, BlockEntity entity) {
        if (world.isClient()) {
            return;
        }
        if (outputAvailable()) {
            if (this.hasRecipe()) {
                if (progress == 0 || world.getTime() % MICROWAVE_SOUND_INTERVAL == 0) {
                    playMicrowaveSound(world, pos, state, true);
                }

                this.updateMaxProgress();
                this.addProgress();
                //if (state.get(OPEN)) playMicrowaveSound(world, pos, state, false);
                markDirty(world, pos, state);
                if (craftingFinished()) {
                    Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();
                    boolean curr_event = Objects.equals(recipe.get().value().getEvent(), "advancement:eggplosion");
                    this.craftRecipe();
                    playMicrowaveSound(world, pos, state, false);
                    this.resetProgress();


                    //now events :D
                    if (curr_event) {
                        CookIt.LOGGER.info("explosion!");
                        if (this.world != null) {
                            this.world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, World.ExplosionSourceType.BLOCK);
                        }
                    };
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            markDirty(world, pos, state);
        }
    }

    private void updateMaxProgress() {
        Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();

        maxProgress = recipe.get().value().getMaxProgress();
    }

    private void craftRecipe() {
        Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();

        this.removeStack(INPUT_SLOT, 1);

        this.setStack(OUTPUT_SLOT, new ItemStack(recipe.get().value().getResult(null).getItem(),
                getStack(OUTPUT_SLOT).getCount() + recipe.get().value().getResult(null).getCount()));
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private boolean craftingFinished() {
        return this.maxProgress<=this.progress;
    }

    private void addProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();

        return recipe.isPresent() && insertableOutputAmount(recipe.get().value().getResult(null)) &&
                insertableOutputItem(recipe.get().value().getResult(null).getItem());
    }

    private Optional<RecipeEntry<MicrowaveRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());

        for(int i=0;i<this.size();i++) {
            inv.setStack(i, this.getStack(i));
        }

        return getWorld().getRecipeManager().getFirstMatch(MicrowaveRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean insertableOutputItem(Item item) {
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean insertableOutputAmount(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }



    private boolean outputAvailable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    private void playMicrowaveSound(World world, BlockPos pos, BlockState state, boolean on) {
        if (on && !state.get(OPEN)) {
            world.playSound(null, pos, CookItSounds.MICROWAVE_SOUND_EVENT, SoundCategory.BLOCKS, 0.3f, 1.0f);
            world.setBlockState(pos, state.with(ON, true));
        } else {
            MinecraftClient.getInstance().getSoundManager().stopSounds(CookItSounds.MICROWAVE_SOUND, SoundCategory.BLOCKS);
            world.setBlockState(pos, state.with(ON, false));
            if (state.get(OPEN)) return;
            world.playSound(null, pos, CookItSounds.MICROWAVE_BEEP_EVENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }
}