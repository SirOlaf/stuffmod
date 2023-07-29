package com.sirolaf.mixin;

import com.sirolaf.util.IStuffModEntityData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class StuffModEntityDataMixin implements IStuffModEntityData {
    @Unique
    private NbtCompound persistentData;

    @Override
    public NbtCompound getPersistentData() {
        if (persistentData == null) {
            persistentData = new NbtCompound();
        }
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void injectWriteNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if (persistentData != null) {
            nbt.put("stuffmod.entity_data", persistentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void injectReadNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("stuffmod.entity_data", NbtElement.COMPOUND_TYPE)) {
            persistentData = nbt.getCompound("stuffmod.entity_data");
        }
    }

}
