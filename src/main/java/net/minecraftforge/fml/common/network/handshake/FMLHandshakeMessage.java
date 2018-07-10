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

package net.minecraftforge.fml.common.network.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.registries.ForgeRegistry;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class FMLHandshakeMessage {
    public static FMLProxyPacket makeCustomChannelRegistration(Set<String> channels)
    {
        String salutation = Joiner.on('\0').join(Iterables.concat(Arrays.asList("FML|HS","FML", "FML|MP"),channels));
        FMLProxyPacket proxy = new FMLProxyPacket(new PacketBuffer(Unpooled.wrappedBuffer(salutation.getBytes(StandardCharsets.UTF_8))), "REGISTER");
        return proxy;
    }
    public static class ServerHello extends FMLHandshakeMessage {
        private byte serverProtocolVersion;
        private int overrideDimension;
        public ServerHello()
        {
            // noargs for the proto
        }
        public ServerHello(int overrideDim)
        {
            this.overrideDimension = overrideDim;
        }

        @Override
        public void toBytes(ByteBuf buffer)
        {
            buffer.writeByte(NetworkRegistry.FML_PROTOCOL);
            buffer.writeInt(overrideDimension);
        }

        @Override
        public void fromBytes(ByteBuf buffer)
        {
            serverProtocolVersion = buffer.readByte();
            // Extended dimension support during login
            if (serverProtocolVersion > 1)
            {
                overrideDimension = buffer.readInt();
                FMLLog.log.debug("Server FML protocol version {}, 4 byte dimension received {}", serverProtocolVersion, overrideDimension);
            }
            else
            {
                FMLLog.log.info("Server FML protocol version {}, no additional data received", serverProtocolVersion);
            }
        }

        public byte protocolVersion()
        {
            return serverProtocolVersion;
        }

        public int overrideDim() {
            return overrideDimension;
        }
    }
    public static class ClientHello extends FMLHandshakeMessage {
        private byte serverProtocolVersion;
        @Override
        public void toBytes(ByteBuf buffer)
        {
            buffer.writeByte(NetworkRegistry.FML_PROTOCOL);
        }

        @Override
        public void fromBytes(ByteBuf buffer)
        {
            serverProtocolVersion = buffer.readByte();
        }

        public byte protocolVersion()
        {
            return serverProtocolVersion;
        }
    }
    public static class ModList extends FMLHandshakeMessage {
        public ModList()
        {

        }
        public ModList(List<ModContainer> modList)
        {
            for (ModContainer mod : modList)
            {
                modTags.put(mod.getModId(), mod.getVersion());
            }
        }
        private Map<String,String> modTags = Maps.newHashMap();

        @Override
        public void toBytes(ByteBuf buffer)
        {
            super.toBytes(buffer);
            ByteBufUtils.writeVarInt(buffer, modTags.size(), 2);
            for (Map.Entry<String,String> modTag: modTags.entrySet())
            {
                ByteBufUtils.writeUTF8String(buffer, modTag.getKey());
                ByteBufUtils.writeUTF8String(buffer, modTag.getValue());
            }
        }

        @Override
        public void fromBytes(ByteBuf buffer)
        {
            super.fromBytes(buffer);
            int modCount = ByteBufUtils.readVarInt(buffer, 2);
            for (int i = 0; i < modCount; i++)
            {
                modTags.put(ByteBufUtils.readUTF8String(buffer), ByteBufUtils.readUTF8String(buffer));
            }
        }

        public String modListAsString()
        {
            return Joiner.on(',').withKeyValueSeparator("@").join(modTags);
        }

        public int modListSize()
        {
            return modTags.size();
        }
        public Map<String, String> modList()
        {
            return modTags;
        }

        @Override
        public String toString(Class<? extends Enum<?>> side)
        {
            return super.toString(side)+":"+modTags.size()+" mods";
        }
    }

    public static class RegistryData extends FMLHandshakeMessage
    {
        public RegistryData()
        {

        }

        public RegistryData(boolean hasMore, ResourceLocation name, ForgeRegistry.Snapshot entry)
        {
            this.hasMore = hasMore;
            this.name = name;
            this.ids = entry.ids;
            this.dummied = entry.dummied;
            this.overrides = entry.overrides;
        }

        private boolean hasMore;
        private ResourceLocation name;
        private Map<ResourceLocation, Integer> ids;
        private Set<ResourceLocation> dummied;
        private Map<ResourceLocation, String> overrides;

        @Override
        public void fromBytes(ByteBuf buffer)
        {
            this.hasMore = buffer.readBoolean();
            this.name = new ResourceLocation(ByteBufUtils.readUTF8String(buffer));

            int length = ByteBufUtils.readVarInt(buffer, 3);
            ids = Maps.newHashMap();

            for (int i = 0; i < length; i++)
            {
                ids.put(new ResourceLocation(ByteBufUtils.readUTF8String(buffer)), ByteBufUtils.readVarInt(buffer, 3));
            }

            length = ByteBufUtils.readVarInt(buffer, 3);
            dummied = Sets.newHashSet();

            for (int i = 0; i < length; i++)
            {
                dummied.add(new ResourceLocation(ByteBufUtils.readUTF8String(buffer)));
            }

            length = ByteBufUtils.readVarInt(buffer, 3);
            overrides = Maps.newHashMap();

            for (int i = 0; i < length; i++)
            {
                overrides.put(new ResourceLocation(ByteBufUtils.readUTF8String(buffer)), ByteBufUtils.readUTF8String(buffer));
            }
        }

        @Override
        public void toBytes(ByteBuf buffer)
        {
            buffer.writeBoolean(this.hasMore);
            ByteBufUtils.writeUTF8String(buffer, this.name.toString());

            ByteBufUtils.writeVarInt(buffer, ids.size(), 3);
            for (Entry<ResourceLocation, Integer> entry: ids.entrySet())
            {
                ByteBufUtils.writeUTF8String(buffer, entry.getKey().toString());
                ByteBufUtils.writeVarInt(buffer, entry.getValue(), 3);
            }

            ByteBufUtils.writeVarInt(buffer, dummied.size(), 3);
            for (ResourceLocation entry: dummied)
            {
                ByteBufUtils.writeUTF8String(buffer, entry.toString());
            }

            ByteBufUtils.writeVarInt(buffer, overrides.size(), 3);
            for (Entry<ResourceLocation, String> entry: overrides.entrySet())
            {
                ByteBufUtils.writeUTF8String(buffer, entry.getKey().toString());
                ByteBufUtils.writeUTF8String(buffer, entry.getValue().toString());
            }
        }

        public Map<ResourceLocation, Integer> getIdMap()
        {
            return ids;
        }

        public Set<ResourceLocation> getDummied()
        {
            return dummied;
        }

        public Map<ResourceLocation, String> getOverrides()
        {
            return overrides;
        }

        public ResourceLocation getName()
        {
            return this.name;
        }

        public boolean hasMore()
        {
            return this.hasMore;
        }

        @Override
        public String toString(Class<? extends Enum<?>> side)
        {
            return super.toString(side) + ":"+ids.size()+" mappings";
        }
    }
    public static class HandshakeAck extends FMLHandshakeMessage {
        int phase;
        public HandshakeAck() {}
        HandshakeAck(int phase)
        {
            this.phase = phase;
        }
        @Override
        public void fromBytes(ByteBuf buffer)
        {
            phase = buffer.readByte();
        }

        @Override
        public void toBytes(ByteBuf buffer)
        {
            buffer.writeByte(phase);
        }
        @Override
        public String toString(Class<? extends Enum<?>> side)
        {
            return super.toString(side) + ":{"+phase+"}";
        }
    }
    public static class HandshakeReset extends FMLHandshakeMessage {
        public HandshakeReset() {}
    }
    public void fromBytes(ByteBuf buffer)
    {
    }

    public void toBytes(ByteBuf buffer)
    {
    }

    public String toString(Class<? extends Enum<?>> side)
    {
        return getClass().getName().substring(getClass().getName().lastIndexOf('$'));
    }
}
