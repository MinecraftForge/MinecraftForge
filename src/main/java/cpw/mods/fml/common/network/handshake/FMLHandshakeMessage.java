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

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public abstract class FMLHandshakeMessage {
    public static FMLProxyPacket makeCustomChannelRegistration(Set<String> channels)
    {
        String salutation = Joiner.on('\0').join(Iterables.concat(Arrays.asList("FML|HS","FML"),channels));
        FMLProxyPacket proxy = new FMLProxyPacket(Unpooled.wrappedBuffer(salutation.getBytes(Charsets.UTF_8)), "REGISTER");
        return proxy;
    }
    public static class ServerHello extends FMLHandshakeMessage {
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

        public ModIdData(Map<String,Integer> modIds)
        {
            this.modIds = modIds;
        }

        private Map<String,Integer> modIds;
        @Override
        public void fromBytes(ByteBuf buffer)
        {
            int length = ByteBufUtils.readVarInt(buffer, 3);
            modIds = Maps.newHashMap();

            for (int i = 0; i < length; i++)
            {
                modIds.put(ByteBufUtils.readUTF8String(buffer),ByteBufUtils.readVarInt(buffer, 3));
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
        }

        public Map<String,Integer> dataList()
        {
            return modIds;
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
