/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.network.simpleimpl;


/**
 * A message handler based on {@link IMessage}. Implement and override {@link #onMessage(IMessage, MessageContext)} to
 * process your packet. Supply the class to {@link SimpleNetworkWrapper#registerMessage(Class, Class, int, net.minecraftforge.fml.relauncher.Side)}
 * to register both the message type and it's associated handler.
 *
 * @author cpw
 *
 * @param <REQ> This is the request type - it is the message you expect to <em>receive</em> from remote.
 * @param <REPLY> This is the reply type - it is the message you expect to <em>send</em> in reply. You can use IMessage as the type here
 * if you don't anticipate sending a reply.
 */
public interface IMessageHandler<REQ extends IMessage, REPLY extends IMessage> {
    /**
     * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
     * is needed.
     *
     * @param message The message
     * @return an optional return message
     */
    REPLY onMessage(REQ message, MessageContext ctx);
}
