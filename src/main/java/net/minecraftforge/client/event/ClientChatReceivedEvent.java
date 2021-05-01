/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.event;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Fired on the client when a chat message is received.<br>
 * If this event is cancelled, the message is not displayed in the chat message window.<br>
 * Fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class ClientChatReceivedEvent extends Event
{
    private ITextComponent message;
    private final ChatType type;
    @Nullable
    private final UUID senderUUID;

    public ClientChatReceivedEvent(ChatType type, ITextComponent message, @Nullable UUID senderUUID)
    {
        this.type = type;
        this.message = message;
        this.senderUUID = senderUUID;
    }

    public ITextComponent getMessage()
    {
        return message;
    }

    public void setMessage(ITextComponent message)
    {
        this.message = message;
    }

    public ChatType getType()
    {
        return type;
    }

    /**
     * The UUID of the player or entity that sent this message, or null if not known.
     * This will be equal to {@link net.minecraft.util.Util#DUMMY_UUID} for system messages.
     */
    @Nullable
    public UUID getSenderUUID()
    {
        return senderUUID;
    }
}
