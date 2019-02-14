/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.ForgeRegistries;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class ForgeMessage {
    private static final Logger LOGGER = LogManager.getLogger();

    public static class DimensionRegisterMessage extends ForgeMessage {
        int id;
        ResourceLocation name;
        ModDimension dim;
        @Nullable
        PacketBuffer data;

        public DimensionRegisterMessage(){}
        public DimensionRegisterMessage(int id, ResourceLocation name, ModDimension dim, @Nullable PacketBuffer data)
        {
            this.id = id;
            this.dim = dim;
            this.data = data;
        }

        @Override
        void toBytes(ByteBuf buff)
        {
            PacketBuffer output = new PacketBuffer(buff);
            output.writeInt(id);
            output.writeResourceLocation(name);
            output.writeResourceLocation(dim.getRegistryName());

            if (data == null)
            {
                output.writeShort(0);
            }
            else
            {
                ByteBuf dup = data.duplicate();
                output.writeShort(dup.readableBytes());
                output.writeBytes(dup);
            }
        }

        @Override
        void fromBytes(ByteBuf buff)
        {
            PacketBuffer input = new PacketBuffer(buff);
            id = input.readInt();
            name = input.readResourceLocation();
            dim = ForgeRegistries.MOD_DIMENSIONS.getValue(input.readResourceLocation());

            int len = input.readShort();
            if (len != 0)
            {
                data = new PacketBuffer(Unpooled.buffer());
                input.readBytes(data, len);
            }
        }
    }
/* TODO fluids
    public static class FluidIdMapMessage extends ForgeMessage {
        BiMap<Fluid, Integer> fluidIds = HashBiMap.create();
        Set<String> defaultFluids = Sets.newHashSet();
        @SuppressWarnings("deprecation")
        @Override
        void toBytes(ByteBuf bytes)
        {
            Map<Fluid, Integer> ids = FluidRegistry.getRegisteredFluidIDs();
            bytes.writeInt(ids.size());
            for (Map.Entry<Fluid, Integer> entry : ids.entrySet())
            {
                ByteBufUtils.writeUTF8String(bytes,entry.getKey().getName());
                bytes.writeInt(entry.getValue());
            }
            for (Map.Entry<Fluid, Integer> entry : ids.entrySet())
            {
                String defaultName = FluidRegistry.getDefaultFluidName(entry.getKey());
                ByteBufUtils.writeUTF8String(bytes, defaultName);
            }
        }

        @Override
        void fromBytes(ByteBuf bytes)
        {
            int listSize = bytes.readInt();
            for (int i = 0; i < listSize; i++) {
                String fluidName = ByteBufUtils.readUTF8String(bytes);
                int fluidId = bytes.readInt();
                fluidIds.put(FluidRegistry.getFluid(fluidName), fluidId);
            }
            // do we have a defaults list?

            if (bytes.isReadable())
            {
                for (int i = 0; i < listSize; i++)
                {
                    defaultFluids.add(ByteBufUtils.readUTF8String(bytes));
                }
            }
            else
            {
                LOGGER.info("Legacy server message contains no default fluid list - there may be problems with fluids");
                defaultFluids.clear();
            }
        }
    }
*/
    abstract void toBytes(ByteBuf bytes);
    abstract void fromBytes(ByteBuf bytes);
}
