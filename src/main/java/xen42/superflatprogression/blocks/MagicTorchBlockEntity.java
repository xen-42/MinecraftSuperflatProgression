package xen42.superflatprogression.blocks;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgressionBlocks;

public class MagicTorchBlockEntity extends BlockEntity {

    public MagicTorchBlockEntity(BlockPos pos, BlockState state) {
        super(SuperflatProgressionBlocks.MAGIC_TORCH_ENTITY, pos, state);

    }    

    public static void tick(World world, BlockPos pos, BlockState state, MagicTorchBlockEntity blockEntity) {
		if (world.getTime() % 80L == 0L) {
			applyPlayerEffects(world, pos, StatusEffects.INVISIBILITY);
		}
	}

    private static void applyPlayerEffects(World world, BlockPos pos, @Nullable StatusEffect primaryEffect) {
		if (!world.isClient && primaryEffect != null) {
			double d = 1 * 10 + 10;
			int j = (9 + 1 * 2) * 20;
			Box box = new Box(pos).expand(d).stretch(0.0, world.getHeight(), 0.0);
			List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);

			for (PlayerEntity playerEntity : list) {
				playerEntity.addStatusEffect(new StatusEffectInstance(primaryEffect, j, 1, true, true));
			}
		}
	}
}
