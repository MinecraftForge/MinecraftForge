/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

import java.util.UUID;

/**
 * Fired when {@link NarratorChatListener} receives a chat message for narration. Use {@link #setMessage(Component)} to
 * modify the message before it is sent to the {@link com.mojang.text2speech.Narrator}.
 *
 * <p>This event fires before the hardcoded transformation of translatable components bearing the key
 * {@code chat.type.text} to they key {@code chat.type.text.narrate}. The transformation will still be done if the
 * resulting component from this event holds that key.</p>
 *
 * <p>This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class NarratorChatEvent extends Event
{
    private final ChatType chatType;
    private final UUID sender;
    private Component message;

    /**
     * @hidden
     */
    public NarratorChatEvent(ChatType chatType, Component message, UUID sender)
    {
        this.chatType = chatType;
        this.message = message;
        this.sender = sender;
    }

    /**
     * {@return the type of the chat message} This will be either {@link ChatType#SYSTEM} or {@link ChatType#GAME_INFO},
     * as the narrator chat listener does not receive chat messages of type {@link ChatType#GAME_INFO}.
     */
    public ChatType getChatType()
    {
        return chatType;
    }

    /**
     * {@return the UUID of the sender of the chat message.} This can be the {@linkplain net.minecraft.Util#NIL_UUID
     * nil UUID}, such as for system messages (of type {@link ChatType#SYSTEM}).
     */
    public UUID getSender()
    {
        return sender;
    }

    /**
     * {@return the chat message}
     *
     * <p>Any styling in the event in the narration message is discarded, as {@link Component#getString()} is called to
     * render the message into a regular string for consumption by the narrator.</p>
     *
     * @see #setMessage(Component)
     */
    public Component getMessage()
    {
        return message;
    }

    /**
     * Sets the chat message to be used for narration.
     *
     * @param message the new chat message
     * @throws NullPointerException if the chat message is {@code null}
     * @see #getMessage()
     */
    public void setMessage(Component message)
    {
        this.message = Preconditions.checkNotNull(message);
    }
}
