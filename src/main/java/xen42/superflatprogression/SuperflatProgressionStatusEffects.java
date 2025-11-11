package xen42.superflatprogression;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;

public class SuperflatProgressionStatusEffects {
    private static RegistryEntry<StatusEffect> registerStatusEffect(String id, StatusEffect statusEffect) {
		return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SuperflatProgression.MOD_ID, id), statusEffect);
	}
	public static RegistryEntry<StatusEffect> MAGIC_TORCH_EFFECT;
	public static RegistryEntry<StatusEffect> WARP_EFFECT;
    
    public static void initialize() {
		MAGIC_TORCH_EFFECT = registerStatusEffect("magic_torch_effect",
			(new SuperflatProgressionStatusEffects.CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 8388736)));
        WARP_EFFECT = registerStatusEffect("warp_effect",
			(new SuperflatProgressionStatusEffects.CustomStatusEffect(StatusEffectCategory.HARMFUL, 4772300, 
                (LivingEntity entity, Integer amplifier) -> {
                    if (entity.age % 20 == 0) {
                        var x = entity.getX();
                        var y = entity.getY();
                        var z = entity.getZ();

                        var world = (ServerWorld)entity.getWorld();

                        for(int i = 0; i < 16; ++i) {
                            var x2 = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
                            var y2 = MathHelper.clamp(
                                entity.getY() + (entity.getRandom().nextInt(16) - 8), 
                                world.getBottomY(), 
                                (world.getBottomY() + world.getLogicalHeight() - 1));
                            var z2 = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 16.0;

                            if (entity.hasVehicle()) {
                                entity.stopRiding();
                            }

                            Vec3d vec3d = entity.getPos();
                            if (entity.teleport(x2, y2, z2, true)) {
                                world.emitGameEvent(GameEvent.TELEPORT, vec3d, Emitter.of(entity));
                                SoundEvent soundEvent = entity instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                                world.playSound(null, x, y, z, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                entity.playSound(soundEvent, 1.0F, 1.0F);
                                break;
                            }
                        }
                    }
                })));
    }

    public static class CustomStatusEffect extends StatusEffect {
        private final BiConsumer<LivingEntity, Integer> effect;

        protected CustomStatusEffect(StatusEffectCategory category, int color) {
            this(category, color, null);
        }

        protected CustomStatusEffect(StatusEffectCategory category, int color, BiConsumer<LivingEntity, Integer> effect) {
            super(category, color);
            this.effect = effect;
        }

        @Override
        public void applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (effect != null && !entity.getWorld().isClient) {
                effect.accept(entity, amplifier);
            }
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return effect != null;
        }
    }
}
