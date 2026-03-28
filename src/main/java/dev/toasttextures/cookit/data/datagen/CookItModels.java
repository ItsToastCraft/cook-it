package dev.toasttextures.cookit.data.datagen;

import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import dev.toasttextures.cookit.CookIt;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class CookItModels {
    public static final TextureSlot PLATE_KEY = TextureSlot.create("plate");
    public static final TextureSlot BOWL_KEY = TextureSlot.create("bowl");
    public static final TextureSlot CUTTING_BOARD_KEY = TextureSlot.create("cutting_board");
    public static final TextureSlot ROLLING_PIN_KEY = TextureSlot.create("rolling_pin");
    public static final TextureSlot VINE_KEY = TextureSlot.create("vine");
    public static final TextureSlot DECOR_KEY = TextureSlot.create("decor");

    public static final ModelTemplate PLANE = newParent("block/plane", TextureSlot.TEXTURE);
    public static final ModelTemplate BOWL_TEMPLATE = newParent("block/bowl", BOWL_KEY);
    public static final ModelTemplate CUTTING_BOARD_TEMPLATE = newParent("block/cutting_board", CUTTING_BOARD_KEY);
    public static final ModelTemplate ROLLING_PIN_TEMPLATE = newParent("item/rolling_pin", ROLLING_PIN_KEY);
    public static final ModelTemplate VANILLA_VINE_TEMPLATE = newParent("block/blooming_vine", VINE_KEY, DECOR_KEY);
    public static final List<ModelTemplate> PLATE_TEMPLATE = IntStream.rangeClosed(1, 4)
            .mapToObj(i -> newParent("block/plate_" + i, PLATE_KEY)).toList();

    public static final List<ModelTemplate> LARGE_PLATE_TEMPLATE = IntStream.rangeClosed(1, 4)
            .mapToObj(i -> newParent("block/large_plate_" + i, PLATE_KEY)).toList();

    static ModelTemplate newParent(String parent, TextureSlot... requiredTextureKeys) {
        return new ModelTemplate(Optional.of(CookIt.idOf(parent)), Optional.empty(), requiredTextureKeys);
    }

    public static TextureMapping coloredTextureMap(TextureSlot type, Block block, String folder) {
        return TextureMapping.singleSlot(type, setTextureOutput(block, "colored/" + folder + "/" + BuiltInRegistries.BLOCK.getKey(block).getPath()));
    }

    public static ResourceLocation setTextureOutput(Block block, String path) {
        return BuiltInRegistries.BLOCK.getKey(block).withPath("block/" + path);
    }
    public static ResourceLocation setModelOutput(Block block, String path) {
        return CookIt.idOf(path + BuiltInRegistries.BLOCK.getKey(block).getPath());
    }

    public static ResourceLocation setModelOutput(Block block, String path, String suffix) {
        return CookIt.idOf(path + BuiltInRegistries.BLOCK.getKey(block).getPath() + suffix);
    }
}