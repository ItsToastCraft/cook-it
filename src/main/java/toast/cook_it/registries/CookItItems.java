package toast.cook_it.registries;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;
import toast.cook_it.item.FireExtinguisherItem;

import java.util.ArrayList;
import java.util.List;

import static toast.cook_it.registries.CookItBlocks.BLOCKS;

public class CookItItems {
    public static final List<Item> ITEMS = new ArrayList<>();
    public static final Item CHEF_HAT = registerItem("chef_hat", new Item(new FabricItemSettings().equipmentSlot(stack -> EquipmentSlot.HEAD)));
    //public static final Item CHEF_SHIRT = registerItem("chef_shirt", new ArmorItem(CHEF_OUTFIT, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));

    // -- Utensils --
    public static final Item ROLLING_PIN = registerItem("rolling_pin", new Item(new FabricItemSettings()));
    public static final Item KITCHEN_KNIFE = registerItem("knife", new SwordItem(ToolMaterials.IRON, 1, -2, new FabricItemSettings()));
    public static final Item BUTCHER_KNIFE = registerItem("butcher_knife", new SwordItem(ToolMaterials.IRON, 1, -1, new FabricItemSettings()));

    public static final Item SPATULA = registerItem("spatula", new Item(new FabricItemSettings()));
    public static final Item WHISk = registerItem("whisk", new Item(new FabricItemSettings()));

    public static final Item TOAST = registerItem("toast", new Item(new FabricItemSettings()));
    public static final Item RAW_CROISSANT = registerItem("raw_croissant", new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(1).build())));
    public static final Item CROISSANT = registerItem("croissant", new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(3).build())));
    public static final Item FIRE_EXTINGUISHER = registerItem("fire_extinguisher", new FireExtinguisherItem(new FabricItemSettings().maxDamage(256)));

    //public static final Item MILK = registerItem("milk", new Item(new FabricItemSettings()));
    public static final Item PIZZA_SLICE = registerItem("pizza_slice", new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(5).build())));

    private static Item registerItem(String name, Item item) {
        CookItItems.ITEMS.add(item);

        return Registry.register(Registries.ITEM, new Identifier(CookIt.MOD_ID, name), item);
    }
    public static void registerItems() {

    }
     public static final ItemGroup COOK_IT_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CHEF_HAT))
            .displayName(Text.translatable("itemGroup.cook-it.items"))
            .entries((context, entries) -> {
                for (Item item : ITEMS) {
                    entries.add(item);
                }
                for (Block block : BLOCKS) {
                    entries.add(block);
                }
            })
            .build();
}

