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

package net.minecraftforge.fml.network;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.stream.Collectors;

public class FMLHandshakeMessages
{
    static class LoginIndexedMessage {
        private int loginIndex;

        void setLoginIndex(final int loginIndex) {
            this.loginIndex = loginIndex;
        }

        int getLoginIndex() {
            return loginIndex;
        }
    }
    /**
     * Server to client "list of mods". Always first handshake message.
     */
    public static class S2CModList extends LoginIndexedMessage
    {
        private NBTTagList channels;
        private List<String> modList;

        public S2CModList() {
            this.modList = ModList.get().getMods().stream().map(ModInfo::getModId).collect(Collectors.toList());
        }

        S2CModList(NBTTagCompound nbtTagCompound)
        {
            this.modList = nbtTagCompound.getList("modlist", 8).stream().map(INBTBase::getString).collect(Collectors.toList());
            this.channels = nbtTagCompound.getList("channels", 10);
        }

        public static S2CModList decode(PacketBuffer packetBuffer)
        {
            final NBTTagCompound nbtTagCompound = packetBuffer.readCompoundTag();
            return new S2CModList(nbtTagCompound);
        }

        public void encode(PacketBuffer packetBuffer)
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

    public static class C2SModListReply extends S2CModList
    {
        public C2SModListReply() {
            super();
        }

        C2SModListReply(final NBTTagCompound buffer) {
            super(buffer);
        }

        public static C2SModListReply decode(PacketBuffer buffer)
        {
            return new C2SModListReply(buffer.readCompoundTag());
        }

        public void encode(PacketBuffer buffer)
        {
            super.encode(buffer);
        }
    }

    public static class C2SAcknowledge extends LoginIndexedMessage {
        public void encode(PacketBuffer buf) {

        }

        public static C2SAcknowledge decode(PacketBuffer buf) {
            return new C2SAcknowledge();
        }
    }

    public static class S2CRegistry extends LoginIndexedMessage {

        public S2CRegistry(final ResourceLocation key, final ForgeRegistry<? extends IForgeRegistryEntry<?>> registry) {
        }

        S2CRegistry() {
        }

        void encode(final PacketBuffer buffer) {
        }

        public static S2CRegistry decode(final PacketBuffer buffer) {
            return new S2CRegistry();
        }
    }
}
