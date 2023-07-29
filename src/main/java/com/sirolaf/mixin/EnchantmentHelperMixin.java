package com.sirolaf.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getDepthStrider", at = @At("RETURN"), cancellable = true)
    private static void getDepthStriderMixin(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof HostileEntity) {
            var world = entity.getWorld();
            var spawnPos = new BlockPos(world.getLevelProperties().getSpawnX(), world.getLevelProperties().getSpawnY(), world.getLevelProperties().getSpawnZ());
            var realSpawnDist = Math.sqrt(spawnPos.getSquaredDistance(entity.getPos()));
            var spawnDistance = realSpawnDist / 10.f + 2.0f;
            var spawnDistLog = Math.log(spawnDistance);
            cir.setReturnValue((int)spawnDistLog); // TODO: Map from 0 to 3 in ints
        }
    }
}
