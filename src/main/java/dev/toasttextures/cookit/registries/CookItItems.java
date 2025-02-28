package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.item.*;
import dev.toasttextures.cookit.item.armor.ChefOutfit.ChefOutfitItem;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.CookIt;

import java.util.ArrayList;
import java.util.List;

import static dev.toasttextures.cookit.CookIt.SUPPORTED_WOOD_TYPES;
import static dev.toasttextures.cookit.registries.CookItBlocks.BLOCKS;

public class CookItItems {
    public static final List<Item> ITEMS = new ArrayList<>();
    public static final List<Item> ROLLING_PINS = new ArrayList<>();
    public static final List<Item> MUFFINS = new ArrayList<>();
    // -- Utensils --

    public static final Item KITCHEN_KNIFE = registerItem("knife", new SwordItem(ToolMaterials.IRON, new Item.Settings().maxCount(1).attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.IRON, 2, -1))));
    public static final Item BUTCHER_KNIFE = registerItem("butcher_knife", new SwordItem(ToolMaterials.IRON, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.IRON, 1, -1))));
    public static final Item SPATULA = registerItem("spatula", new Item(new Item.Settings().maxCount(1)));
    public static final Item WHISK = registerItem("whisk", new Item(new Item.Settings().maxCount(1)));
    public static final Item DONUT_CUTTER = registerItem("donut_cutter", new Item(new Item.Settings().maxCount(1)));
    public static final Item FRYER_BASKET = registerItem("fryer_basket", new FryerBasket(new Item.Settings().maxCount(1)));

    // -- Ingredients --
    public static final Item DOUGH = registerItem("dough", new Item(new Item.Settings()));
    public static final Item DOUGH_ROLLED = registerItem("dough_rolled", new Item(new Item.Settings()));
    public static final Item DOUGH_SLICED = registerItem("dough_sliced", new Item(new Item.Settings()));
    public static final Item UNCOOKED_FRENCH_FRIES = registerItem("uncooked_french_fries", new Fries(new Item.Settings(), CookItFoodTypes.FRYING));

    // -- Food --
    public static final Item VANILLA_BEAN = registerItem("vanilla_bean", new AliasedBlockItem(CookItBlocks.VANILLA_VINE_STEM, new Item.Settings()));
    public static final Item CHEESE = registerItem("cheese", new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(3).build())));
    public static final Item TOAST = registerItem("toast", new CookItFood(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build()), CookItFoodTypes.DONE));
    public static final Item RAW_CROISSANT = registerItem("raw_croissant", new CookItFood(new Item.Settings(), CookItFoodTypes.BAKING));
    public static final Item CROISSANT = registerItem("croissant", new CookItFood(new Item.Settings().food(new FoodComponent.Builder().nutrition(3).build()), CookItFoodTypes.DONE));
    public static final Item PIZZA_SLICE = registerItem("pizza_slice", new PizzaSlice(new Item.Settings().food(new FoodComponent.Builder().nutrition(5).build())));
    public static final Item FRENCH_FRIES = registerItem("french_fries", new Fries(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build()), CookItFoodTypes.DONE));
    public static final Item RAW_DONUT = registerItem("raw_donut", new CookItFood(new Item.Settings(), CookItFoodTypes.FRYING));
    public static final Item DONUT = registerItem("plain_donut", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(3).build())));
    public static final Item CHOCOLATE_DONUT = registerItem("chocolate_donut", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build())));
    public static final Item CHOCOLATE_DONUT_SPRINKLES = registerItem("chocolate_donut_sprinkles", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build())));
    public static final Item CHOCOLATE_DONUT_STRIPED = registerItem("chocolate_donut_striped", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build())));
    public static final Item SWEET_BERRY_DONUT = registerItem("sweet_berry_donut", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build())));
    public static final Item SWEET_BERRY_DONUT_SPRINKLES = registerItem("sweet_berry_donut_sprinkles", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build())));
    public static final Item VANILLA_DONUT = registerItem("vanilla_donut", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build())));
    public static final Item VANILLA_DONUT_SPRINKLES = registerItem("vanilla_donut_sprinkles", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build())));

    public static final Item VANILLA_DONUT_STRIPED = registerItem("vanilla_donut_striped", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build())));
    public static final Item DONUT_WITH_NUTS = registerItem("peanut_donut", new Donut(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build())));

    public static final Item MUFFIN = registerItem("plain_muffin", new CookItFood(new Item.Settings().food(new FoodComponent.Builder().nutrition(5).build()), CookItFoodTypes.DONE));
    public static final Item BLUEBERRY_MUFFIN = registerItem("blueberry_muffin", new CookItFood(new Item.Settings().food(new FoodComponent.Builder().nutrition(7).build()), CookItFoodTypes.DONE));
    public static final Item SWEET_BERRY_MUFFIN = registerItem("sweet_berry_muffin", new CookItFood(new Item.Settings().food(new FoodComponent.Builder().nutrition(7).build()), CookItFoodTypes.DONE));
    public static final Item CHOCOLATE_CHIP_MUFFIN = registerItem("chocolate_chip_muffin", new CookItFood(new Item.Settings().food(new FoodComponent.Builder().nutrition(6).build()), CookItFoodTypes.DONE));
    public static final Item CHOCOLATE_MUFFIN = registerItem("chocolate_muffin", new CookItFood(new Item.Settings().food(new FoodComponent.Builder().nutrition(8).build()), CookItFoodTypes.DONE));

    public static final Item RAW_CINNAMON_ROLL = registerItem("raw_cinnamon_roll", new CookItFood(new Item.Settings(), CookItFoodTypes.BAKING));
    public static final Item CINNAMON_ROLL = registerItem("cinnamon_roll", new CookItFood(new Item.Settings().food(new FoodComponent.Builder().nutrition(3).build()), CookItFoodTypes.DONE));
    public static final Item CINNAMON_ROLL_GLAZED = registerItem("cinnamon_roll_glazed", new CookItFood(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build()), CookItFoodTypes.DONE));

    // -- Accessories --
    public static final Item CHEF_HAT = registerItem("chef_hat", new Item(new Item.Settings().equipmentSlot(stack -> EquipmentSlot.HEAD)));
    public static final Item CHEF_UNIFORM = registerItem("chef_uniform", new ChefOutfitItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final Item CHEF_PANTS = registerItem("chef_pants", new ChefOutfitItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Settings()));

    // -- Miscellaneous --
    public static final Item FIRE_EXTINGUISHER = registerItem("fire_extinguisher", new FireExtinguisherItem(new Item.Settings().maxDamage(256)));
    public static final Item GOOP = registerItem("goop", new Item(new Item.Settings()));

    //public static final Item SPRINKLES = registerItem("sprinkles", new Item(new Item.Settings()));
    // public static final Item MILK = registerItem("milk", new Item(new Item.Settings()));

    public static void registerWoodenItems() {
        for (String woodType : SUPPORTED_WOOD_TYPES) {
            Item ROLLING_PIN = registerItem(woodType + "_rolling_pin", new Item(new Item.Settings()));
            ROLLING_PINS.add(ROLLING_PIN);
        }
    }
    private static Item registerItem(String name, Item item) {
        CookItItems.ITEMS.add(item);
        return Registry.register(Registries.ITEM, new Identifier(CookIt.MOD_ID, name), item);
    }
    public static void registerItems() {
        CookItItems.ITEMS.remove(GOOP);
        MUFFINS.add(MUFFIN);
        MUFFINS.add(BLUEBERRY_MUFFIN);
        MUFFINS.add(SWEET_BERRY_MUFFIN);
        MUFFINS.add(CHOCOLATE_CHIP_MUFFIN);
        MUFFINS.add(CHOCOLATE_MUFFIN);

        registerWoodenItems();
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

