/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.chat;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod("client_chat_event_test")
@Mod.EventBusSubscriber
public class ClientChatEventTest
{
    @SubscribeEvent
    public static void onPlayerAttemptChat(ClientChatEvent event)
    {
        if (event.getMessage().equals("Cancel"))
            event.setCanceled(true);
        else if (event.getMessage().equals("Replace this text"))
            event.setMessage("Text replaced.");
    }
}
