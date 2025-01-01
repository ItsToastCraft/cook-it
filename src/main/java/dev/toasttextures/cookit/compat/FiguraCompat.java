package dev.toasttextures.cookit.compat;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.utils.RenderUtils;

public class FiguraCompat {
    public static boolean renderArmorPart(PlayerEntity player, EquipmentSlot slot) {
        Avatar avatar = AvatarManager.getAvatar(player);
        if (!RenderUtils.vanillaModelAndScript(avatar))
            return true;
        Boolean a = switch (slot) {
            case HEAD -> avatar.luaRuntime.vanilla_model.HELMET.getVisible();
            case CHEST -> avatar.luaRuntime.vanilla_model.CHESTPLATE.getVisible();
            case LEGS -> avatar.luaRuntime.vanilla_model.LEGGINGS.getVisible();
            case FEET -> avatar.luaRuntime.vanilla_model.BOOTS.getVisible();
            default -> true;
        };
        if (a == null) { a = true; }
        return a;
    }
}
