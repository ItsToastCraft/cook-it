package dev.toasttextures.cookit.item;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class FireExtinguisherItem extends Item {
    public static final double OFFSET_DISTANCE = 4.0;

    public FireExtinguisherItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) return InteractionResult.CONSUME;
        Player user = context.getPlayer();
        if (user == null) return InteractionResult.CONSUME;

        Vec3 hitPos = context.getClickLocation();
        addExtinguishEffects(hitPos, user, (ServerLevel) world);

        context.getItemInHand().hurtAndBreak(1, user, playerEntity -> user.broadcastBreakEvent(user.getUsedItemHand()));

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        Level world = entity.level();
        if (world.isClientSide) return InteractionResult.SUCCESS;
        addExtinguishEffects(entity.position(), user, (ServerLevel) world);

        if (entity.isOnFire()) {
            entity.clearFire();
            world.playSound(null, entity.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.0f);
        }

        return InteractionResult.SUCCESS;
    }

    private void addExtinguishEffects(Vec3 origin, Player player, ServerLevel world) {
        world.playSound(null, origin.x, origin.y, origin.z, SoundEvents.WEATHER_RAIN, SoundSource.PLAYERS, 0.5f, 1.0f);

        Vec3 pos = player.position();
        double pitch = Math.toRadians(player.getXRot());
        double yaw = Math.toRadians(player.getYRot());
        Vec3 direction = new Vec3(-Math.sin(yaw) * Math.cos(pitch) * OFFSET_DISTANCE, -Math.sin(pitch) * OFFSET_DISTANCE, Math.cos(yaw) * Math.cos(pitch) * OFFSET_DISTANCE);

        Vec3 hitOffset = origin.subtract(pos.x, pos.y + 1.0, pos.z);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 200; i++) {
            double div = i / 128.0;
            double particleX = pos.x + (hitOffset.x * div) + direction.x + (world.random.nextDouble() * 4) - 2;
            double particleY = pos.y + (hitOffset.y * div) + direction.y + (world.random.nextDouble() * 2) + 1.5;
            double particleZ = pos.z + (hitOffset.z * div) + direction.z + (world.random.nextDouble() * 4) - 2;

            mutablePos.set(particleX, particleY, particleZ);

            world.sendParticles(ParticleTypes.SPIT, particleX, particleY, particleZ, 1, 0.0, 0.124, 0.0, 0.0);

            if (world.random.nextFloat() < 0.5f) {
                extinguishFire(player, mutablePos, world);
            }
        }
    }

    private void extinguishFire(Player player, BlockPos pos, ServerLevel world) {
        BlockState state = world.getBlockState(pos);
        if (state.is(BlockTags.FIRE)) {
            world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1f);
            world.destroyBlock(pos, false, null);
        } else if (CampfireBlock.isLitCampfire(state)) {
            CampfireBlock.dowse(player, world, pos, state.setValue(LIT, false));
        }
    }
}