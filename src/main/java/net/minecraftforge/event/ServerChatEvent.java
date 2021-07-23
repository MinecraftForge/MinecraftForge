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

package net.minecraftforge.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * ServerChatEvent is fired whenever a C01PacketChatMessage is processed. <br>
 * This event is fired via {@link ForgeHooks#onServerChatEvent(NetHandlerPlayServer, String, ITextComponent)},
 * which is executed by the {@link NetHandlerPlayServer#processChatMessage(CPacketChatMessage)}<br>
 * <br>
 * {@link #username} contains the username of the player sending the chat message.<br>
 * {@link #rawMessage} contains the message being sent.<br>
 * {@link #player} the instance of EntityPlayerMP for the player sending the chat message.<br>
 * {@link #message} contains the instance of ChatComponentTranslation for the sent message.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the chat message is never distributed to all clients.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ServerChatEvent extends Event
{
    private final String rawMessage;
    private final String username;
    private final ServerPlayer player;
    private Component message;

    public ServerChatEvent(ServerPlayer player, String rawMessage, Component message)
    {
        this.rawMessage = rawMessage;
        this.player = player;
        this.username = player.getGameProfile().getName();
        this.message = message;
    }

    public void setMessage(Component message)
    {
        this.message = message;
    }

    public Component getMessage()
    {
        return this.message;
    }

    public String getRawMessage()
    {
        return this.rawMessage;
    }

    public String getUsername()
    {
        return this.username;
    }

    public ServerPlayer getPlayer()
    {
        return this.player;
    }
}
