/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Represents additional data sent by FML when a server is pinged.
 * Previous versions used the following format:
 * <pre>{@code
 * {
 *     "fmlNetworkVersion" : FMLNETVERSION,
 *     "channels": [
 *          {
 *              "res": "fml:handshake",
 *              "version": "1.2.3.4",
 *              "required": true
 *          }
 *     ],
 *     "mods": [
 *          {
 *              "modid": "modid",
 *              "modmarker": "{@literal <somestring>}"
 *          }
 *     ]
 * }
 * }</pre>
 * <p>
 * Due to size of the ping packet (32767 UTF-16 code points of JSON data) this could exceed this limit and
 * cause issues. To work around this, a truncation mechanism was introduced, to heuristically truncate the size of the
 * data, at the expense of making the compatibility info on the server screen inaccurate.
 *
 * <p>
 * Modern versions will send binary data, which is encoded in a custom format optimized for UTF-16 code point count.
 * See {@link #encodeOptimized(ByteBuf)} and {@link #decodeOptimized(String)}.
 * Essentially 15 bits of binary data are encoded into every UTF-16 code point. The resulting string is then stored in
 * the "d" property of the resulting JSON.
 *
 * <p>
 * The "channels" and "mods" properties are retained for backwards compatibility,
 * but left empty. A client that cannot read the old format would not be able to connect anyways, but the properties
 * must exist to not cause exceptions.
 *
 * <pre>{@code
 * {
 *     "fmlNetworkVersion": FMLNETVERSION,
 *     "channels": [],
 *     "mods": [],
 *     "d": "&lt;binary data&gt;"
 * }
 * }</pre>
 *
 */
public record ServerStatusPing(
        Map<ResourceLocation, ChannelData> channels,
        Map<String, String> mods,
        int fmlNetworkVer,
        boolean truncated
)
{
    private static final Codec<ByteBuf> BYTE_BUF_CODEC = Codec.STRING
            .xmap(ServerStatusPing::decodeOptimized, ServerStatusPing::encodeOptimized);

    public static final Codec<ServerStatusPing> CODEC = RecordCodecBuilder.create(in -> in.group(
            Codec.INT.fieldOf("fmlNetworkVersion").forGetter(ServerStatusPing::getFMLNetworkVersion),

            ServerStatusPing.BYTE_BUF_CODEC.optionalFieldOf("d").forGetter(ping -> Optional.of(ping.toBuf())),

            ChannelData.CODEC.listOf().optionalFieldOf("channels").forGetter(ping -> Optional.of(List.of())),
            ModInfo.CODEC.listOf().optionalFieldOf("mods").forGetter(ping -> Optional.of(List.of())),

            // legacy versions see truncated lists, modern versions ignore this truncated flag (binary data has its own)
            Codec.BOOL.optionalFieldOf("truncated").forGetter(ping -> Optional.of(ping.isTruncated()))
    ).apply(in, (fmlVer, buf, channels, mods, truncated) -> buf.map(byteBuf -> deserializeOptimized(fmlVer, byteBuf))
            .orElseGet(() -> new ServerStatusPing(
                    channels.orElseGet(List::of).stream().collect(Collectors.toMap(ChannelData::res, Function.identity())),
                    mods.orElseGet(List::of).stream().collect(Collectors.toMap(ModInfo::modId, ModInfo::modmarker)),
                    fmlVer, truncated.orElse(false)
            ))));


    public ServerStatusPing()
    {
        this(
                NetworkRegistry.buildChannelVersionsForListPing(),
                Util.make(new HashMap<>(), map -> ModList.get().forEachModContainer((modid, mc) ->
                        map.put(modid, mc.getCustomExtension(IExtensionPoint.DisplayTest.class)
                                .map(IExtensionPoint.DisplayTest::suppliedVersion)
                                .map(Supplier::get)
                                .orElse(NetworkConstants.IGNORESERVERONLY)))),
                NetworkConstants.FMLNETVERSION,
                false
        );
    }

    @Override // Don't compare the truncated flag as it is irrelevant
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ServerStatusPing that)) return false;
        return fmlNetworkVer == that.fmlNetworkVer && channels.equals(that.channels) && mods.equals(that.mods);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(channels, mods, fmlNetworkVer);
    }

    private List<Map.Entry<ResourceLocation, ChannelData>> getChannelsForMod(String modId)
    {
        return channels.entrySet().stream()
                .filter(c -> c.getKey().getNamespace().equals(modId))
                .toList();
    }

    private List<Map.Entry<ResourceLocation, ChannelData>> getNonModChannels()
    {
        return channels.entrySet().stream()
                .filter(c -> !mods.containsKey(c.getKey().getNamespace()))
                .toList();
    }

    public ByteBuf toBuf() {
        // The following techniques are used to keep the size down:
        // 1. Try and group channels by ModID, this relies on the assumption that a mod "examplemod" uses a channel
        //    like "examplemod:network". In that case only the "path" of the ResourceLocation is written
        // 2. Avoid sending IGNORESERVERONLY in plain text, instead use a flag (if set, no version string is sent)
        //
        // The size can be estimated as follows (assuming there are no non-mod network channels)
        // bytes = 2
        //          + mod_count * (avg_mod_id_length + avg_mod_version_length + 1)
        //          + (mod_count * avg_channel_count_per_mod) * (avg_mod_channel_length + avg_mod_channel_version_length + 1)
        //          + 1
        // for 600 mods with an average ModID and channel length of 20, an average channel of 1 per mod and an
        // average version length of 5 this turns out to be 31203 bytes, which easily fits into the upper limit of
        // roughly 60000 bytes. As such it is estimated that the upper limit will never be reached.
        // we still check though and potentially truncate the list
        var reachedSizeLimit = false;
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(false); // placeholder for whether we are truncating
        buf.writeShort(mods.size()); // short so that we can replace it later in case of truncation
        int writtenCount = 0;
        for (var modEntry : mods.entrySet())
        {
            var isIgnoreServerOnly = modEntry.getValue().equals(NetworkConstants.IGNORESERVERONLY);

            var channelsForMod = getChannelsForMod(modEntry.getKey());
            var channelSizeAndVersionFlag = channelsForMod.size() << 1;
            if (isIgnoreServerOnly)
            {
                channelSizeAndVersionFlag |= VERSION_FLAG_IGNORESERVERONLY;
            }
            buf.writeVarInt(channelSizeAndVersionFlag);

            buf.writeUtf(modEntry.getKey());
            if (!isIgnoreServerOnly)
            {
                buf.writeUtf(modEntry.getValue());
            }

            // write the channels for this mod, if any
            for (var entry : channelsForMod)
            {
                buf.writeUtf(entry.getKey().getPath());
                buf.writeUtf(entry.getValue().version());
                buf.writeBoolean(entry.getValue().required());
            }

            writtenCount++;

            if (buf.readableBytes() >= 60000)
            {
                reachedSizeLimit = true;
                break;
            }
        }

        if (!reachedSizeLimit)
        {
            // write any channels that don't match up with a ModID.
            var nonModChannels = getNonModChannels();
            buf.writeVarInt(nonModChannels.size());
            for (var entry : nonModChannels)
            {
                buf.writeResourceLocation(entry.getKey());
                buf.writeUtf(entry.getValue().version());
                buf.writeBoolean(entry.getValue().required());
            }
        }
        else
        {
            buf.setShort(1, writtenCount);
            buf.writeVarInt(0);
        }

        buf.setBoolean(0, reachedSizeLimit);
        return buf;
    }

    private static final int VERSION_FLAG_IGNORESERVERONLY = 0b1;

    private static ServerStatusPing deserializeOptimized(int fmlNetworkVersion, ByteBuf bbuf) {
        var buf = new FriendlyByteBuf(bbuf);
        boolean truncated;
        Map<ResourceLocation, ChannelData> channels;
        Map<String, String> mods;

        try
        {
            truncated = buf.readBoolean();
            var modsSize = buf.readUnsignedShort();
            mods = new HashMap<>();
            channels = new HashMap<>();
            for (var i = 0; i < modsSize; i++) {
                var channelSizeAndVersionFlag = buf.readVarInt();
                var channelSize = channelSizeAndVersionFlag >>> 1;
                var isIgnoreServerOnly = (channelSizeAndVersionFlag & VERSION_FLAG_IGNORESERVERONLY) != 0;
                var modId = buf.readUtf();
                var modVersion = isIgnoreServerOnly ? NetworkConstants.IGNORESERVERONLY : buf.readUtf();
                for (var i1 = 0; i1 < channelSize; i1++) {
                    var channelName = buf.readUtf();
                    var channelVersion = buf.readUtf();
                    var requiredOnClient = buf.readBoolean();
                    final ResourceLocation id = new ResourceLocation(modId, channelName);
                    channels.put(id, new ChannelData(id, channelVersion, requiredOnClient));
                }

                mods.put(modId, modVersion);
            }

            var nonModChannelCount = buf.readVarInt();
            for (var i = 0; i < nonModChannelCount; i++) {
                var channelName = buf.readResourceLocation();
                var channelVersion = buf.readUtf();
                var requiredOnClient = buf.readBoolean();
                channels.put(channelName, new ChannelData(channelName, channelVersion, requiredOnClient));
            }
        }
        finally
        {
            buf.release();
        }

        return new ServerStatusPing(channels, mods, fmlNetworkVersion, truncated);
    }

    /**
     * Encode given ByteBuf to a String. This is optimized for UTF-16 Code-Point count.
     * Supports at most 2^30 bytes in length
     */
    private static String encodeOptimized(ByteBuf buf)
    {
        var byteLength = buf.readableBytes();
        var sb = new StringBuilder();
        sb.append((char) (byteLength & 0x7FFF));
        sb.append((char) ((byteLength >>> 15) & 0x7FFF));

        int buffer = 0; // we will need at most 8 + 14 = 22 bits of buffer, so an int is enough
        int bitsInBuf = 0;
        while (buf.isReadable())
        {
            if (bitsInBuf >= 15)
            {
                char c = (char) (buffer & 0x7FFF);
                sb.append(c);
                buffer >>>= 15;
                bitsInBuf -= 15;
            }
            var b = buf.readUnsignedByte();
            buffer |= (int) b << bitsInBuf;
            bitsInBuf += 8;
        }
        buf.release();

        if (bitsInBuf > 0)
        {
            char c = (char) (buffer & 0x7FFF);
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Decode binary data encoded by {@link #encodeOptimized}
     */
    private static ByteBuf decodeOptimized(String s)
    {
        var size0 = ((int) s.charAt(0));
        var size1 = ((int) s.charAt(1));
        var size = size0 | (size1 << 15);

        var buf = Unpooled.buffer(size);

        int stringIndex = 2;
        int buffer = 0; // we will need at most 8 + 14 = 22 bits of buffer, so an int is enough
        int bitsInBuf = 0;
        while (stringIndex < s.length())
        {
            while (bitsInBuf >= 8)
            {
                buf.writeByte(buffer);
                buffer >>>= 8;
                bitsInBuf -= 8;
            }

            var c = s.charAt(stringIndex);
            buffer |= (((int) c) & 0x7FFF) << bitsInBuf;
            bitsInBuf += 15;
            stringIndex++;
        }

        // write any leftovers
        while (buf.readableBytes() < size)
        {
            buf.writeByte(buffer);
            buffer >>>= 8;
            bitsInBuf -= 8;
        }
        return buf;
    }

    public Map<ResourceLocation, ChannelData> getRemoteChannels()
    {
        return this.channels;
    }

    public Map<String,String> getRemoteModData()
    {
        return mods;
    }

    public int getFMLNetworkVersion()
    {
        return fmlNetworkVer;
    }

    public boolean isTruncated()
    {
        return truncated;
    }

    public record ModInfo(String modId, String modmarker) {
        public static final Codec<ModInfo> CODEC = RecordCodecBuilder.create(in -> in.group(
                Codec.STRING.fieldOf("modId").forGetter(ModInfo::modId),
                Codec.STRING.fieldOf("modmarker").forGetter(ModInfo::modmarker)
        ).apply(in, ModInfo::new));
    }

    public record ChannelData(ResourceLocation res, String version, boolean required) {
        public static final Codec<ChannelData> CODEC = RecordCodecBuilder.create(in -> in.group(
                ResourceLocation.CODEC.fieldOf("res").forGetter(ChannelData::res),
                Codec.STRING.fieldOf("version").forGetter(ChannelData::version),
                Codec.BOOL.fieldOf("required").forGetter(ChannelData::required)
        ).apply(in, ChannelData::new));
    }
}
