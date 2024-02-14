package toast.cook_it;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import toast.cook_it.registries.CookItBlockEntities;
import toast.cook_it.registries.CookItItems;

@Environment(EnvType.CLIENT)
public class CookItClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Here we will put client-only registration code
        CookItBlockEntities.registerRenderers();

        ModelPredicateProviderRegistry.register(CookItItems.FIRE_EXTINGUISHER, new Identifier("extinguisher_fuel"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return (float) (stack.getDamage() / 100);
            }
        });
    }
}