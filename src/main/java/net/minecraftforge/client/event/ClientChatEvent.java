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

import com.google.common.base.Strings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when the client is about to send a chat message or command to the server.
 * This can be used to implement client-only commands as chat messages, or intercepting messages for modification.
 *
 * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
 * If the event is cancelled, the chat message or command will not be sent to the server. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see ForgeEventFactory#onClientSendMessage(String)
 **/
@Cancelable
public class ClientChatEvent extends Event
{
    private String message;
    private final String originalMessage;

    public ClientChatEvent(String message)
    {
        this.originalMessage = Strings.nullToEmpty(message);
        this.message = this.originalMessage;
    }

    /**
     * {@return the message that will be sent to the server, if the event is not cancelled}
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the new message to be sent to the server, if the event is not cancelled.
     *
     * @param message the new message to be sent
     */
    public void setMessage(String message)
    {
        this.message = Strings.nullToEmpty(message);
    }

    /**
     * {@return the original message that was to be sent to the server}
     */
    public String getOriginalMessage()
    {
        return originalMessage;
    }
}
