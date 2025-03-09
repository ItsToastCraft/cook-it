package dev.toasttextures.cookit.mixin;

import dev.toasttextures.cookit.registry.CookItBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EggItem.class)
public class EggItemMixin extends Item {
    public EggItemMixin(Settings settings) {
        super(settings);
    }
    @Inject(method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;", at = @At("HEAD"), cancellable = true)
    private void injectMethod(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        HitResult cast = user.raycast(3,1, false);

        if (cast.getType().equals(HitResult.Type.BLOCK) && world.getBlockState(BlockPos.ofFloored(cast.getPos())).getBlock().equals(CookItBlocks.MIXING_BOWL)) {
            cir.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
            cir.cancel();
        }
    }
}
