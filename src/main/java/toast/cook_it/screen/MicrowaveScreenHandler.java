package toast.cook_it.screen;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import toast.cook_it.CookIt;
import toast.cook_it.block.microwave.MicrowaveEntity;

public class MicrowaveScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final MicrowaveEntity blockEntity;

    public MicrowaveScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(2));
    }

    public MicrowaveScreenHandler(int syncId, PlayerInventory pInventory, BlockEntity blockEntity,
                                  PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.MICROWAVE_SCREEN_HANDLER, syncId);
        checkSize(((Inventory) blockEntity), 2);
        this.inventory=((Inventory) blockEntity);
        inventory.onOpen(pInventory.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = ((MicrowaveEntity) blockEntity);

        this.addSlot(new Slot(inventory, 0, 80, 14));
        this.addSlot(new Slot(inventory, 1, 80, 62));
        addPlayerInventory(pInventory);
        addPlayerHotbar(pInventory);
        addProperties(arrayPropertyDelegate);
    }
    /*protected ModScreenHandlers(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }*/

    public boolean isCrafting() { return propertyDelegate.get(0) > 0; }
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
    //copy pastin time
    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public int getScaledProgress() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        int progressArrowSize = 26;

        return maxProgress !=0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getDigit2() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        int dif = (maxProgress-progress)/20;
        while (dif>9) {
            dif = dif-10;
        }
        return dif;
        //CookIt.LOGGER.info(String.valueOf((maxProgress-progress)/20));
        //return (maxProgress-progress)/20;
    }
    public int getDigit1() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);

        int dif = (maxProgress-progress)/20; // seconds
        int digit=0;
        while (dif>9) {
            dif = dif-10; // ones of seconds
            digit++; // tens of seconds
        }
        return digit;
    }
}
