package net.minecraftforge.fml.common.network.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class FMLHandshakeMessage {
    public static FMLProxyPacket makeCustomChannelRegistration(Set<String> channels)
    {
        String salutation = Joiner.on('\0').join(Iterables.concat(Arrays.asList("FML|HS","FML", "FML|MP"),channels));
        FMLProxyPacket proxy = new FMLProxyPacket(new PacketBuffer(Unpooled.wrappedBuffer(salutation.getBytes(Charsets.UTF_8))), "REGISTER");
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
                FMLLog.fine("Server FML protocol version %d, 4 byte dimension received %d", serverProtocolVersion, overrideDimension);
            }
            else
            {
                FMLLog.info("Server FML protocol version %d, no additional data received", serverProtocolVersion);
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

        public RegistryData(boolean hasMore, ResourceLocation name, PersistentRegistryManager.GameDataSnapshot.Entry entry)
        {
            this.hasMore = hasMore;
            this.name = name;
            this.ids = entry.ids;
            this.substitutions = entry.substitutions;
            this.dummied = entry.dummied;
        }

        private boolean hasMore;
        private ResourceLocation name;
        private Map<ResourceLocation, Integer> ids;
        private Set<ResourceLocation> substitutions;
        private Set<ResourceLocation> dummied;

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
            substitutions = Sets.newHashSet();

            for (int i = 0; i < length; i++)
            {
                substitutions.add(new ResourceLocation(ByteBufUtils.readUTF8String(buffer)));
            }
            dummied = Sets.newHashSet();
            // if the dummied list isn't present - probably an older server
            if (!buffer.isReadable()) return;
            length = ByteBufUtils.readVarInt(buffer, 3);

            for (int i = 0; i < length; i++)
            {
                dummied.add(new ResourceLocation(ByteBufUtils.readUTF8String(buffer)));
            }
            //if (!buffer.isReadable()) return; // In case we expand
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

            ByteBufUtils.writeVarInt(buffer, substitutions.size(), 3);
            for (ResourceLocation entry: substitutions)
            {
                ByteBufUtils.writeUTF8String(buffer, entry.toString());
            }
            ByteBufUtils.writeVarInt(buffer, dummied.size(), 3);
            for (ResourceLocation entry: dummied)
            {
                ByteBufUtils.writeUTF8String(buffer, entry.toString());
            }
        }

        public Map<ResourceLocation,Integer> getIdMap()
        {
            return ids;
        }
        public Set<ResourceLocation> getSubstitutions()
        {
            return substitutions;
        }
        public Set<ResourceLocation> getDummied() { return dummied; }
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
