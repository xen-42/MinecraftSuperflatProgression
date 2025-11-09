package xen42.superflatprogression.entities;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import xen42.superflatprogression.SuperflatProgression;
import xen42.superflatprogression.SuperflatProgressionBlocks;
import xen42.superflatprogression.blocks.EndPortalFrameGeneratorBlock;

public class EndPortalFrameGeneratorEntity extends BlockEntity {

    public EndPortalFrameGeneratorEntity(BlockPos pos, BlockState state) {
        super(SuperflatProgressionBlocks.END_PORTAL_FRAME_GENERATOR_ENTITY, pos, state);
    }    
    
    public static void tick(World world, BlockPos pos, BlockState state, EndPortalFrameGeneratorEntity blockEntity) {
		if (!world.isClient) {
            if (EndPortalFrameGeneratorBlock.canCreateEndPortalFrame(world, state, pos)) {
                if (world.getTime() % 20 == 0) {
                    spawnParticles(ParticleTypes.ASH, world, pos);
                }

                if (world.getTime() % 40 == 0) {
                    for (var dir : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) {
                        for (int i = -1; i <= 1; i++) {
                            var framePos = pos.subtract(dir.getVector().multiply(2)).add(dir.getVector().crossProduct(new Vec3i(0, 1, 0).multiply(i))); 
                            spawnParticles(ParticleTypes.HAPPY_VILLAGER, world, framePos);
                        }
                    }
                }
            }
		}
    }

    private static void spawnParticles(DefaultParticleType type, World world, BlockPos pos) {
        ((ServerWorld) world).spawnParticles(
            type,
            pos.getX() + 0.5,
            pos.getY() + 0.5f,
            pos.getZ() + 0.5f,
            5,
            0.25, 0.25, 0.25,
            0
        );
    }
}
