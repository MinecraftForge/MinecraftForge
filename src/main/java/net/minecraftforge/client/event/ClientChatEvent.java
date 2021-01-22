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
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.common.MinecraftForge;

/**
 * ClientChatEvent is fired whenever the client is about to send a chat message or command to the server. <br>
 * This event is fired via {@link ForgeEventFactory#onClientSendMessage(String)},
 * which is executed by {@link GuiScreen#sendChatMessage(String, boolean)}<br>
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
