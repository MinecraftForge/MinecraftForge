/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.player;

import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("entity_interact_specific_event_test")
@Mod.EventBusSubscriber
public class EntityInteractSpecificEventTest
{
    private static final boolean ENABLE = false;
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if (!ENABLE) return;
        if (event.getTarget() instanceof ArmorStand)
        {
            LOGGER.info("EntityInteractSpecific event has been canceled for armor stand");
            event.setCanceled(true);
        }
    }
}
