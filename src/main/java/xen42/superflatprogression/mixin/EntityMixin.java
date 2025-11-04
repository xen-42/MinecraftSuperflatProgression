package xen42.superflatprogression.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
	@Inject(at = @At("HEAD"), method = "onStruckByLightning", cancellable = true)
	private void onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo info) {
		// Logic for making slimes turn into magma cubes on lightning strike
		if (((Object)this) instanceof SlimeEntity) {
			var slime = (SlimeEntity)((Object)this);
			// MagmaCubes inherit from slime so skip those
			if (slime.getClass() == SlimeEntity.class) {
				var magmaCube = EntityType.MAGMA_CUBE.create(world);
				magmaCube.refreshPositionAndAngles(slime.getX(), slime.getY(), slime.getZ(), slime.getYaw(), slime.getPitch());
				magmaCube.setAiDisabled(slime.isAiDisabled());
				if (slime.hasCustomName()) {
					magmaCube.setCustomName(slime.getCustomName());
					magmaCube.setCustomNameVisible(slime.isCustomNameVisible());
				}
				magmaCube.setSize(slime.getSize(), true);
				world.spawnEntity(magmaCube);
				slime.discard();
				info.cancel();
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
	private void isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {
		// Since slimes turn into magma cubes on lightning strikes now make sure that magma cubes are immune
		if (((Object)this) instanceof MagmaCubeEntity) {
			var magmaCube = (MagmaCubeEntity)((Object)this);
			if (damageSource == magmaCube.getDamageSources().lightningBolt()) {
				info.setReturnValue(true);
				info.cancel();
			}
		}
	}
}