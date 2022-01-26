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

package net.minecraftforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

public class HandshakeMessages
{
    static class LoginIndexedMessage implements IntSupplier
    {
        private int loginIndex;

        void setLoginIndex(final int loginIndex) {
            this.loginIndex = loginIndex;
        }

        int getLoginIndex() {
            return loginIndex;
        }

        @Override
        public int getAsInt() {
            return getLoginIndex();
        }
    }
    /**
     * Server to client "list of mods". Always first handshake message after the data sent by S2CModData.
     */
    public static class S2CModList extends LoginIndexedMessage
    {
        private List<String> mods;
        private Map<ResourceLocation, String> channels;
        private List<ResourceLocation> registries;

        public S2CModList()
        {
            this.mods = ModList.get().getMods().stream().map(mod -> mod.getModId()).collect(Collectors.toList());
            this.channels = NetworkRegistry.buildChannelVersions();
            this.registries = RegistryManager.getRegistryNamesForSyncToClient();
        }

        private S2CModList(List<String> mods, Map<ResourceLocation, String> channels, List<ResourceLocation> registries)
        {
            this.mods = mods;
            this.channels = channels;
            this.registries = registries;
        }

        public static S2CModList decode(FriendlyByteBuf input)
        {
            List<String> mods = new ArrayList<>();
            int len = input.readVarInt();
            for (int x = 0; x < len; x++)
                mods.add(input.readUtf(0x100));

            Map<ResourceLocation, String> channels = new HashMap<>();
            len = input.readVarInt();
            for (int x = 0; x < len; x++)
                channels.put(input.readResourceLocation(), input.readUtf(0x100));

            List<ResourceLocation> registries = new ArrayList<>();
            len = input.readVarInt();
            for (int x = 0; x < len; x++)
                registries.add(input.readResourceLocation());

            return new S2CModList(mods, channels, registries);
        }

        public void encode(FriendlyByteBuf output)
        {
            output.writeVarInt(mods.size());
            mods.forEach(m -> output.writeUtf(m, 0x100));

            output.writeVarInt(channels.size());
            channels.forEach((k, v) -> {
                output.writeResourceLocation(k);
                output.writeUtf(v, 0x100);
            });

            output.writeVarInt(registries.size());
            registries.forEach(output::writeResourceLocation);
        }

        public List<String> getModList() {
            return mods;
        }

        public List<ResourceLocation> getRegistries() {
            return this.registries;
        }

        public Map<ResourceLocation, String> getChannels() {
            return this.channels;
        }
    }

    /**
     * Prefixes S2CModList by sending additional data about the mods installed on the server to the client
     * The mod data is stored as follows: [modId -> [modName, modVersion]]
     */
    public static class S2CModData extends LoginIndexedMessage implements INoResponse
    {
        private final Map<String, Pair<String, String>> mods;

        public S2CModData()
        {
            this.mods = ModList.get().getMods().stream().map(info -> Pair.of(info.getModId(), Pair.of(info.getDisplayName(), info.getVersion().toString()))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }

        private S2CModData(Map<String, Pair<String, String>> mods) {
            this.mods = mods;
        }

        public static S2CModData decode(FriendlyByteBuf input)
        {
            Map<String, Pair<String, String>> mods = input.readMap(o -> o.readUtf(0x100), o -> Pair.of(o.readUtf(0x100), o.readUtf(0x100)));
            return new S2CModData(mods);
        }

        public void encode(FriendlyByteBuf output)
        {
            output.writeMap(mods, (o, s) -> o.writeUtf(s, 0x100), (o, p) -> {
                o.writeUtf(p.getLeft(), 0x100);
                o.writeUtf(p.getRight(), 0x100);
            });
        }

        public Map<String, Pair<String, String>> getMods()
        {
            return mods;
        }
    }

    public static class C2SModListReply extends LoginIndexedMessage
    {
        private List<String> mods;
        private Map<ResourceLocation, String> channels;
        private Map<ResourceLocation, String> registries;

        public C2SModListReply()
        {
            this.mods = ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
            this.channels = NetworkRegistry.buildChannelVersions();
            this.registries = Maps.newHashMap(); //TODO: Fill with known hashes, which requires keeping a file cache
        }

        private C2SModListReply(List<String> mods, Map<ResourceLocation, String> channels, Map<ResourceLocation, String> registries)
        {
            this.mods = mods;
            this.channels = channels;
            this.registries = registries;
        }

        public static C2SModListReply decode(FriendlyByteBuf input)
        {
            List<String> mods = new ArrayList<>();
            int len = input.readVarInt();
            for (int x = 0; x < len; x++)
                mods.add(input.readUtf(0x100));

            Map<ResourceLocation, String> channels = new HashMap<>();
            len = input.readVarInt();
            for (int x = 0; x < len; x++)
                channels.put(input.readResourceLocation(), input.readUtf(0x100));

            Map<ResourceLocation, String> registries = new HashMap<>();
            len = input.readVarInt();
            for (int x = 0; x < len; x++)
                registries.put(input.readResourceLocation(), input.readUtf(0x100));

            return new C2SModListReply(mods, channels, registries);
        }

        public void encode(FriendlyByteBuf output)
        {
            output.writeVarInt(mods.size());
            mods.forEach(m -> output.writeUtf(m, 0x100));

            output.writeVarInt(channels.size());
            channels.forEach((k, v) -> {
                output.writeResourceLocation(k);
                output.writeUtf(v, 0x100);
            });

            output.writeVarInt(registries.size());
            registries.forEach((k, v) -> {
                output.writeResourceLocation(k);
                output.writeUtf(v, 0x100);
            });
        }

        public List<String> getModList() {
            return mods;
        }

        public Map<ResourceLocation, String> getRegistries() {
            return this.registries;
        }

        public Map<ResourceLocation, String> getChannels() {
            return this.channels;
        }
    }

    public static class C2SAcknowledge extends LoginIndexedMessage {
        public void encode(FriendlyByteBuf buf) {

        }

        public static C2SAcknowledge decode(FriendlyByteBuf buf) {
            return new C2SAcknowledge();
        }
    }

    public static class S2CRegistry extends LoginIndexedMessage {
        private ResourceLocation registryName;
        @Nullable
        private ForgeRegistry.Snapshot snapshot;

        public S2CRegistry(final ResourceLocation name, @Nullable ForgeRegistry.Snapshot snapshot) {
            this.registryName = name;
            this.snapshot = snapshot;
        }

        void encode(final FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(registryName);
            buffer.writeBoolean(hasSnapshot());
            if (hasSnapshot())
                buffer.writeBytes(snapshot.getPacketData());
        }

        public static S2CRegistry decode(final FriendlyByteBuf buffer) {
            ResourceLocation name = buffer.readResourceLocation();
            ForgeRegistry.Snapshot snapshot = null;
            if (buffer.readBoolean())
                snapshot = ForgeRegistry.Snapshot.read(buffer);
            return new S2CRegistry(name, snapshot);
        }

        public ResourceLocation getRegistryName() {
            return registryName;
        }

        public boolean hasSnapshot() {
            return snapshot != null;
        }

        @Nullable
        public ForgeRegistry.Snapshot getSnapshot() {
            return snapshot;
        }
    }


    public static class S2CConfigData extends LoginIndexedMessage {
        private final String fileName;
        private final byte[] fileData;

        public S2CConfigData(final String configFileName, final byte[] configFileData) {
            this.fileName = configFileName;
            this.fileData = configFileData;
        }

        void encode(final FriendlyByteBuf buffer) {
            buffer.writeUtf(this.fileName);
            buffer.writeByteArray(this.fileData);
        }

        public static S2CConfigData decode(final FriendlyByteBuf buffer) {
            return new S2CConfigData(buffer.readUtf(32767), buffer.readByteArray());
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getBytes() {
            return fileData;
        }
    }

    public static class S2CModMismatchData extends LoginIndexedMessage {
        private final Map<ResourceLocation, String> mismatchedChannelData;
        private final Map<ResourceLocation, Pair<String, String>> presentChannelData;

        public S2CModMismatchData(Map<ResourceLocation, String> mismatchedChannelData, Map<ResourceLocation, Pair<String, String>> presentChannelData)
        {
            this.mismatchedChannelData = mismatchedChannelData;
            this.presentChannelData = presentChannelData;
        }

        public static S2CModMismatchData decode(FriendlyByteBuf input)
        {
            Map<ResourceLocation, String> mismatchedMods = input.readMap(i -> new ResourceLocation(i.readUtf(0x100)), i -> i.readUtf(0x100));
            Map<ResourceLocation, Pair<String, String>> presentMods = input.readMap(i -> new ResourceLocation(i.readUtf(0x100)), i -> Pair.of(i.readUtf(0x100), i.readUtf(0x100)));

            return new S2CModMismatchData(mismatchedMods, presentMods);
        }

        public void encode(FriendlyByteBuf output)
        {
            output.writeMap(mismatchedChannelData, (o, r) -> o.writeUtf(r.toString(), 0x100), (o, v) -> o.writeUtf(v, 0x100));
            output.writeMap(presentChannelData, (o, r) -> o.writeUtf(r.toString(), 0x100), (o, p) -> {
                o.writeUtf(p.getLeft(), 0x100);
                o.writeUtf(p.getRight(), 0x100);
            });
        }

        public Map<ResourceLocation, String> getMismatchedChannelData()
        {
            return mismatchedChannelData;
        }

        public Map<ResourceLocation, Pair<String, String>> getPresentChannelData()
        {
            return presentChannelData;
        }
    }

    /**
     * Marker interface for all server -> client packets that the server should not await a response from the client for
     */

    public interface INoResponse {
    }
}
