package com.sirolaf.mixin;

import com.sirolaf.util.IStuffModEntityData;
import com.sirolaf.util.StuffModEntityData;
import com.sirolaf.util.Utils;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
	@Unique
	protected void adjustSkeletonBowInterval(AbstractSkeletonEntity s, double difficultyMultiplier) {
		s.bowAttackGoal.setAttackInterval(Math.max(0, (int)(30.f - (difficultyMultiplier * 5))));
	}

	@Inject(at = @At("RETURN"), method = "createNavigation", cancellable = true)
	private void createNavigation(World world, CallbackInfoReturnable<EntityNavigation> cir) {
		var navigation = new SpiderNavigation(((MobEntity)(Object)this), world);
		cir.setReturnValue(navigation); // TODO: Make custom navigators
	}

	@Unique
	private void addAttributeMultiplier(String name, EntityAttribute attribute, double difficultyMultiplier) {
		var t = (MobEntity)(Object)this;
		var inst = t.getAttributeInstance(attribute);
		if (inst == null) { return; }
		inst.addPersistentModifier(new EntityAttributeModifier(
				"[StuffMod] " + name + " difficulty multiplier",
				difficultyMultiplier - 1.0, // NOTE: Value calculation adds 1 for MULTIPLY_TOTAL, so we subtract 1
				EntityAttributeModifier.Operation.MULTIPLY_TOTAL
		));
	}

	@Inject(at = @At("TAIL"), method = "initialize")
	private void init(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
		if (world.toServerWorld().getRegistryKey() == World.END) {
			return;
		}

		var t = ((MobEntity)(Object)this);
		if (t instanceof HostileEntity) {
			StuffModEntityData.setSpawnPos((IStuffModEntityData)this, t.getPos());

			t.getNavigation().setCanSwim(true);
			var realSpawnDist = Utils.getSpawnPointDistance(world.toServerWorld(), t.getPos());
			var difficultyMultiplier = Utils.calcDifficultyMultiplier(world.toServerWorld(), t.getPos());

			if (realSpawnDist > 500) {
				if (world.getRandom().nextDouble() > 0.95) {
					t.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, -1));
				}
			}
			t.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, -1, 3));

			this.addAttributeMultiplier("Follow range", EntityAttributes.GENERIC_FOLLOW_RANGE, difficultyMultiplier);
			this.addAttributeMultiplier("Movement speed", EntityAttributes.GENERIC_MOVEMENT_SPEED, difficultyMultiplier);
			this.addAttributeMultiplier("Health", EntityAttributes.GENERIC_MAX_HEALTH, difficultyMultiplier);
			this.addAttributeMultiplier("Damage", EntityAttributes.GENERIC_ATTACK_DAMAGE, difficultyMultiplier);
			this.addAttributeMultiplier("Attack speed", EntityAttributes.GENERIC_ATTACK_SPEED, difficultyMultiplier);

			if (t instanceof CreeperEntity) {
				var c = (CreeperEntity)(Object)this;
				c.fuseTime = Math.max(1, (int)(30.f / (difficultyMultiplier * 2)));
				c.explosionRadius = Math.max(1, (int)((float)c.explosionRadius * (0.1f + difficultyMultiplier)));

				if (world.getRandom().nextDouble() > (0.998 - realSpawnDist / 1000.f)) {
					// TODO: Change probability in a less dumb way
					c.getDataTracker().set(CreeperEntity.CHARGED, true);
				}
			}
			else if (t instanceof AbstractSkeletonEntity) {
				var s = (AbstractSkeletonEntity)(Object)this;
				adjustSkeletonBowInterval(s, difficultyMultiplier);
			}
		}
	}
}