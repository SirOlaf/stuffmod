package com.sirolaf.mixin;

import com.sirolaf.util.IStuffModEntityData;
import com.sirolaf.util.StuffModEntityData;
import com.sirolaf.util.Utils;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin extends MobEntityMixin {
    @Inject(method = "updateAttackType", at = @At("TAIL"))
    private void injectUpdateAttackType(CallbackInfo cbi) {
        var t = (AbstractSkeletonEntity)(Object)this;
        if (StuffModEntityData.hasSpawnPos((IStuffModEntityData)this)) {
            var spawnPos = StuffModEntityData.getSpawnPos((IStuffModEntityData)this);
            var difficultyMultiplier = Utils.calcDifficultyMultiplier(t.world, spawnPos);
            adjustSkeletonBowInterval(t, difficultyMultiplier);
        }
    }
}
