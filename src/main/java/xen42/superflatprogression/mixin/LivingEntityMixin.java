package xen42.superflatprogression.mixin;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import xen42.superflatprogression.SuperflatProgressionStatusEffects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    // This is what applies the Bounty buff by re-rolling loot drops
	@Inject(at = @At("HEAD"), method = "dropLoot", cancellable = true)
	private void dropLoot(DamageSource damageSource, boolean causedByPlayer, CallbackInfo info) {
        var attacker = damageSource.getAttacker();
        var entity = (LivingEntity)((Object)this);
        if (attacker instanceof LivingEntity && ((LivingEntity)attacker).hasStatusEffect(SuperflatProgressionStatusEffects.MAGIC_TORCH_EFFECT.value())) {
            // Largely the same as base game method with a change at the end
            Identifier identifier = entity.getLootTable();
            LootTable lootTable = entity.getWorld().getServer().getLootManager().getLootTable(identifier);
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld)entity.getWorld())
                .add(LootContextParameters.THIS_ENTITY, entity)
                .add(LootContextParameters.ORIGIN, entity.getPos())
                .add(LootContextParameters.DAMAGE_SOURCE, damageSource)
                .addOptional(LootContextParameters.KILLER_ENTITY, damageSource.getAttacker())
                .addOptional(LootContextParameters.DIRECT_KILLER_ENTITY, damageSource.getSource());

            var attackingPlayer = entity.getPrimeAdversary() instanceof PlayerEntity ? (PlayerEntity)entity.getPrimeAdversary() : null;
            if (causedByPlayer && attackingPlayer != null && attackingPlayer instanceof PlayerEntity) {
                builder = builder.add(LootContextParameters.LAST_DAMAGE_PLAYER, attackingPlayer).luck(attackingPlayer.getLuck());
            }

            LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.ENTITY);

            // Basically try dropping loot X times and take the best roll for each item
            // If I knew how to do a transpiler this would be the part where I did a transpiler
            var possibleDrops = new HashMap<Item, ItemStack>();
            for (int i = 0; i < 10; i++) {
                lootTable.generateLoot(lootContextParameterSet, entity.getLootTableSeed(), (itemStack) -> {
                    if (possibleDrops.containsKey(itemStack.getItem())) {
                        if (possibleDrops.get(itemStack.getItem()).getCount() < itemStack.getCount()) {
                            possibleDrops.put(itemStack.getItem(), itemStack);
                        }
                    }
                    else {
                        possibleDrops.put(itemStack.getItem(), itemStack);
                    }
                });
            }

            for (var itemStack : possibleDrops.values()) {
                entity.dropStack(itemStack);
            }

            info.cancel();
        }
	}
}
