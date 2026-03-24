package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.entity.PizzaEntity;
import dev.toasttextures.cookit.block.food.pizza.PizzaTopping;
import dev.toasttextures.cookit.client.CookItEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

@Environment(EnvType.CLIENT)
public class PizzaEntityRenderer implements BlockEntityRenderer<PizzaEntity> {
    private final List<ModelPart> pizzaBaseParts;
    private final List<ModelPart> toppingLayerParts;

    private final Identifier CHEESE_BASE = CookIt.idOf("textures/entity/pizza/pizza_cheese.png");
    private final Identifier CRUST_BASE = CookIt.idOf("textures/entity/pizza/pizza_crust.png");

    public PizzaEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart pizzaRoot = ctx.getLayerModelPart(CookItEntityModelLayers.PIZZA);
        this.pizzaBaseParts = List.of(pizzaRoot.getChild("pizza_slices_1"), pizzaRoot.getChild("pizza_slices_2"), pizzaRoot.getChild("pizza_slices_3"), pizzaRoot.getChild("pizza_full"));

        ModelPart toppingRoot = ctx.getLayerModelPart(CookItEntityModelLayers.PIZZA_TOPPING);
        this.toppingLayerParts = List.of(toppingRoot.getChild("topping_slices_1"), toppingRoot.getChild("topping_slices_2"), toppingRoot.getChild("topping_slices_3"), toppingRoot.getChild("topping_full"));
    }

    public static TexturedModelData getBaseModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("pizza_full", ModelPartBuilder.create()
                .uv(0, 0).mirrored().cuboid(-7.0f, 7.0f, -7.0f, 14.0f, 1.0f, 14.0f, new Dilation(0.0f)).mirrored(false)
                .uv(0, 28).cuboid(-7.0f, 6.0f, -6.0f, 1.0f, 2.0f, 12.0f, new Dilation(0.0f))
                .uv(26, 28).cuboid(6.0f, 6.0f, -6.0f, 1.0f, 2.0f, 12.0f, new Dilation(0.0f))
                .uv(0, 42).cuboid(-7.0f, 6.0f, -7.0f, 14.0f, 2.0f, 1.0f, new Dilation(0.0f))
                .uv(30, 42).cuboid(-7.0f, 6.0f, 6.0f, 14.0f, 2.0f, 1.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, 16.0f, 0.0f));

        root.addChild("pizza_slices_3", ModelPartBuilder.create().uv(0, 28).cuboid(-7.0f, 6.0f, -6.0f, 1.0f, 2.0f, 12.0f, new Dilation(0.0f))
                .uv(32, 34).cuboid(6.0f, 6.0f, -6.0f, 1.0f, 2.0f, 6.0f, new Dilation(0.0f))
                .uv(7, 48).mirrored().cuboid(-7.0f, 7.0f, -7.0f, 14.0f, 1.0f, 7.0f, new Dilation(0.0f)).mirrored(false)
                .uv(7, 17).mirrored().cuboid(-7.0f, 7.0f, 0.0f, 7.0f, 1.0f, 7.0f, new Dilation(0.0f)).mirrored(false)
                .uv(0, 42).cuboid(-7.0f, 6.0f, -7.0f, 14.0f, 2.0f, 1.0f, new Dilation(0.0f))
                .uv(44, 42).cuboid(-7.0f, 6.0f, 6.0f, 7.0f, 2.0f, 1.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, 16.0f, 0.0f));

        root.addChild("pizza_slices_2", ModelPartBuilder.create().uv(6, 34).cuboid(-7.0f, 6.0f, -6.0f, 1.0f, 2.0f, 6.0f, new Dilation(0.0f))
                .uv(32, 34).cuboid(6.0f, 6.0f, -6.0f, 1.0f, 2.0f, 6.0f, new Dilation(0.0f))
                .uv(7, 48).mirrored().cuboid(-7.0f, 7.0f, -7.0f, 14.0f, 1.0f, 7.0f, new Dilation(0.0f)).mirrored(false)
                .uv(0, 42).cuboid(-7.0f, 6.0f, -7.0f, 14.0f, 2.0f, 1.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, 16.0f, 0.0f));

        root.addChild("pizza_slices_1", ModelPartBuilder.create().uv(32, 34).cuboid(6.0f, 6.0f, -6.0f, 1.0f, 2.0f, 6.0f, new Dilation(0.0f))
                .uv(22, 18).mirrored().cuboid(0.0f, 7.0f, -7.0f, 7.0f, 1.0f, 7.0f, new Dilation(0.0f)).mirrored(false)
                .uv(7, 42).cuboid(0.0f, 6.0f, -7.0f, 7.0f, 2.0f, 1.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, 16.0f, 0.0f));

        return TexturedModelData.of(modelData, 64, 64);
    }

    public static TexturedModelData getToppingModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("topping_full", ModelPartBuilder.create().uv(-14, 0).mirrored().cuboid(-7.0f, -1.001f, -7.0f, 14.0f, 0.0f, 14.0f, new Dilation(0.0f)).mirrored(false), ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        root.addChild("topping_slices_3", ModelPartBuilder.create().uv(-7, 7).mirrored().cuboid(-7.0f, -1.001f, -7.0f, 14.0f, 0.0f, 7.0f, new Dilation(0.0f)).mirrored(false)
                .uv(1, 1).mirrored().cuboid(-7.0f, -1.001f, 0.0f, 7.0f, 0.0f, 6.0f, new Dilation(0.0f)).mirrored(false), ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        root.addChild("topping_slices_2", ModelPartBuilder.create().uv(-7, 7).mirrored().cuboid(-7.0f, -1.001f, -7.0f, 14.0f, 0.0f, 7.0f, new Dilation(0.0f)).mirrored(false), ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        root.addChild("topping_slices_1", ModelPartBuilder.create().uv(-7, 7).mirrored().cuboid(0.0f, -1.001f, -7.0f, 7.0f, 0.1f, 7.0f, new Dilation(0.0f)).mirrored(false), ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        return TexturedModelData.of(modelData, 32, 16);
    }

    @Override
    public void render(PizzaEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Identifier baseTexture = entity.isCooked() ? CHEESE_BASE : CRUST_BASE;

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrices.translate(-0.5, -1.5, 0.5);

        // 0 indexed aah
        int sliceCount = Math.max(0, entity.getSliceCount() - 1);

        VertexConsumer baseConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(baseTexture));
        pizzaBaseParts.get(sliceCount).render(matrices, baseConsumer, light, overlay);
        List<PizzaTopping> toppings = PizzaTopping.getToppings(entity.getToppings());

        for (PizzaTopping topping : toppings) {
            VertexConsumer toppingConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityNoOutline(topping.getTexture()));
            toppingLayerParts.get(sliceCount).render(matrices, toppingConsumer, light, overlay);
        }

        matrices.pop();
    }
}