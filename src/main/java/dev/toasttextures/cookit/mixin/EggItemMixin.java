package dev.toasttextures.cookit.mixin;

import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EggItem.class)
public class EggItemMixin extends Item {
    public EggItemMixin(Properties settings) {
        super(settings);
    }
    @Inject(method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", at = @At("HEAD"), cancellable = true)
    private void injectMethod(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        HitResult cast = user.pick(3,1, false);

        if (cast.getType().equals(HitResult.Type.BLOCK) && world.getBlockState(BlockPos.containing(cast.getLocation())).getBlock().equals(CookItBlocks.MIXING_BOWL)) {
            cir.setReturnValue(InteractionResultHolder.fail(user.getItemInHand(hand)));
            cir.cancel();
        }
    }
}
