package dev.toasttextures.cookit.client.render.model;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ChefOutfitModel {

    public static LayerDefinition getModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(16, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.3125F))
                .texOffs(16,32)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.625F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
               .texOffs(40, 16)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3125F))
               .texOffs(40, 32)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.625F)),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

           root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                           .texOffs(48, 48)
                           .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.625F)),
                           PartPose.offset(5.0F, 2.0F, 0.0F))
                   .addOrReplaceChild("left_sleeve", CubeListBuilder.create()
                           .texOffs(32, 48)
                           .addBox(-1.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3125F)),
                           PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.3125F))
                .texOffs(0, 32)
                .addBox(-1.9F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.625F)),
                PartPose.offset(-2.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(16, 48)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.3125F))
                .texOffs(0, 48)
                .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.625F)),
                PartPose.offset(2.0F, 12.0F, 0.0F));
        return LayerDefinition.create(modelData, 64, 64);
    }
}
