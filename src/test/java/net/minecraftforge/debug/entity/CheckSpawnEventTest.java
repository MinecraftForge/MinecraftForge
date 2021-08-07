package net.minecraftforge.debug.entity;

import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("check_spawn_event_test")
public class CheckSpawnEventTest
{
    public static final boolean ENABLE = true;
    public static final Logger LOGGER = LogManager.getLogger();

    public CheckSpawnEventTest()
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void checkSpawn(LivingSpawnEvent.CheckSpawn event)
    {
        if (event.isSpawner())
        {
            if (event.getSpawner() instanceof SpawnerBlockEntity.Spawner spawner)
            {
                SpawnerBlockEntity spawnerBlockEntity = spawner.getSpawnerBlockEntity();
                LOGGER.info("Stopped {} from spawning from Spawner Block Entity : {}", event.getEntity(), spawnerBlockEntity);
                event.setResult(Event.Result.DENY);
            }
            if (event.getSpawner().getSpawnerEntity() instanceof MinecartSpawner spawner)
            {
                LOGGER.info("Stopped {} from spawning from Minecart Spawner : {}", event.getEntity(), spawner);
                event.setResult(Event.Result.DENY);
            }
        }
    }
}