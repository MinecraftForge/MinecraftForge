/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.entity.player;

import net.minecraftsingularity.event.entity.player.PermissionsChangedEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("permissions_changed_event_test")
@Mod.EventBusSubscriber
public class PermissionsChangedEventTest
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPermissionChanged(PermissionsChangedEvent event)
    {
        LOGGER.info("{} permission level changed to {} from {}",
                event.getEntity().getName().getString(),
                event.getNewLevel(),
                event.getOldLevel());
    }
}
