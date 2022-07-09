/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.Util;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a chat message is received on the client.
 * This can be used for filtering and detecting messages with specific words or phrases, and suppressing them.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If the event is cancelled, the message is not displayed in the chat message window.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see ChatType
 */
@Cancelable
public class ClientChatReceivedEvent extends Event
{
    private Component message;
    private final ChatType type;
    private final ChatSender chatSender;

    @ApiStatus.Internal
    public ClientChatReceivedEvent(ChatType type, Component message, ChatSender chatSender)
    {
        this.type = type;
        this.message = message;
        this.chatSender = chatSender;
    }

    /**
     * {@return the message that will be displayed in the chat message window, if the event is not cancelled}
     */
    public Component getMessage()
    {
        return message;
    }

    /**
     * Sets the new message to be displayed in the chat message window, if the event is not cancelled.
     *
     * @param message the new message to be sent
     */
    public void setMessage(Component message)
    {
        this.message = message;
    }

    /**
     * {@return the type of the chat message}
     */
    public ChatType getType()
    {
        return type;
    }

    /**
     * {@return the sender of this chat message} This contains the UUID, name, and (optionally) the team name of the
     * entity which sent the chat message. For system messages, {@link ChatSender#uuid()} will be equal to
     * {@link Util#NIL_UUID}.
     */
    public ChatSender getChatSender()
    {
        return chatSender;
    }
}
