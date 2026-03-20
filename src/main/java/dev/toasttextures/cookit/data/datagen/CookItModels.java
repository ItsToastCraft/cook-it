package dev.toasttextures.cookit.data.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.CookIt;

import java.util.Optional;

public class CookItModels {
    static TextureKey PLATE = TextureKey.of("plate");
    static TextureKey BOWL = TextureKey.of("bowl");
    static TextureKey CUTTING_BOARD = TextureKey.of("cutting_board");
    static TextureKey ROLLING_PIN = TextureKey.of("rolling_pin");
    static TextureKey VINE = TextureKey.of("vine");
    static TextureKey DECOR = TextureKey.of("decor");
    public static final Model PLANE = newParent("block/plane", TextureKey.TEXTURE);
    public static final Model TEMPLATE_PLATE_1 = newParent("block/plate_1", PLATE);
    public static final Model TEMPLATE_PLATE_2 = newParent("block/plate_2", PLATE);
    public static final Model TEMPLATE_PLATE_3 = newParent("block/plate_3", PLATE);
    public static final Model TEMPLATE_PLATE_4 = newParent("block/plate_4", PLATE);
    public static final Model TEMPLATE_LARGE_PLATE_1 = newParent("block/large_plate_1", PLATE);
    public static final Model TEMPLATE_LARGE_PLATE_2 = newParent("block/large_plate_2", PLATE);
    public static final Model TEMPLATE_LARGE_PLATE_3 = newParent("block/large_plate_3", PLATE);
    public static final Model TEMPLATE_LARGE_PLATE_4 = newParent("block/large_plate_4", PLATE);
    public static final Model TEMPLATE_BOWL = newParent("block/bowl", BOWL);
    public static final Model TEMPLATE_CUTTING_BOARD = newParent("block/cutting_board", CUTTING_BOARD);
    public static final Model TEMPLATE_ROLLING_PIN = newParent("item/rolling_pin", ROLLING_PIN);

    static Model newParent(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(new Identifier(CookIt.MOD_ID, parent)), Optional.empty(), requiredTextureKeys);
    }

    public static TextureMap coloredTextureMap(TextureKey type, Block block, String folder) {
        return TextureMap.of(type, setTextureOutput(block, "colored/" + folder + "/" + Registries.BLOCK.getId(block).getPath()));
    }

    public static Identifier setTextureOutput(Block block, String path) {
        Identifier identifier = Registries.BLOCK.getId(block);
        return identifier.withPath("block/" + path);
    }
    public static Identifier setModelOutput(String path, Block block) {
        return new Identifier(CookIt.MOD_ID, path + Registries.BLOCK.getId(block).getPath());
    }

    public static Identifier setModelOutput(String path, Block block, String suffix) {
        return new Identifier(CookIt.MOD_ID, path + Registries.BLOCK.getId(block).getPath() + suffix);
    }
}
