/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.chat;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

@Mod("client_chat_event_test")
@Mod.EventBusSubscriber
public final class ClientChatEventTest {
    @SubscribeEvent
    public static boolean onPlayerAttemptChat(ClientChatEvent event) {
        if (event.getMessage().equals("Cancel")) {
            return true;
        } else if (event.getMessage().equals("Replace this text")) {
            event.setMessage("Text replaced.");
            return false;
        }
    }
}
