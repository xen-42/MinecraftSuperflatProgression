package xen42.superflatprogression.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionItems;

public class PixieEntity extends PassiveEntity {

    public PixieEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }
    
    public static DefaultAttributeContainer.Builder createPixieAttributes() {
        return PassiveEntity.createLivingAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0f)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0);
    }

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
        this.goalSelector.add(2, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 1.6, 1.4, entity -> true));
		this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(4, new LookAroundGoal(this));
	}

    @Override
	public PixieEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return SuperflatProgression.PIXIE_ENTITY.create(serverWorld);
	}

    @Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ALLAY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ALLAY_DEATH;
	}

    @Override
	protected void playStepSound(BlockPos pos, BlockState state) {

	}

	@Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(SuperflatProgressionItems.PIXIE_SPAWN_EGG);
    }

    public static boolean isValidSpawn(EntityType<? extends PixieEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getBlockState(pos).isAir() && !world.getBlockState(pos.down()).isAir() && !world.getBlockState(pos).isOf(Blocks.LAVA);
    }
}
