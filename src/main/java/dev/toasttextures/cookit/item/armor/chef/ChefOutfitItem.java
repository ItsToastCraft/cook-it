package dev.toasttextures.cookit.item.armor.chef;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.client.CookItEntityModelLayers;
import dev.toasttextures.cookit.item.armor.ArmorModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ChefOutfitItem extends ArmorItem {
    public static final Identifier texture = CookIt.idOf("textures/armor/chef_outfit.png");

    @Environment(EnvType.CLIENT)
    private BipedEntityModel<LivingEntity> model;

    public ChefOutfitItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRepair(ItemStack first, ItemStack second) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    protected BipedEntityModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
        var models = MinecraftClient.getInstance().getEntityModelLoader();

        return new ArmorModel(models.getModelPart(CookItEntityModelLayers.CHEF_OUTFIT), slot);
    }

    @Environment(EnvType.CLIENT)
    public BipedEntityModel<LivingEntity> getArmorModel() {
        if (model == null) {
            model = provideArmorModelForSlot(getSlotType());
        }
        return model;
    }
}