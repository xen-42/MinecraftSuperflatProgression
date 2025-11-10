package xen42.superflatprogression;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

// Simialar to base game WanderingTraderManager
public class MobSpawnerHelper {
	public static void spawnWanderingTrader(ServerPlayerEntity playerEntity) {
		for (int i = 0; i < 10; i++) {
			if (trySpawnWanderingTrader(playerEntity)) {
				return;
			}
		}
	}

	private static boolean trySpawnWanderingTrader(ServerPlayerEntity playerEntity) {
        var world = (ServerWorld)playerEntity.getWorld();
        BlockPos blockPos = playerEntity.getBlockPos();
        PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
        Optional<BlockPos> optional = pointOfInterestStorage.getPosition(
            poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING), pos -> true, blockPos, 48, PointOfInterestStorage.OccupationStatus.ANY
        );
        BlockPos blockPos2 = (BlockPos)optional.orElse(blockPos);
        BlockPos blockPos3 = getNearbySpawnPos(world, blockPos2, 12);
        if (blockPos3 != null && doesNotSuffocateAt(world, blockPos3)) {
            if (world.getBiome(blockPos3).isIn(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                return false;
            }

            WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(world, blockPos3, SpawnReason.EVENT);
            if (wanderingTraderEntity != null) {
                for (int j = 0; j < 2; j++) {
                    spawnLlama(world, wanderingTraderEntity, 4);
                }

                wanderingTraderEntity.setDespawnDelay(48000);
                wanderingTraderEntity.setWanderTarget(blockPos2);
                wanderingTraderEntity.setPositionTarget(blockPos2, 16);
                return true;
            }
        }

        return false;
	}

	public static void spawnMob(ServerPlayerEntity playerEntity, EntityType<?> entityType) {
		var range = 32;
		var world = (ServerWorld)playerEntity.getWorld();
		for (int i = 0; i < 10; i++) {
			BlockPos blockPos = getNearbySpawnPos(world, playerEntity.getBlockPos(), range);
			if (blockPos != null) {
				entityType.spawn(world, blockPos, SpawnReason.EVENT);
				return;
			}
		}
		// If we failed just put them on the player
		entityType.spawn(world, playerEntity.getBlockPos(), SpawnReason.EVENT);
	}

	private static void spawnLlama(ServerWorld world, WanderingTraderEntity wanderingTrader, int range) {
		BlockPos blockPos = getNearbySpawnPos(world, wanderingTrader.getBlockPos(), range);
		if (blockPos != null) {
			TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(world, blockPos, SpawnReason.EVENT);
			if (traderLlamaEntity != null) {
				traderLlamaEntity.attachLeash(wanderingTrader, true);
			}
		}
	}

	@Nullable
	private static BlockPos getNearbySpawnPos(ServerWorld world, BlockPos pos, int range) {
		BlockPos blockPos = null;

		for (int i = 0; i < 10; i++) {
			int j = pos.getX() + world.getRandom().nextInt(range * 2) - range;
			int k = pos.getZ() + world.getRandom().nextInt(range * 2) - range;
			int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, j, k);
			BlockPos blockPos2 = new BlockPos(j, l, k);
			if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos2, EntityType.WANDERING_TRADER)) {
				blockPos = blockPos2;
				break;
			}
		}

		return blockPos;
	}

	private static boolean doesNotSuffocateAt(BlockView world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.iterate(pos, pos.add(1, 2, 1))) {
			if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
