package dev.toasttextures.cookit.item.armor;

import net.minecraft.client.model.geom.ModelPart;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class ArmorModel extends HumanoidModel<LivingEntity> {
    final EquipmentSlot slot;
    public ArmorModel(ModelPart root, EquipmentSlot slot) {
        super(root);
        this.slot = slot;
    }

    @Override
    public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        renderArmorSlot(slot);
        super.renderToBuffer(ms, buffer, light, overlay, r, g, b, a);
    }

    private void renderArmorSlot(EquipmentSlot slot) {
        setAllVisible(false);
        switch (slot) {
            case CHEST -> {
                body.visible = true;
                rightArm.visible = true;
                leftArm.visible = true;
            }
            case LEGS, FEET -> {
                rightLeg.visible = true;
                leftLeg.visible = true;
            }
            default -> {}
        }
    }
}
