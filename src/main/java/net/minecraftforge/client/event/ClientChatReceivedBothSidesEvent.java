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

import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a message is received on the client from either the server or the same client
 */
@Cancelable
public class ClientChatReceivedBothSidesEvent extends Event
{
    private Component message;
    private int id;

    public ClientChatReceivedBothSidesEvent(Component message, int id)
    {
        this.message = message;
        this.id = id;
    }

    public Component getMessage()
    {
        return message;
    }

    public void setMessage(Component message)
    {
        this.message = message;
    }

    /*
     * Gets the id of the message that is being received. If there is another message with the same id that was already received then that message will be removed
     */
    public int getId()
    {
        return id;
    }

    /*
     * Sets the id of the message that is being received. If there is another message with the same id that was already received then that message will be removed
     */
    public void setId(int id)
    {
        this.id = id;
    }
}
