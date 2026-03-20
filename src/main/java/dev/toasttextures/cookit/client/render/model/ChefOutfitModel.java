package dev.toasttextures.cookit.client.render.model;

import net.minecraft.client.model.*;

public class ChefOutfitModel {

    public static TexturedModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        root.addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild("head", ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild("body", ModelPartBuilder.create()
                .uv(16, 16)
                .cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.3125F))
                .uv(16,32)
                .cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.625F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        root.addChild("right_arm", ModelPartBuilder.create()
               .uv(40, 16)
               .cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.3125F))
               .uv(40, 32)
               .cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.625F)),
                ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

           root.addChild("left_arm", ModelPartBuilder.create()
                           .uv(48, 48)
                           .cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.625F)),
                           ModelTransform.pivot(5.0F, 2.0F, 0.0F))
                   .addChild("left_sleeve", ModelPartBuilder.create()
                           .uv(32, 48)
                           .cuboid(-1.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.3125F)),
                           ModelTransform.of(0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addChild("right_leg", ModelPartBuilder.create()
                .uv(0, 16)
                .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.3125F))
                .uv(0, 32)
                .cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.625F)),
                ModelTransform.pivot(-2.0F, 12.0F, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create()
                .uv(16, 48)
                .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.3125F))
                .uv(0, 48)
                .cuboid(-2.1F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.625F)),
                ModelTransform.pivot(2.0F, 12.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}
