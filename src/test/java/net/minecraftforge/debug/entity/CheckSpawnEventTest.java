/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

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
            if (event.getSpawner().getSpawnerBlockEntity() instanceof SpawnerBlockEntity)
            {
                LOGGER.info("Stopped {} from spawning from Spawner Block Entity : {}", event.getEntity(), event.getSpawner().getSpawnerBlockEntity());
                event.setResult(Event.Result.DENY);
            }
            if (event.getSpawner().getSpawnerEntity() instanceof MinecartSpawner)
            {
                LOGGER.info("Stopped {} from spawning from Minecart Spawner : {}", event.getEntity(), event.getSpawner().getSpawnerEntity());
                event.setResult(Event.Result.DENY);
            }
        }
    }
}