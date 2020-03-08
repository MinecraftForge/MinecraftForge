package net.minecraftforge.debug.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.Validate;

@Mod(MagmaBlockSpawnTest.MODID)
public class MagmaBlockSpawnTest {

    static final String MODID = "magma_block_spawn_test";

    @SubscribeEvent
    public static void onEntitySpawned(LivingSpawnEvent event)
    {
        // Check to see if the entity that spawned was on top of a block of lava. If it did, then it means the
        // canCreatureSpawn event returned true, i.e., the wrong value
        BlockPos currBlockPos = new BlockPos(event.getX(), event.getY(), event.getZ());
        Validate.isTrue(!(event.getWorld().getBlockState(currBlockPos).getMaterial() == Material.LAVA));
    }

    @SubscribeEvent
    public static void onEntitySpawnedV2(LivingSpawnEvent event)
    {
        // Similar to onEntitySpawned, this makes an explicit call to canCreatureSpawn and checks to see if it's
        // returning the right value with a direct call
        BlockPos currBlockPos = new BlockPos(event.getX(), event.getY(), event.getZ());
        IWorld world = event.getWorld();
        BlockState state = world.getBlockState(currBlockPos);
        EntitySpawnPlacementRegistry.PlacementType type = EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS;
        Validate.isTrue(!((IForgeBlock)state).canCreatureSpawn(state, world, currBlockPos, type, null));
    }
}
