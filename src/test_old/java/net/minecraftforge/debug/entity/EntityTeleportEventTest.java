/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.entity;

import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.event.entity.EntityTeleportEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("entity_teleport_event_test")
public class EntityTeleportEventTest
{
    public static final boolean ENABLE = true;
    public static final Logger LOGGER = LogManager.getLogger();

    public EntityTeleportEventTest()
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void entityTeleport(EntityTeleportEvent event)
    {
        LOGGER.info("{} teleporting from {} to {}", event.getEntity(), event.getPrev(), event.getTarget());
    }
}
