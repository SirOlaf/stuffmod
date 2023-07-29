package com.sirolaf.mixin;

import com.sirolaf.util.IStuffModEntityData;
import com.sirolaf.util.StuffModEntityData;
import com.sirolaf.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "isClimbing", at = @At("RETURN"), cancellable = true)
    private void isClimbingMixin(CallbackInfoReturnable<Boolean> cir) {
        var t = (LivingEntity)(Object)this;

        if (t instanceof HostileEntity) {
            BlockPos blockPos = t.getBlockPos();
            if (t.horizontalCollision) {
                t.climbingPos = Optional.of(blockPos);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "tickItemStackUsage", at = @At("TAIL"))
    private void injectTickItemStackUsage(ItemStack stack, CallbackInfo ci) {
        var t = (LivingEntity)(Object)this;
        if (t instanceof AbstractSkeletonEntity) {
            if (StuffModEntityData.hasSpawnPos((IStuffModEntityData) this)) {
                var spawnPos = StuffModEntityData.getSpawnPos((IStuffModEntityData) this);
                var difficultyMultiplier = Utils.calcDifficultyMultiplier(t.world, spawnPos);
                if (difficultyMultiplier > 1.0) {
                    t.itemUseTimeLeft -= (int)difficultyMultiplier;
                }
            }
        }
    }
}
