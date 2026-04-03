package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.entity.PizzaEntity;
import dev.toasttextures.cookit.block.food.pizza.PizzaTopping;
import dev.toasttextures.cookit.client.CookItEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Axis;

import java.util.List;

@Environment(EnvType.CLIENT)
public class PizzaEntityRenderer implements BlockEntityRenderer<PizzaEntity> {
    private final List<ModelPart> pizzaBaseParts;
    private final List<ModelPart> toppingLayerParts;

    private final ResourceLocation CHEESE_BASE = CookIt.idOf("textures/entity/pizza/pizza_cheese.png");
    private final ResourceLocation CRUST_BASE = CookIt.idOf("textures/entity/pizza/pizza_crust.png");

    public PizzaEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart pizzaRoot = ctx.bakeLayer(CookItEntityModelLayers.PIZZA);
        this.pizzaBaseParts = List.of(pizzaRoot.getChild("pizza_slices_1"), pizzaRoot.getChild("pizza_slices_2"), pizzaRoot.getChild("pizza_slices_3"), pizzaRoot.getChild("pizza_full"));

        ModelPart toppingRoot = ctx.bakeLayer(CookItEntityModelLayers.PIZZA_TOPPING);
        this.toppingLayerParts = List.of(toppingRoot.getChild("topping_slices_1"), toppingRoot.getChild("topping_slices_2"), toppingRoot.getChild("topping_slices_3"), toppingRoot.getChild("topping_full"));
    }

    public static LayerDefinition getBaseModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        root.addOrReplaceChild("pizza_full", CubeListBuilder.create()
                .texOffs(0, 0).mirror().addBox(-7.0f, 7.0f, -7.0f, 14.0f, 1.0f, 14.0f).mirror(false)
                .texOffs(0, 28).addBox(-7.0f, 6.0f, -6.0f, 1.0f, 2.0f, 12.0f)
                .texOffs(26, 28).addBox(6.0f, 6.0f, -6.0f, 1.0f, 2.0f, 12.0f)
                .texOffs(0, 42).addBox(-7.0f, 6.0f, -7.0f, 14.0f, 2.0f, 1.0f)
                .texOffs(30, 42).addBox(-7.0f, 6.0f, 6.0f, 14.0f, 2.0f, 1.0f), PartPose.offset(0.0f, 16.0f, 0.0f));

        root.addOrReplaceChild("pizza_slices_3", CubeListBuilder.create().texOffs(0, 28).addBox(-7.0f, 6.0f, -6.0f, 1.0f, 2.0f, 12.0f)
                .texOffs(32, 34).addBox(6.0f, 6.0f, -6.0f, 1.0f, 2.0f, 6.0f)
                .texOffs(7, 48).mirror().addBox(-7.0f, 7.0f, -7.0f, 14.0f, 1.0f, 7.0f).mirror(false)
                .texOffs(7, 17).mirror().addBox(-7.0f, 7.0f, 0.0f, 7.0f, 1.0f, 7.0f).mirror(false)
                .texOffs(0, 42).addBox(-7.0f, 6.0f, -7.0f, 14.0f, 2.0f, 1.0f)
                .texOffs(44, 42).addBox(-7.0f, 6.0f, 6.0f, 7.0f, 2.0f, 1.0f), PartPose.offset(0.0f, 16.0f, 0.0f));

        root.addOrReplaceChild("pizza_slices_2", CubeListBuilder.create().texOffs(6, 34).addBox(-7.0f, 6.0f, -6.0f, 1.0f, 2.0f, 6.0f)
                .texOffs(32, 34).addBox(6.0f, 6.0f, -6.0f, 1.0f, 2.0f, 6.0f)
                .texOffs(7, 48).mirror().addBox(-7.0f, 7.0f, -7.0f, 14.0f, 1.0f, 7.0f).mirror(false)
                .texOffs(0, 42).addBox(-7.0f, 6.0f, -7.0f, 14.0f, 2.0f, 1.0f), PartPose.offset(0.0f, 16.0f, 0.0f));

        root.addOrReplaceChild("pizza_slices_1", CubeListBuilder.create().texOffs(32, 34).addBox(6.0f, 6.0f, -6.0f, 1.0f, 2.0f, 6.0f)
                .texOffs(22, 18).mirror().addBox(0.0f, 7.0f, -7.0f, 7.0f, 1.0f, 7.0f).mirror(false)
                .texOffs(7, 42).addBox(0.0f, 6.0f, -7.0f, 7.0f, 2.0f, 1.0f), PartPose.offset(0.0f, 16.0f, 0.0f));

        return LayerDefinition.create(modelData, 64, 64);
    }

    public static LayerDefinition getToppingModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        root.addOrReplaceChild("topping_full", CubeListBuilder.create().texOffs(-14, 0).mirror().addBox(-7.0f, -1.001f, -7.0f, 14.0f, 0.0f, 14.0f).mirror(false), PartPose.offset(0.0f, 24.0f, 0.0f));

        root.addOrReplaceChild("topping_slices_3", CubeListBuilder.create().texOffs(-7, 7).mirror().addBox(-7.0f, -1.001f, -7.0f, 14.0f, 0.0f, 7.0f).mirror(false)
                .texOffs(1, 1).mirror().addBox(-7.0f, -1.001f, 0.0f, 7.0f, 0.0f, 6.0f).mirror(false), PartPose.offset(0.0f, 24.0f, 0.0f));

        root.addOrReplaceChild("topping_slices_2", CubeListBuilder.create().texOffs(-7, 7).mirror().addBox(-7.0f, -1.001f, -7.0f, 14.0f, 0.0f, 7.0f).mirror(false), PartPose.offset(0.0f, 24.0f, 0.0f));

        root.addOrReplaceChild("topping_slices_1", CubeListBuilder.create().texOffs(-7, 7).mirror().addBox(0.0f, -1.001f, -7.0f, 7.0f, 0.1f, 7.0f).mirror(false), PartPose.offset(0.0f, 24.0f, 0.0f));

        return LayerDefinition.create(modelData, 32, 16);
    }

    @Override
    public void render(PizzaEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ResourceLocation baseTexture = entity.isCooked() ? CHEESE_BASE : CRUST_BASE;
        int sliceCount = Math.max(0, entity.getSliceCount() - 1);
        VertexConsumer baseConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(baseTexture));

        matrices.pushPose();
        matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
        matrices.translate(-0.5, -1.5, 0.5);

        pizzaBaseParts.get(sliceCount).render(matrices, baseConsumer, light, overlay);
        List<PizzaTopping> toppings = PizzaTopping.getToppings(entity.getToppings());

        for (PizzaTopping topping : toppings) {
            VertexConsumer toppingConsumer = vertexConsumers.getBuffer(RenderType.entityNoOutline(topping.getTexture()));
            toppingLayerParts.get(sliceCount).render(matrices, toppingConsumer, light, overlay);
        }

        matrices.popPose();
    }
}