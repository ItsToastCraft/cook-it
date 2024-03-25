package toast.cook_it.registries;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;
import toast.cook_it.recipes.MicrowaveRecipe;

public class CookItRecipes {

    public static void registerRecipes() {
       String ID = "microwaving";
       Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(CookIt.MOD_ID, ID),
                MicrowaveRecipe.Serializer.INSTANCE);

        Registry.register(Registries.RECIPE_TYPE, new Identifier(CookIt.MOD_ID, ID),
                MicrowaveRecipe.Type.INSTANCE);

    }
}