package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.block.WoodType;
import dev.toasttextures.cookit.item.*;
import dev.toasttextures.cookit.item.armor.chef.ChefOutfitItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import dev.toasttextures.cookit.CookIt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static dev.toasttextures.cookit.registries.CookItBlocks.*;

public class CookItItems {
    public static final Component UNCOOKED_TEXT = Component.translatable("stage.cook-it.uncooked").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);

    public static final List<Item> ITEMS = new ArrayList<>();
    public static final List<RollingPin> ROLLING_PINS = registerWooden("rolling_pin", settings -> new FabricItemSettings(), RollingPin::new);

    // -- Utensils --
    public static final Item KITCHEN_KNIFE = registerItem("knife", new SwordItem(Tiers.IRON, 1, -2, new FabricItemSettings().maxCount(1)));
    public static final Item BUTCHER_KNIFE = registerItem("butcher_knife", new SwordItem(Tiers.IRON, 1, -1, new FabricItemSettings()));
    public static final Item SPATULA = registerItem("spatula", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item WHISK = registerItem("whisk", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item DONUT_CUTTER = registerItem("donut_cutter", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item FRYER_BASKET = registerItem("fryer_basket", new FryerBasket(new FabricItemSettings().stacksTo(1)));

    // -- Ingredients --
    public static final Item DOUGH = registerItem("dough", new Item(new FabricItemSettings()));
    public static final Item DOUGH_ROLLED = registerItem("dough_rolled", new Item(new FabricItemSettings()));
    public static final Item DOUGH_SLICED = registerItem("dough_sliced", new Item(new FabricItemSettings()));
    public static final Item UNCOOKED_FRIES = registerItem("uncooked_fries", new Fries(new FabricItemSettings()));

    // -- Food --
    public static final Item VANILLA_BEAN = registerItem("vanilla_bean", new ItemNameBlockItem(CookItBlocks.VANILLA_VINE_STEM, new FabricItemSettings()));
    public static final Item CHEESE = registerItem("cheese", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(3).build())));
    public static final Item TOAST = registerItem("toast", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build())));
    public static final Item RAW_CROISSANT = registerItem("raw_croissant", new Item(new FabricItemSettings()));
    public static final Item CROISSANT = registerItem("croissant", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(3).build())));
    public static final Item PIZZA_SLICE = registerItem("pizza_slice", new PizzaSlice(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(5).build())));
    public static final Item FRIES = registerItem("fries", new Fries(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build())));
    public static final Item RAW_DONUT = registerItem("raw_donut", new Item(new FabricItemSettings()));
    public static final Item DONUT = registerItem("plain_donut", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(3).build()), Donut.Type.PLAIN));
    public static final Item CHOCOLATE_DONUT = registerItem("chocolate_donut", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build()), Donut.Type.PLAIN));
    public static final Item CHOCOLATE_DONUT_SPRINKLES = registerItem("chocolate_donut_sprinkles", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build()), Donut.Type.SPRINKLES));
    public static final Item CHOCOLATE_DONUT_STRIPED = registerItem("chocolate_donut_striped", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build()), Donut.Type.STRIPED));
    public static final Item SWEET_BERRY_DONUT = registerItem("sweet_berry_donut", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build()), Donut.Type.PLAIN));
    public static final Item SWEET_BERRY_DONUT_SPRINKLES = registerItem("sweet_berry_donut_sprinkles", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build()), Donut.Type.SPRINKLES));
    public static final Item VANILLA_DONUT = registerItem("vanilla_donut", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build()), Donut.Type.PLAIN));
    public static final Item VANILLA_DONUT_SPRINKLES = registerItem("vanilla_donut_sprinkles", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build()), Donut.Type.SPRINKLES));
    public static final Item VANILLA_DONUT_STRIPED = registerItem("vanilla_donut_striped", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build()), Donut.Type.STRIPED));
    public static final Item DONUT_WITH_NUTS = registerItem("peanut_donut", new Donut(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build()), Donut.Type.PLAIN));

    public static final Item MUFFIN = registerItem("plain_muffin", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(5).build())));
    public static final Item BLUEBERRY_MUFFIN = registerItem("blueberry_muffin", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(7).build())));
    public static final Item SWEET_BERRY_MUFFIN = registerItem("sweet_berry_muffin", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(7).build())));
    public static final Item CHOCOLATE_CHIP_MUFFIN = registerItem("chocolate_chip_muffin", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(6).build())));
    public static final Item CHOCOLATE_MUFFIN = registerItem("chocolate_muffin", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(8).build())));

    public static final Item RAW_CINNAMON_ROLL = registerItem("raw_cinnamon_roll", new Item(new FabricItemSettings()));
    public static final Item CINNAMON_ROLL = registerItem("cinnamon_roll", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(3).build())));
    public static final Item CINNAMON_ROLL_GLAZED = registerItem("cinnamon_roll_glazed", new Item(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(4).build())));

    // -- Accessories --
    public static final Item CHEF_HAT = registerItem("chef_hat", new Item(new FabricItemSettings().equipmentSlot(stack -> EquipmentSlot.HEAD)));
    public static final Item CHEF_UNIFORM = registerItem("chef_uniform", new ChefOutfitItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item CHEF_PANTS = registerItem("chef_pants", new ChefOutfitItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));

    // -- Miscellaneous --
    public static final Item FIRE_EXTINGUISHER = registerItem("fire_extinguisher", new FireExtinguisherItem(new FabricItemSettings().durability(256)));
    public static final Item GOOP = registerItem("goop", new Item(new FabricItemSettings()));

    //public static final Item SPRINKLES = registerItem("sprinkles", new Item(new FabricItemSettings()));
    // public static final Item MILK = registerItem("milk", new Item(new FabricItemSettings()));

    private static <T extends Item> List<T> registerWooden(String suffix, Function<WoodType, Item.Properties> settingsProvider, BiFunction<Item.Properties, WoodType, T> item) {
        List<T> list = new ArrayList<>();
        for (WoodType type : WoodType.values()) {
            Item.Properties settings = settingsProvider.apply(type);
            list.add(registerItem(type + "_" + suffix, item.apply(settings, type)));
        }
        return list;
    }

    private static <T extends Item> T registerItem(String name, T item) {
        CookItItems.ITEMS.add(item);
        return Registry.register(BuiltInRegistries.ITEM, CookIt.idOf(name), item);
    }

    public static void register() {
        CookItItems.ITEMS.remove(GOOP);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CookIt.idOf("items"), CookItItems.COOK_IT_GROUP);
    }
    public static final CreativeModeTab COOK_IT_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CHEF_HAT))
            .title(Component.translatable("itemGroup.cook-it.items"))
            .displayItems((context, entries) -> {
                for (Item item : ITEMS) {
                    entries.accept(item);
                }

                for (Block block : BLOCKS) {
                    if (!(block.equals(VANILLA_VINE_STEM) || block.equals(VANILLA_VINE))) {
                        entries.accept(block);
                    }
                }
            })
            .build();
}