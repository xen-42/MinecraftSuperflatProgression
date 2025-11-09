package xen42.superflatprogression.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import xen42.superflatprogression.SuperflatProgression;

public class EndPortalFrameGenerator extends Block {

    public EndPortalFrameGenerator(Settings settings) {
        super(settings);
    }
    
    public static boolean canCreateEndPortalFrame(World world, BlockState state, BlockPos pos) {
        if (world.getRegistryKey() != World.OVERWORLD) {
            return false;
        }

        for (var framePositions : List.of(
            pos.add(new Vec3i(-2, 0, -1)),
            pos.add(new Vec3i(-2, 0, 0)),
            pos.add(new Vec3i(-2, 0, 1)),

            pos.add(new Vec3i(2, 0, -1)),
            pos.add(new Vec3i(2, 0, 0)),
            pos.add(new Vec3i(2, 0, 1)),

            pos.add(new Vec3i(-1, 0, -2)),
            pos.add(new Vec3i(0, 0, -2)),
            pos.add(new Vec3i(1, 0, -2)),

            pos.add(new Vec3i(-1, 0, 2)),
            pos.add(new Vec3i(0, 0, 2)),
            pos.add(new Vec3i(1, 0, 2))
        )) {
            if (!world.getBlockState(pos.add(framePositions)).isAir()) {
                return false;
            }
        }
        return true;
    }

    public static void tryCreateEndPortalFrame(ServerWorld world, BlockState state, BlockPos pos) {
        if (canCreateEndPortalFrame(world, state, pos)) {
            for (var dir : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) {
                for (int i = -1; i <= 1; i++) {
                    var framePos = pos.subtract(dir.getVector().multiply(2)).add(dir.getVector().crossProduct(new Vec3i(0, 1, 0).multiply(i)));
                    world.setBlockState(framePos, Blocks.END_PORTAL_FRAME.getDefaultState()
                        .with(EndPortalFrameBlock.FACING, dir)
                        .with(EndPortalFrameBlock.EYE, world.getRandom().nextFloat() < 0.1));
                }
            }
            //world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.BLOCKS, 5f, 1f, false);
            //world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, pos.getX(), pos.getY(), pos.getZ(), 100, 5, 1, 5, 0.5);
        }
    }
}
