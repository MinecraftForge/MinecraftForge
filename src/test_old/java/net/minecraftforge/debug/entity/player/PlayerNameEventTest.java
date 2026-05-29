/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.entity.player;

import net.minecraft.network.chat.Component;
import net.minecraftsingularity.event.entity.player.PlayerEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;

@Mod("player_name_event_test")
@Mod.EventBusSubscriber()
public class PlayerNameEventTest
{
    private static final boolean ENABLE = false;

    @SubscribeEvent
    public static void onPlayerNameEvent(PlayerEvent.NameFormat event)
    {
        if (!ENABLE) return;
        event.setDisplayname(Component.literal("Test Name"));
    }
}
