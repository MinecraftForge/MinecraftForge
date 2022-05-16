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

/**
 * Tests {@link PlayerNegotiationEvent} by listening for and then enqueuing work to be done asynchronously,
 * details regarding the work execution is printed out and exceptions are thrown to ensure proper handling.
 */
@Mod("player_negotiation_event_test")
@Mod.EventBusSubscriber()
public class PlayerNegotiationEventTest
{
    private static final boolean ENABLE = false;
    private static final Logger LOGGER = LogManager.getLogger(PlayerNegotiationEventTest.class);

    @SubscribeEvent
    public static void onPlayerNegotiation(PlayerNegotiationEvent event)
    {
        if (!ENABLE) return;
        LOGGER.info("{} ({})[{}] started negotiation", event.getProfile().getName(), event.getProfile().getId(), event.getConnection().getRemoteAddress());
        event.enqueueWork(() -> {
            // This log message should be printed on the fork-join-pool.
            LOGGER.info("Hello from {}", Thread.currentThread().getName());
        });
        event.enqueueWork(() -> {
            // This exception should be logged by Forge.
            throw new RuntimeException("Test Exception from PlayerNegotiationEventTest");
        });
    }
}
