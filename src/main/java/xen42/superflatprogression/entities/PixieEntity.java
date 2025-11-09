package xen42.superflatprogression.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionItems;

public class PixieEntity extends PassiveEntity implements Flutterer {

    public PixieEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
		this.moveControl = new FlightMoveControl(this, 20, true);
    }
    
    public static DefaultAttributeContainer.Builder createPixieAttributes() {
        return PassiveEntity.createLivingAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0f)
			.add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6F)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0);
    }

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
        this.goalSelector.add(2, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 1.6, 1.4, entity -> true));
		this.goalSelector.add(3, new FlyGoal(this, 1f));
		//this.goalSelector.add(4, new LookAroundGoal(this));
	}

    @Override
	public PixieEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return SuperflatProgression.PIXIE_ENTITY.create(serverWorld);
	}

    @Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.BLOCK_AMETHYST_BLOCK_HIT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_AMETHYST_BLOCK_HIT;
	}

    @Override
	protected void playStepSound(BlockPos pos, BlockState state) {

	}

	@Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(SuperflatProgressionItems.PIXIE_SPAWN_EGG);
    }

    public static boolean isValidSpawn(EntityType<? extends PixieEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return !world.toServerWorld().isDay() &&
			world.getBlockState(pos).isAir() && 
			!world.getBlockState(pos.down()).isAir() && 
			!world.getBlockState(pos).isOf(Blocks.LAVA);
    }

	@Override
	public void tick() {
		super.tick();
		var world = this.getWorld();
		if (this.age % 20 == 0) {
			if (!world.isClient) {
				((ServerWorld) world).spawnParticles(
				SuperflatProgression.PIXIE_PARTICLE,
				this.getX(),
				this.getY(),
				this.getZ(),
				2,
				0.25, 0.25, 0.25,
				1
				);
			}
		}
		if (!world.isClient && world.isDay()) {
			this.discard();
		}
	}

	@Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return world.getBlockState(pos).isAir() ? 10.0F : world.getPhototaxisFavor(pos);
    }

	@Override
    public boolean isInAir() {
        return !isOnGround();
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {}

	@Override
    protected EntityNavigation createNavigation(World world) {
        var navigation = new BirdNavigation(this, world) {
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }
        };
        navigation.setCanPathThroughDoors(true);
        navigation.setCanSwim(true);
        return navigation;
    }
}
