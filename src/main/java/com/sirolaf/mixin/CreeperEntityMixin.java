package com.sirolaf.mixin;

import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin {
    @Inject(method = "explode", at = @At("HEAD"))
    private void explodeMixin(CallbackInfo ci) {
        var t = (CreeperEntity)(Object)(this);
        for (var instance : t.getStatusEffects()) {
            if (instance.getDuration() < 0) {
                continue;
            }
            t.removeStatusEffect(instance.getEffectType());
        }
    }
}
