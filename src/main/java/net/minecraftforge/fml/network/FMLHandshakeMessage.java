package net.minecraftforge.fml.network;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.List;
import java.util.stream.Collectors;

class FMLHandshakeMessage
{
    // Login index sequence number
    private int index;
    void setPacketIndexSequence(int i)
    {
        this.index = i;
    }

    int getPacketIndexSequence()
    {
        return index;
    }

    /**
     * Server to client "list of mods". Always first handshake message.
     */
    static class S2CModList extends FMLHandshakeMessage
    {
        private NBTTagList channels;
        private List<String> modList;

        S2CModList() {
            this.modList = ModList.get().getMods().stream().map(ModInfo::getModId).collect(Collectors.toList());
        }

        S2CModList(NBTTagCompound nbtTagCompound)
        {
            this.modList = nbtTagCompound.getTagList("modlist", 8).stream().map(INBTBase::getString).collect(Collectors.toList());
            this.channels = nbtTagCompound.getTagList("channels", 10);
        }

        static S2CModList decode(PacketBuffer packetBuffer)
        {
            final NBTTagCompound nbtTagCompound = packetBuffer.readCompoundTag();
            return new S2CModList(nbtTagCompound);
        }

        void encode(PacketBuffer packetBuffer)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("modlist",modList.stream().map(NBTTagString::new).collect(Collectors.toCollection(NBTTagList::new)));
            tag.setTag("channels", NetworkRegistry.buildChannelVersions());
            packetBuffer.writeCompoundTag(tag);
        }

        String getModList() {
            return String.join(",", modList);
        }

        NBTTagList getChannels() {
            return this.channels;
        }
    }

    static class C2SModListReply extends S2CModList
    {
        C2SModListReply() {
            super();
        }

        C2SModListReply(final NBTTagCompound buffer) {
            super(buffer);
        }

        static C2SModListReply decode(PacketBuffer buffer)
        {
            return new C2SModListReply(buffer.readCompoundTag());
        }

        public void encode(PacketBuffer buffer)
        {
            super.encode(buffer);
        }
    }
}
