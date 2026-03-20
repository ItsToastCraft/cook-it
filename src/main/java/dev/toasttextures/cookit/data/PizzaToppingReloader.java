package dev.toasttextures.cookit.data;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.food_blocks.pizza.PizzaTopping;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;

public class PizzaToppingReloader implements SimpleSynchronousResourceReloadListener {
    private static final Identifier id = CookIt.idOf("toppings");
    private static final Codec<PizzaTopping> codec = RecordCodecBuilder.create(instance -> instance.group(Identifier.CODEC.fieldOf("id").forGetter(PizzaTopping::getId), Registries.ITEM.getCodec().fieldOf("item").forGetter(PizzaTopping::getItem), Identifier.CODEC.fieldOf("texture").forGetter(PizzaTopping::getTexture)).apply(instance, PizzaTopping::register));

    @Override
    public Identifier getFabricId() {
        return id;
    }

    @Override
    public void reload(ResourceManager manager) {
        PizzaTopping.REGISTRY.clear();

        for (Resource resource : manager.findResources("toppings", (id -> id.getPath().endsWith(".json"))).values()) {
            try (BufferedReader reader = resource.getReader()) {
                codec.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader).getAsJsonObject());
            } catch (IOException e) {
                return;
            }
        }
    }
}