package dev.toasttextures.cookit.data;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.food.pizza.PizzaTopping;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;

public class PizzaToppingReloader implements SimpleSynchronousResourceReloadListener {
    private static final ResourceLocation id = CookIt.idOf("toppings");
    private static final Codec<PizzaTopping> codec = RecordCodecBuilder.create(instance -> instance.group(ResourceLocation.CODEC.fieldOf("id").forGetter(PizzaTopping::getId), BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(PizzaTopping::getItem), ResourceLocation.CODEC.fieldOf("texture").forGetter(PizzaTopping::getTexture)).apply(instance, PizzaTopping::register));

    @Override
    public ResourceLocation getFabricId() {
        return id;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        PizzaTopping.REGISTRY.clear();

        for (Resource resource : manager.listResources("toppings", (id -> id.getPath().endsWith(".json"))).values()) {
            try (BufferedReader reader = resource.openAsReader()) {
                codec.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader).getAsJsonObject());
            } catch (IOException e) {
                break;
            }
        }
    }
}