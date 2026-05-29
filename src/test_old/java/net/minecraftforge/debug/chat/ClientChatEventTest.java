/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.chat;

import net.minecraftsingularity.client.event.ClientChatEvent;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;

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
