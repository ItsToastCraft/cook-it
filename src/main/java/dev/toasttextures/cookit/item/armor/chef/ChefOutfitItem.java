package dev.toasttextures.cookit.item.armor.chef;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.client.CookItEntityModelLayers;
import dev.toasttextures.cookit.item.armor.ArmorModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class ChefOutfitItem extends ArmorItem {
    public static final ResourceLocation texture = CookIt.idOf("textures/armor/chef_outfit.png");

    @Environment(EnvType.CLIENT)
    private HumanoidModel<LivingEntity> model;

    public ChefOutfitItem(ArmorMaterial material, Type type, Properties settings) {
        super(material, type, settings);
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack first, ItemStack second) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    protected HumanoidModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
        var models = Minecraft.getInstance().getEntityModels();

        return new ArmorModel(models.bakeLayer(CookItEntityModelLayers.CHEF_OUTFIT), slot);
    }

    @Environment(EnvType.CLIENT)
    public HumanoidModel<LivingEntity> getArmorModel() {
        if (model == null) {
            model = provideArmorModelForSlot(getEquipmentSlot());
        }
        return model;
    }
}