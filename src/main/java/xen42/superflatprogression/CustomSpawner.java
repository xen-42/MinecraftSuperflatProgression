package xen42.superflatprogression;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.spawner.Spawner;
import net.minecraft.world.Heightmap;


public class CustomSpawner implements Spawner {
	private int cooldown;

    private EntityType<?> type;
    private boolean isHostile;
    private boolean requiresDark;
    private int maxCount = 5;
    private boolean disableDuringDragonFight;

    public CustomSpawner(EntityType<?> type) {
        this.type = type;
    }

    public CustomSpawner markIsHostile() {
        this.isHostile = true;
        return this;
    }

    public CustomSpawner markRequiresDark() {
        this.requiresDark = true;
        return this;
    }

    public CustomSpawner setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    public CustomSpawner disableDuringDragonFight() {
        this.disableDuringDragonFight = true;
        return this;
    }

	@Override
	public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (isHostile && (!spawnMonsters || world.getDifficulty() == Difficulty.PEACEFUL)) {
            return 0;
        }
        else if (!isHostile && !spawnAnimals) {
            return 0;
        }
        else if (!world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            return 0;
        } else if (disableDuringDragonFight && !world.getAliveEnderDragons().isEmpty()) {
            return 0;
        } else {
			Random random = world.random;
			this.cooldown--;
			if (this.cooldown > 0) {
				return 0;
			} else {
				this.cooldown = 100 + random.nextInt(200);

                int i = world.getPlayers().size();
                if (i < 1) {
                    return 0;
                } else {
                    PlayerEntity playerEntity = (PlayerEntity)world.getPlayers().get(random.nextInt(i));
                    if (playerEntity.isSpectator()) {
                        return 0;
                    } else {
                        int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        BlockPos.Mutable mutable = playerEntity.getBlockPos().mutableCopy().move(j, 0, k);

                        if (world.getLightLevel(mutable) > 11 && requiresDark) {
                            return 0;
                        }

                        var radius = 100;
                        Box box = new Box(
                            playerEntity.getX() - radius, playerEntity.getY() - radius, playerEntity.getZ() - radius,
                            playerEntity.getX() + radius, playerEntity.getY() + radius, playerEntity.getZ() + radius
                        );
                        var mobCount = world.getEntitiesByClass(MobEntity.class, box, e -> e.getType() == this.type).size();

                        if (mobCount > maxCount) return 0;

                        int m = 10;
                        if (!world.isRegionLoaded(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)) {
                            return 0;
                        } else {
                            int n = 0;
                            int o = (int)Math.ceil(world.getLocalDifficulty(mutable).getLocalDifficulty()) + random.nextBetween(1, 3);

                            for (int p = 0; p < o; p++) {
                                n++;
                                mutable.setY(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY());

                                spawnMob(world, mutable, random);

                                mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                            }

                            return n;
                        }
                    }
                }
			}
		}
	}

	private boolean spawnMob(ServerWorld world, BlockPos pos, Random random) {
		BlockState blockState = world.getBlockState(pos);
		if (!SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), type)) {
			return false;
		} else {
			var entity = (MobEntity)type.create(world);
			if (entity != null) {
				entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
				entity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.NATURAL, null, null);
				world.spawnEntityAndPassengers(entity);
				return true;
			} else {
				return false;
			}
		}
	}
}
