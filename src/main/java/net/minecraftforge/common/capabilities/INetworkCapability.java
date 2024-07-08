/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.common.capabilities;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Provides the ability for {@link ICapabilityProvider}s to synchronize
 * capabilities over the network.
 */
public interface INetworkCapability
{
    /**
     * Write capabilities to the specified {@link FriendlyByteBuf}.
     * 
     * @param out      - the {@link FriendlyByteBuf} to write to
     * @param writeAll - {@code true} if all data should be written, {@code false}
     *                 if only dirty data should be written
     */
    void writeCapabilities(FriendlyByteBuf out, boolean writeAll);

    /**
     * Read capabilities from the specified {@link FriendlyByteBuf}.
     * 
     * @param in - {@link FriendlyByteBuf} to read from
     */
    void readCapabilities(FriendlyByteBuf in);

    /**
     * Whether capability data is 'dirty' and needs to be sent to the client.
     */
    boolean requiresSync();

    /**
     * A version of {@link #writeCapabilities(FriendlyByteBuf, boolean)} which
     * returns a {@code byte[]}. This is intended for use with {@link ByteArrayTag},
     * generally {@link #writeCapabilities(FriendlyByteBuf, boolean)} should be used
     * to avoid unnecessarily creating a {@link byte[]}.
     * 
     * @param writeAll - {@code true} if all data should be written, {@code false}
     *                 if only dirty data should be written
     * @return a {@code byte[]} containing the serialized capability data
     */
    default byte[] writeCapabilities(boolean writeAll)
    {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        this.writeCapabilities(buf, writeAll);
        var bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        return bytes;
    }

    /**
     * A version of {@link #readCapabilities(FriendlyByteBuf)} which accepts a
     * {@code byte[]}.
     * 
     * @param bytes - the {@link byte[]} to read from
     */
    default void readCapabilities(byte[] bytes)
    {
        var buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes));
        this.readCapabilities(buf);
        buf.release();
    }
}
