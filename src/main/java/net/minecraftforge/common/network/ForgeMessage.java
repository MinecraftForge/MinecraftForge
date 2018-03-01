/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common.network;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public abstract class ForgeMessage {
    public static class DimensionRegisterMessage extends ForgeMessage {
        /** The dimension ID to register on client */
        int dimensionId;
        /** The provider ID to register with dimension on client */
        String providerId;

        public DimensionRegisterMessage(){}
        public DimensionRegisterMessage(int dimensionId, String providerId)
        {
            this.dimensionId = dimensionId;
            this.providerId = providerId;
        }

        @Override
        void toBytes(ByteBuf bytes)
        {
            bytes.writeInt(this.dimensionId);
            byte[] data = this.providerId.getBytes(StandardCharsets.UTF_8);
            bytes.writeShort(data.length);
            bytes.writeBytes(data);
        }

        @Override
        void fromBytes(ByteBuf bytes)
        {
            dimensionId = bytes.readInt();
            byte[] data = new byte[bytes.readShort()];
            bytes.readBytes(data);
            providerId = new String(data, StandardCharsets.UTF_8);
        }
    }

    abstract void toBytes(ByteBuf bytes);
    abstract void fromBytes(ByteBuf bytes);
}
