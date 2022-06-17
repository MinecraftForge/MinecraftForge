/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

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
        private final List<ResourceKey<? extends Registry<?>>> dataPackRegistries;

        public S2CModList()
        {
            this.mods = ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
            this.channels = NetworkRegistry.buildChannelVersions();
            this.registries = RegistryManager.getRegistryNamesForSyncToClient();
            this.dataPackRegistries = List.copyOf(DataPackRegistriesHooks.getSyncedCustomRegistries());
        }

        private S2CModList(List<String> mods, Map<ResourceLocation, String> channels, List<ResourceLocation> registries, List<ResourceKey<? extends Registry<?>>> dataPackRegistries)
        {
            this.mods = mods;
            this.channels = channels;
            this.registries = registries;
            this.dataPackRegistries = dataPackRegistries;
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

            List<ResourceKey<? extends Registry<?>>> dataPackRegistries = input.readCollection(ArrayList::new, buf -> ResourceKey.createRegistryKey(buf.readResourceLocation()));
            return new S2CModList(mods, channels, registries, dataPackRegistries);
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

            Set<ResourceKey<? extends Registry<?>>> dataPackRegistries = DataPackRegistriesHooks.getSyncedCustomRegistries();
            output.writeCollection(dataPackRegistries, (buf, key) -> buf.writeResourceLocation(key.location()));
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

        /**
         * @return list of ids of non-vanilla syncable datapack registries on the server.
         */
        public List<ResourceKey<? extends Registry<?>>> getCustomDataPackRegistries() {
            return this.dataPackRegistries;
        }
    }

    /**
     * Prefixes S2CModList by sending additional data about the mods installed on the server to the client
     * The mod data is stored as follows: [modId -> [modName, modVersion]]
     */
    public static class S2CModData extends LoginIndexedMessage
    {
        private final Map<String, Pair<String, String>> mods;

        public S2CModData()
        {
            this.mods = ModList.get().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, info -> Pair.of(info.getDisplayName(), info.getVersion().toString())));
        }

        private S2CModData(Map<String, Pair<String, String>> mods)
        {
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

    /**
     * Notifies the client of a channel mismatch on the server, so a {@link net.minecraftforge.client.gui.ModMismatchDisconnectedScreen} is used to notify the user of the disconnection.
     * This packet also sends the data of a channel mismatch (currently, the ids and versions of the mismatched channels) to the client for it to display the correct information in said screen.
     */
    public static class S2CChannelMismatchData extends LoginIndexedMessage
    {
        private final Map<ResourceLocation, String> mismatchedChannelData;

        public S2CChannelMismatchData(Map<ResourceLocation, String> mismatchedChannelData)
        {
            this.mismatchedChannelData = mismatchedChannelData;
        }

        public static S2CChannelMismatchData decode(FriendlyByteBuf input)
        {
            Map<ResourceLocation, String> mismatchedMods = input.readMap(i -> new ResourceLocation(i.readUtf(0x100)), i -> i.readUtf(0x100));

            return new S2CChannelMismatchData(mismatchedMods);
        }

        public void encode(FriendlyByteBuf output)
        {
            output.writeMap(mismatchedChannelData, (o, r) -> o.writeUtf(r.toString(), 0x100), (o, v) -> o.writeUtf(v, 0x100));
        }

        public Map<ResourceLocation, String> getMismatchedChannelData()
        {
            return mismatchedChannelData;
        }
    }
}
