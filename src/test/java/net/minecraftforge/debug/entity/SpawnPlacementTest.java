package net.minecraftforge.debug.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod("spawn_placement_test")
public class SpawnPlacementTest
{
    public static final boolean ENABLED = true;
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpawnPlacementTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onZombieSpawnPlacement);
        }
    }

    private void onZombieSpawnPlacement(SpawnPlacementRegisterEvent event)
    {
        if (event.getEntityType() == EntityType.ZOMBIE)
        {
            LOGGER.info("Intercepted zombie spawn placement register!");
            event.requireSecondPredicate(((entityType, level, spawnType, pos, random) -> pos.getY() < 40));
            event.setCanceled(true);
        }
    }
}
