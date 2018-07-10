/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import io.netty.buffer.ByteBuf;

/**
 * Implement this interface for each message you wish to define.
 *
 * @author cpw
 *
 */
public interface IMessage {
    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    void fromBytes(ByteBuf buf);

    /**
     * Deconstruct your message into the supplied byte buffer
     * @param buf
     */
    void toBytes(ByteBuf buf);
}
