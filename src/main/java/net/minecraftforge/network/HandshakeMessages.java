/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

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
     * Server to client "list of mods". Always first handshake message.
     */
    public static class S2CModList extends LoginIndexedMessage
    {
        private List<String> mods;
        private Map<ResourceLocation, String> channels;
        private List<ResourceLocation> registries;

        public S2CModList()
        {
            this.mods = ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
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
}
