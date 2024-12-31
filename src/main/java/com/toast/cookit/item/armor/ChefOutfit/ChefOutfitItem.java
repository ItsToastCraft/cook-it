package com.toast.cookit.item.armor.ChefOutfit;

import com.toast.cookit.CookItClient;
import com.toast.cookit.client.CookItEntityModelLayers;
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
import org.jetbrains.annotations.NotNull;

import static com.toast.cookit.CookIt.MOD_ID;

public class ChefOutfitItem extends ArmorItem {
    @Environment(EnvType.CLIENT)
    private BipedEntityModel<LivingEntity> model;

    public ChefOutfitItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }


    @Override
    public ItemStack getDefaultStack() {
        return new ItemStack(this);
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
    public boolean canRepair(ItemStack itemStack_1, ItemStack itemStack_2) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    protected BipedEntityModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
        var models = MinecraftClient.getInstance().getEntityModelLoader();

        return new FullArmorModel(models.getModelPart(CookItEntityModelLayers.CHEF_OUTFIT), slot);
    }

    @Environment(EnvType.CLIENT)
    public BipedEntityModel<LivingEntity> getArmorModel() {
        if (model == null) {
            model = provideArmorModelForSlot(getSlotType());
        }
        return model;
    }

    @NotNull
    public Identifier getArmorTexture() {

        return new Identifier(MOD_ID, "textures/armor/chef_outfit.png");
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }
}
