package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.block.appliances.Toaster;
import net.minecraft.block.Block;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.CookIt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class CookItModels {
    public static final TextureKey PLATE_KEY = TextureKey.of("plate");
    public static final TextureKey BOWL_KEY = TextureKey.of("bowl");
    public static final TextureKey CUTTING_BOARD_KEY = TextureKey.of("cutting_board");
    public static final TextureKey ROLLING_PIN_KEY = TextureKey.of("rolling_pin");
    public static final TextureKey VINE_KEY = TextureKey.of("vine");
    public static final TextureKey DECOR_KEY = TextureKey.of("decor");

    public static final Model PLANE = newParent("block/plane", TextureKey.TEXTURE);
    public static final Model BOWL_TEMPLATE = newParent("block/bowl", BOWL_KEY);
    public static final Model CUTTING_BOARD_TEMPLATE = newParent("block/cutting_board", CUTTING_BOARD_KEY);
    public static final Model ROLLING_PIN_TEMPLATE = newParent("item/rolling_pin", ROLLING_PIN_KEY);
    public static final Model VANILLA_VINE_TEMPLATE = newParent("block/blooming_vine", VINE_KEY, DECOR_KEY);

    public static final List<Model> PLATE_TEMPLATE = IntStream.rangeClosed(1, 4)
            .mapToObj(i -> newParent("block/plate_" + i, PLATE_KEY)).toList();

    public static final List<Model> LARGE_PLATE_TEMPLATE = IntStream.rangeClosed(1, 4)
            .mapToObj(i -> newParent("block/large_plate_" + i, PLATE_KEY)).toList();

    static Model newParent(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(CookIt.idOf(parent)), Optional.empty(), requiredTextureKeys);
    }

    public static TextureMap coloredTextureMap(TextureKey type, Block block, String folder) {
        return TextureMap.of(type, setTextureOutput(block, "colored/" + folder + "/" + Registries.BLOCK.getId(block).getPath()));
    }

    public static Identifier setTextureOutput(Block block, String path) {
        return Registries.BLOCK.getId(block).withPath("block/" + path);
    }
    public static Identifier setModelOutput(Block block, String path) {
        return CookIt.idOf(path + Registries.BLOCK.getId(block).getPath());
    }

    public static Identifier setModelOutput(Block block, String path, String suffix) {
        return CookIt.idOf(path + Registries.BLOCK.getId(block).getPath() + suffix);
    }
}
