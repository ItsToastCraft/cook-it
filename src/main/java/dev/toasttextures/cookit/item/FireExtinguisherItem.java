package dev.toasttextures.cookit.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.minecraft.state.property.Properties.LIT;

public class FireExtinguisherItem extends Item {
    public static final double OFFSET_DISTANCE = 4.0;

    public FireExtinguisherItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.CONSUME;
        PlayerEntity user = context.getPlayer();
        if (user == null) return ActionResult.CONSUME;

        Vec3d hitPos = context.getHitPos();
        addExtinguishEffects(hitPos, user, (ServerWorld) world);

        context.getStack().damage(1, user, playerEntity -> user.sendToolBreakStatus(user.getActiveHand()));

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity.getWorld().isClient) return ActionResult.SUCCESS;
        addExtinguishEffects(entity.getPos(), user, (ServerWorld) entity.getWorld());

        if (entity.isOnFire()) {
            entity.extinguish();
            entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }
        return ActionResult.SUCCESS;
    }

    private void addExtinguishEffects(Vec3d origin, PlayerEntity player, ServerWorld world) {
        world.playSound(null, origin.x, origin.y, origin.z, SoundEvents.WEATHER_RAIN, SoundCategory.PLAYERS, 0.5f, 1.0f);

        Vec3d pos = player.getPos();
        double pitch = Math.toRadians(player.getPitch());
        double yaw = Math.toRadians(player.getYaw());
        Vec3d direction = new Vec3d(-Math.sin(yaw) * Math.cos(pitch) * OFFSET_DISTANCE, -Math.sin(pitch) * OFFSET_DISTANCE, Math.cos(yaw) * Math.cos(pitch) * OFFSET_DISTANCE);

        Vec3d hitOffset = origin.subtract(pos.x, pos.y + 1.0, pos.z);
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int i = 0; i < 200; i++) {
            double div = i / 128.0;
            double particleX = pos.x + (hitOffset.x * div) + direction.x + (world.random.nextDouble() * 4) - 2;
            double particleY = pos.y + (hitOffset.y * div) + direction.y + (world.random.nextDouble() * 2) + 1.5;
            double particleZ = pos.z + (hitOffset.z * div) + direction.z + (world.random.nextDouble() * 4) - 2;

            mutablePos.set(particleX, particleY, particleZ);

            world.spawnParticles(ParticleTypes.SPIT, particleX, particleY, particleZ, 1, 0.0, 0.124, 0.0, 0.0);

            if (world.random.nextFloat() < 0.5f) {
                extinguishFire(player, mutablePos, world);
            }
        }
    }

    private void extinguishFire(PlayerEntity player, BlockPos pos, ServerWorld world) {
        BlockState state = world.getBlockState(pos);
        if (state.isIn(BlockTags.FIRE)) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 1f);
            world.breakBlock(pos, false, null);
        } else if (CampfireBlock.isLitCampfire(state)) {
            CampfireBlock.extinguish(player, world, pos, state.with(LIT, false));
        }
    }
}