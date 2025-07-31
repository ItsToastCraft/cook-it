package dev.toasttextures.cookit.item.armor.ChefOutfit.render;

import net.minecraft.client.model.*;

public class ChefOutfitModel {
    public final ModelPart head;
    public final ModelPart body;
    public final ModelPart right_arm;
    public final ModelPart left_arm;
    public final ModelPart right_leg;
    public final ModelPart left_leg;

    public ChefOutfitModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.right_arm = root.getChild("right_arm");
        this.left_arm = root.getChild("left_arm");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
    }
    public static TexturedModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);
        modelPartData.addChild("head", ModelPartBuilder.create(), ModelTransform.NONE);
        modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(16, 16)
                .cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.3125F))
                .uv(16,32)
                .cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.625F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("right_arm", ModelPartBuilder.create()
               .uv(40, 16)
               .cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.3125F))
               .uv(40, 32)
               .cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.625F)),
                ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

           modelPartData.addChild("left_arm", ModelPartBuilder.create()
                           .uv(48, 48)
                           .cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.625F)),
                           ModelTransform.pivot(5.0F, 2.0F, 0.0F))
                   .addChild("left_sleeve", ModelPartBuilder.create()
                           .uv(32, 48)
                           .cuboid(-1.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.3125F)),
                           ModelTransform.of(0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        modelPartData.addChild("right_leg", ModelPartBuilder.create()
                .uv(0, 16)
                .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.3125F))
                .uv(0, 32)
                .cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.625F)),
                ModelTransform.pivot(-2.0F, 12.0F, 0.0F));
        modelPartData.addChild("left_leg", ModelPartBuilder.create()
                .uv(16, 48)
                .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.3125F))
                .uv(0, 48)
                .cuboid(-2.1F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.625F)),
                ModelTransform.pivot(2.0F, 12.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}
