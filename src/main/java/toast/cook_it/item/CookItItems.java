package toast.cook_it.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;

import static toast.cook_it.block.CookItBlocks.CUTTING_BOARD;
import static toast.cook_it.block.CookItBlocks.TOASTER;

public class CookItItems {

    public static final Item CHEF_HAT = registerItem("chef_hat", new Item(new FabricItemSettings().equipmentSlot(stack -> EquipmentSlot.HEAD)));
    //public static final Item CHEF_SHIRT = registerItem("chef_shirt", new ArmorItem(CHEF_OUTFIT, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item ROLLING_PIN = registerItem("rolling_pin", new Item(new FabricItemSettings()));

    public static final Item KITCHEN_KNIFE = registerItem("knife", new SwordItem(ToolMaterials.IRON, 1, 0.01f, new FabricItemSettings()));

    public static final Item TOAST = registerItem("toast", new Item(new FabricItemSettings()));
    public static final Item CROISSANT = registerItem("croissant", new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(3).build())));
    public static final Item COOKIE = registerItem("cookie", new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(3).build())));
    public static final Item FIRE_EXTINGUISHER = registerItem("fire_extinguisher", new FireExtinguisherItem(new FabricItemSettings().maxDamage(256)));
    public static final Item BUTCHER_KNIFE = registerItem("butcher_knife", new Item(new FabricItemSettings()));
    public static final Item MILK = registerItem("milk", new Item(new FabricItemSettings()));

    private static void creativeEntries(FabricItemGroupEntries entries) {

        entries.add(CHEF_HAT);
        //entries.add(CHEF_SHIRT);
        entries.add(ROLLING_PIN);
        entries.add(CUTTING_BOARD);
        entries.add(KITCHEN_KNIFE);
        entries.add(TOASTER);
        entries.add(TOAST);
        entries.add(CROISSANT);
        entries.add(COOKIE);
    }
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CookIt.MOD_ID, name), item);
    }
    public static void registerItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(CookItItems::creativeEntries);
    }
}

