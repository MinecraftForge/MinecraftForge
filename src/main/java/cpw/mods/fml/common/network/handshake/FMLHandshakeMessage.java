package cpw.mods.fml.common.network.handshake;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;

import io.netty.buffer.ByteBuf;

public abstract class FMLHandshakeMessage {
    public static class ServerHello extends FMLHandshakeMessage {
        private byte serverProtocolVersion;
        public void toBytes(ByteBuf buffer)
        {
            buffer.writeByte(NetworkRegistry.FML_PROTOCOL);
        }

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
        public void toBytes(ByteBuf buffer)
        {
            buffer.writeByte(NetworkRegistry.FML_PROTOCOL);
        }

        public void fromBytes(ByteBuf buffer)
        {
            serverProtocolVersion = buffer.readByte();
        }

        public byte protocolVersion()
        {
            return serverProtocolVersion;
        }
    }
    public static class ServerModList extends FMLHandshakeMessage {
        private List<String> modTags = Lists.newArrayList();

        @Override
        public void toBytes(ByteBuf buffer)
        {
            super.toBytes(buffer);
            ByteBufUtils.writeVarInt(buffer, modTags.size(), 2);
            for (String modTag: modTags)
            {
                ByteBufUtils.writeUTF8String(buffer, modTag);
            }
        }

        @Override
        public void fromBytes(ByteBuf buffer)
        {
            super.fromBytes(buffer);
            int modCount = ByteBufUtils.readVarInt(buffer, 2);
            for (int i = 0; i < modCount; i++)
            {
                modTags.add(ByteBufUtils.readUTF8String(buffer));
            }
        }
    }

    public static class ClientModList extends FMLHandshakeMessage {
        public ClientModList()
        {

        }
        public ClientModList(List<ModContainer> modList)
        {
            for (ModContainer mod : modList)
            {
                modTags.add(new String[] { mod.getModId(), mod.getVersion() });
            }
        }
        private List<String[]> modTags = Lists.newArrayList();

        @Override
        public void toBytes(ByteBuf buffer)
        {
            super.toBytes(buffer);
            ByteBufUtils.writeVarInt(buffer, modTags.size(), 2);
            for (String[] modTag: modTags)
            {
                ByteBufUtils.writeUTF8String(buffer, modTag[0]);
                ByteBufUtils.writeUTF8String(buffer, modTag[1]);
            }
        }

        @Override
        public void fromBytes(ByteBuf buffer)
        {
            super.fromBytes(buffer);
            int modCount = ByteBufUtils.readVarInt(buffer, 2);
            for (int i = 0; i < modCount; i++)
            {
                modTags.add(new String[] { ByteBufUtils.readUTF8String(buffer), ByteBufUtils.readUTF8String(buffer)});
            }
        }

        public String modListAsString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("[ ");
            for (int i = 0; i < modTags.size(); i++)
            {
                String[] mod = modTags.get(i);
                sb.append(mod[0]).append("@").append(mod[1]);
                if (i < modTags.size() - 1) sb.append(", ");
            }
            sb.append(" ]");
            return sb.toString();
        }

        public int modListSize()
        {
            return modTags.size();
        }
    }

    public static class ClientAck extends FMLHandshakeMessage {

    }
    public void fromBytes(ByteBuf buffer)
    {
    }

    public void toBytes(ByteBuf buffer)
    {
    }


}
