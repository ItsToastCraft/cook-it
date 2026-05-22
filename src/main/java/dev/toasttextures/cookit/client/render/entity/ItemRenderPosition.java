package dev.toasttextures.cookit.client.render.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

import java.util.ArrayList;

public record ItemRenderPosition(float x, float y, float z, float extra) {
    public static ArrayList<ItemRenderPosition> createCache(Int2ObjectFunction<ItemRenderPosition> mappingFunction, int size) {
        ArrayList<ItemRenderPosition> pos = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            pos.add(i, mappingFunction.apply(i));
        }

        return pos;
    }
}