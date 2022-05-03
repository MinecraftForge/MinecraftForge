/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.player;

import net.minecraftforge.event.entity.player.PlayerNegotiationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("player_negotiation_event_test")
@Mod.EventBusSubscriber()
public class PlayerNegotiationEventTest
{
    private static final boolean ENABLE = true;
    private static final Logger LOGGER = LogManager.getLogger(PlayerNegotiationEventTest.class);

    @SubscribeEvent
    public static void onPlayerNegotiation(PlayerNegotiationEvent event)
    {
        if (!ENABLE) return;
        LOGGER.info("{}[{}] started negotiation", event.getProfile().getName(), event.getConnection().getRemoteAddress());
        event.enqueueWork(() -> {
            LOGGER.info("Hello from {}", Thread.currentThread().getName());
        });
    }
}
