/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.player;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerJoinMessageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("player_join_message_event_test")
@Mod.EventBusSubscriber()
public class PlayerJoinMessageEventTest
{
    private static final boolean ENABLE = true;
    private static final Logger LOGGER = LogManager.getLogger(PlayerJoinMessageEventTest.class);

    @SubscribeEvent
    public static void onPlayerJoinMessageEvent(PlayerJoinMessageEvent event)
    {
        if (!ENABLE) return;
        LOGGER.info("PlayerJoinMessageEvent fired for player '{}'. Original message: {}", event.getPlayer().getName().getString(), event.getMessage().getString());

        // change the message
        event.setMessage(Component.literal("Welcome ")
                .append(event.getPlayer().getDisplayName())
                .append(" to the server!").withStyle(ChatFormatting.GREEN));
    }
}
