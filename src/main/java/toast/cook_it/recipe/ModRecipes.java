package toast.cook_it.recipe;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;

public class ModRecipes {

    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(CookIt.MOD_ID, MicrowaveRecipe.Serializer.ID),
                MicrowaveRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(CookIt.MOD_ID, MicrowaveRecipe.Type.ID),
                MicrowaveRecipe.Type.INSTANCE);
    }
}
