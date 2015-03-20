package cpw.mods.fml.common.network.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.registry.GameData;

public abstract class FMLHandshakeMessage {
    public static FMLProxyPacket makeCustomChannelRegistration(Set<String> channels)
    {
        String salutation = Joiner.on('\0').join(Iterables.concat(Arrays.asList("FML|HS","FML"),channels));
        FMLProxyPacket proxy = new FMLProxyPacket(Unpooled.wrappedBuffer(salutation.getBytes(Charsets.UTF_8)), "REGISTER");
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

    public static class ModIdData extends FMLHandshakeMessage {
        public ModIdData()
        {

        }

        public ModIdData(GameData.GameDataSnapshot snapshot)
        {
            this.modIds = snapshot.idMap;
            this.blockSubstitutions = snapshot.blockSubstitutions;
            this.itemSubstitutions = snapshot.itemSubstitutions;
        }

        private Map<String,Integer> modIds;
        private Set<String> blockSubstitutions;
        private Set<String> itemSubstitutions;
        @Override
        public void fromBytes(ByteBuf buffer)
        {
            int length = ByteBufUtils.readVarInt(buffer, 3);
            modIds = Maps.newHashMap();
            blockSubstitutions = Sets.newHashSet();
            itemSubstitutions = Sets.newHashSet();

            for (int i = 0; i < length; i++)
            {
                modIds.put(ByteBufUtils.readUTF8String(buffer),ByteBufUtils.readVarInt(buffer, 3));
            }
            // we don't have any more data to read
            if (!buffer.isReadable())
            {
                return;
            }
            length = ByteBufUtils.readVarInt(buffer, 3);
            for (int i = 0; i < length; i++)
            {
                blockSubstitutions.add(ByteBufUtils.readUTF8String(buffer));
            }
            length = ByteBufUtils.readVarInt(buffer, 3);
            for (int i = 0; i < length; i++)
            {
                itemSubstitutions.add(ByteBufUtils.readUTF8String(buffer));
            }
        }

        @Override
        public void toBytes(ByteBuf buffer)
        {
            ByteBufUtils.writeVarInt(buffer, modIds.size(), 3);
            for (Entry<String, Integer> entry: modIds.entrySet())
            {
                ByteBufUtils.writeUTF8String(buffer, entry.getKey());
                ByteBufUtils.writeVarInt(buffer, entry.getValue(), 3);
            }

            ByteBufUtils.writeVarInt(buffer, blockSubstitutions.size(), 3);
            for (String entry: blockSubstitutions)
            {
                ByteBufUtils.writeUTF8String(buffer, entry);
            }
            ByteBufUtils.writeVarInt(buffer, blockSubstitutions.size(), 3);

            for (String entry: itemSubstitutions)
            {
                ByteBufUtils.writeUTF8String(buffer, entry);
            }
        }

        public Map<String,Integer> dataList()
        {
            return modIds;
        }
        public Set<String> blockSubstitutions()
        {
            return blockSubstitutions;
        }
        public Set<String> itemSubstitutions()
        {
            return itemSubstitutions;
        }

        @Override
        public String toString(Class<? extends Enum<?>> side)
        {
            return super.toString(side) + ":"+modIds.size()+" mappings";
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
