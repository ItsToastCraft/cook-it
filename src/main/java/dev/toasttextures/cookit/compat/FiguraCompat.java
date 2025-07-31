package dev.toasttextures.cookit.compat;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.utils.RenderUtils;

public class FiguraCompat {
    public static boolean renderArmorPart(PlayerEntity player, EquipmentSlot slot) {
        Avatar avatar = AvatarManager.getAvatar(player);
        if (RenderUtils.vanillaModelAndScript(avatar)) {
            return RenderUtils.partFromSlot(avatar, slot).checkVisible();
        }
        return true;
    }
}
