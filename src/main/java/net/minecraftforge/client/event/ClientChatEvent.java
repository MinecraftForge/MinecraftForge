/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.base.Strings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * ClientChatEvent is fired whenever the client is about to send a chat message or command to the server. <br>
 * This event is fired via {@link ForgeEventFactory#onClientSendMessage(String)},
 * which is executed by {@link Screen#sendMessage(String, boolean)}<br>
 * <br>
 * {@link #message} contains the message that will be sent to the server. This can be changed by mods.<br>
 * {@link #originalMessage} contains the original message that was going to be sent to the server. This cannot be changed by mods.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the chat message or command is never sent to the server.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ClientChatEvent extends Event
{
    private String message;
    private final String originalMessage;
    public ClientChatEvent(String message)
    {
        this.setMessage(message);
        this.originalMessage = Strings.nullToEmpty(message);
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = Strings.nullToEmpty(message);
    }

    public String getOriginalMessage()
    {
        return originalMessage;
    }
}
