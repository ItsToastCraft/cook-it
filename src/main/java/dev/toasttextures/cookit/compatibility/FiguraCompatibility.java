package dev.toasttextures.cookit.compatibility;

import dev.toasttextures.cookit.CookItClient;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.vanilla_model.VanillaPart;
import org.figuramc.figura.utils.RenderUtils;

public class FiguraCompatibility {
    public static boolean renderArmorPart(Player player, EquipmentSlot slot) {
        if (CookItClient.isFiguraLoaded) {
            VanillaPart part = RenderUtils.partFromSlot(AvatarManager.getAvatar(player), slot);
            return part == null || part.checkVisible();
        }
        return true;
    }
}