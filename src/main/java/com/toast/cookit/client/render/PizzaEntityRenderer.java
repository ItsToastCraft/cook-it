package com.toast.cookit.client.render;

import com.toast.cookit.CookIt;
import com.toast.cookit.block.entity.PizzaEntity;
import com.toast.cookit.block.food_blocks.pizza.PizzaToppings;
import com.toast.cookit.client.CookItEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class PizzaEntityRenderer implements BlockEntityRenderer<PizzaEntity> {
    Map<PizzaToppings, Identifier> toppingToIdentifier = Arrays.stream(PizzaToppings.values()).collect(Collectors.toMap(Function.identity(), topping -> new Identifier(CookIt.MOD_ID, "textures/entity/pizza/topping/"+topping.asString() + ".png")));
    private final List<ModelPart> pizzaBaseParts;
    private final List<ModelPart> toppingLayerParts;

    public PizzaEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart pizzaRoot = ctx.getLayerModelPart(CookItEntityModelLayers.PIZZA);
        this.pizzaBaseParts = List.of(pizzaRoot.getChild("pizza_slices_1"), pizzaRoot.getChild("pizza_slices_2"), pizzaRoot.getChild("pizza_slices_3"), pizzaRoot.getChild("pizza_full"));

        ModelPart toppingRoot = ctx.getLayerModelPart(CookItEntityModelLayers.PIZZA_TOPPING);
        this.toppingLayerParts = List.of(toppingRoot.getChild("topping_slices_1"), toppingRoot.getChild("topping_slices_2"), toppingRoot.getChild("topping_slices_3"), toppingRoot.getChild("topping_full"));
    }

    public static TexturedModelData getBaseModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("pizza_full", ModelPartBuilder.create()
                .uv(0, 0).mirrored().cuboid(-7.0F, 7.0F, -7.0F, 14.0F, 1.0F, 14.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 28).cuboid(-7.0F, 6.0F, -6.0F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F))
                .uv(26, 28).cuboid(6.0F, 6.0F, -6.0F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 42).cuboid(-7.0F, 6.0F, -7.0F, 14.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 42).cuboid(-7.0F, 6.0F, 6.0F, 14.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        modelPartData.addChild("pizza_slices_3", ModelPartBuilder.create().uv(0, 28).cuboid(-7.0F, 6.0F, -6.0F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F))
                .uv(32, 34).cuboid(6.0F, 6.0F, -6.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(7, 48).mirrored().cuboid(-7.0F, 7.0F, -7.0F, 14.0F, 1.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
                .uv(7, 17).mirrored().cuboid(-7.0F, 7.0F, 0.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 42).cuboid(-7.0F, 6.0F, -7.0F, 14.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 42).cuboid(-7.0F, 6.0F, 6.0F, 7.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        modelPartData.addChild("pizza_slices_2", ModelPartBuilder.create().uv(6, 34).cuboid(-7.0F, 6.0F, -6.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(32, 34).cuboid(6.0F, 6.0F, -6.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(7, 48).mirrored().cuboid(-7.0F, 7.0F, -7.0F, 14.0F, 1.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 42).cuboid(-7.0F, 6.0F, -7.0F, 14.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        modelPartData.addChild("pizza_slices_1", ModelPartBuilder.create().uv(32, 34).cuboid(6.0F, 6.0F, -6.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(22, 18).mirrored().cuboid(0.0F, 7.0F, -7.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
                .uv(7, 42).cuboid(0.0F, 6.0F, -7.0F, 7.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    public static TexturedModelData getToppingModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("topping_full", ModelPartBuilder.create().uv(-14, 0).mirrored().cuboid(-7.0F, -1.001F, -7.0F, 14.0F, 0.0F, 14.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        modelPartData.addChild("topping_slices_3", ModelPartBuilder.create().uv(-7, 7).mirrored().cuboid(-7.0F, -1.001F, -7.0F, 14.0F, 0.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
                .uv(1, 1).mirrored().cuboid(-7.0F, -1.001F, 0.0F, 7.0F, 0.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        modelPartData.addChild("topping_slices_2", ModelPartBuilder.create().uv(-7, 7).mirrored().cuboid(-7.0F, -1.001F, -7.0F, 14.0F, 0.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        modelPartData.addChild("topping_slices_1", ModelPartBuilder.create().uv(-7, 7).mirrored().cuboid(0.0F, -1.001F, -7.0F, 7.0F, 0.1F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        return TexturedModelData.of(modelData, 32, 16);
    }

    @Override
    public void render(PizzaEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Identifier baseTexture = entity.isCooked() ? Identifier.of(CookIt.MOD_ID, "textures/entity/pizza/pizza_cheese.png") : Identifier.of(CookIt.MOD_ID, "textures/entity/pizza/pizza_crust.png");

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrices.translate(-0.5, -1.5, 0.5);

        // 0 indexed aah
        int sliceCount = Math.max(0, entity.getSliceCount() - 1);

        VertexConsumer baseConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(baseTexture));
        pizzaBaseParts.get(sliceCount).render(matrices, baseConsumer, light, overlay);
        NbtList toppings = entity.getToppings();
        if (!toppings.isEmpty()) {
            // loop over all the toppings
            for (int i = 0; i < toppings.size(); i++) {
                String string = toppings.getString(i);
                // check for a valid topping and a valid topping texture
                if (string != null && PizzaToppings.fromName(string) != null) {
                    PizzaToppings topping = PizzaToppings.fromName(string);
                    Identifier toppingTexture = toppingToIdentifier.get(topping);
                    if (toppingTexture != null) {
                        // render the topping layer with the current topping's texture
                        VertexConsumer toppingConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityNoOutline(toppingTexture));
                        toppingLayerParts.get(sliceCount).render(matrices, toppingConsumer, light, overlay);
                    }
                }
            }
        }
        matrices.pop();
    }
}
