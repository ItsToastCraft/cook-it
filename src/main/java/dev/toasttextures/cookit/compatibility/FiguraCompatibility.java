package dev.toasttextures.cookit.compatibility;

import dev.toasttextures.cookit.CookItClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.vanilla_model.VanillaPart;
import org.figuramc.figura.utils.RenderUtils;

public class FiguraCompatibility {
    public static boolean renderArmorPart(PlayerEntity player, EquipmentSlot slot) {
        if (CookItClient.isFiguraLoaded) {
            VanillaPart part = RenderUtils.partFromSlot(AvatarManager.getAvatar(player), slot);
            return part == null || part.checkVisible();
        }
        return true;
    }
}