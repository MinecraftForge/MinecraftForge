/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
 * See {@link Serializer#encodeOptimized(ByteBuf)} and {@link Serializer#decodeOptimized(String)}.
 * Essentially 15 bits of binary data are encoded into every UTF-16 code point. The resulting string is then stored in
 * the "d" property of the resulting JSON.
 *
 * <p>
 * For the format of the binary data see {@link Serializer#serialize(ServerStatusPing)}.
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
public class ServerStatusPing
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final transient Map<ResourceLocation, Pair<String, Boolean>> channels;
    private final transient Map<String, String> mods;
    private final transient int fmlNetworkVer;
    private final transient boolean truncated;

    public ServerStatusPing()
    {
        this.channels = NetworkRegistry.buildChannelVersionsForListPing();
        this.mods = new HashMap<>();
        ModList.get().forEachModContainer((modid, mc) ->
                    mods.put(modid, mc.getCustomExtension(IExtensionPoint.DisplayTest.class)
                            .map(IExtensionPoint.DisplayTest::suppliedVersion)
                            .map(Supplier::get)
                            .orElse(NetworkConstants.IGNORESERVERONLY)));
        this.fmlNetworkVer = NetworkConstants.FMLNETVERSION;
        this.truncated = false;
    }

    private ServerStatusPing(Map<ResourceLocation, Pair<String, Boolean>> deserialized, Map<String,String> modMarkers, int fmlNetVer, boolean truncated) {
        this.channels = ImmutableMap.copyOf(deserialized);
        this.mods = modMarkers;
        this.fmlNetworkVer = fmlNetVer;
        this.truncated = truncated;
    }

    @Override
    public String toString()
    {
        return "FMLStatusPing{" +
                "channels=" + channels +
                ", mods=" + mods +
                ", fmlNetworkVer="
                + fmlNetworkVer +
                ", truncated=" + truncated +
                '}';
    }

    @Override
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

    private List<Map.Entry<ResourceLocation, Pair<String, Boolean>>> getChannelsForMod(String modId)
    {
        return channels.entrySet().stream()
                .filter(c -> c.getKey().getNamespace().equals(modId))
                .toList();
    }

    private List<Map.Entry<ResourceLocation, Pair<String, Boolean>>> getNonModChannels()
    {
        return channels.entrySet().stream()
                .filter(c -> !mods.containsKey(c.getKey().getNamespace()))
                .toList();
    }

    public static class Serializer
    {
        public static ServerStatusPing deserialize(JsonObject forgeData)
        {
            try
            {
                if (forgeData.has("d"))
                {
                    return deserializeOptimized(forgeData);
                }
                final Map<ResourceLocation, Pair<String, Boolean>> channels = StreamSupport.stream(GsonHelper.getAsJsonArray(forgeData, "channels").spliterator(), false).
                        map(JsonElement::getAsJsonObject).
                        collect(Collectors.toMap(jo -> new ResourceLocation(GsonHelper.getAsString(jo, "res")),
                                jo -> Pair.of(GsonHelper.getAsString(jo, "version"), GsonHelper.getAsBoolean(jo, "required")))
                        );

                final Map<String, String> mods = StreamSupport.stream(GsonHelper.getAsJsonArray(forgeData, "mods").spliterator(), false).
                        map(JsonElement::getAsJsonObject).
                        collect(Collectors.toMap(jo -> GsonHelper.getAsString(jo, "modId"), jo->GsonHelper.getAsString(jo, "modmarker")));

                final int remoteFMLVersion = GsonHelper.getAsInt(forgeData, "fmlNetworkVersion");
                final boolean truncated = GsonHelper.getAsBoolean(forgeData, "truncated", false);
                return new ServerStatusPing(channels, mods, remoteFMLVersion, truncated);
            }
            catch (JsonSyntaxException | IndexOutOfBoundsException | ResourceLocationException e)
            {
                LOGGER.debug(NetworkConstants.NETWORK, "Encountered an error parsing status ping data", e);
                return null;
            }
        }


        public static JsonObject serialize(ServerStatusPing forgeData)
        {
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
            buf.writeShort(forgeData.mods.size()); // short so that we can replace it later in case of truncation
            int writtenCount = 0;
            for (var modEntry : forgeData.mods.entrySet())
            {
                var isIgnoreServerOnly = modEntry.getValue().equals(NetworkConstants.IGNORESERVERONLY);

                var channelsForMod = forgeData.getChannelsForMod(modEntry.getKey());
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
                    buf.writeUtf(entry.getValue().getLeft());
                    buf.writeBoolean(entry.getValue().getRight());
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
                var nonModChannels = forgeData.getNonModChannels();
                buf.writeVarInt(nonModChannels.size());
                for (var entry : nonModChannels)
                {
                    buf.writeResourceLocation(entry.getKey());
                    buf.writeUtf(entry.getValue().getLeft());
                    buf.writeBoolean(entry.getValue().getRight());
                }
            }
            else
            {
                buf.setShort(1, writtenCount);
                buf.writeVarInt(0);
            }

            buf.setBoolean(0, reachedSizeLimit);

            var obj = new JsonObject();
            obj.addProperty("fmlNetworkVersion", forgeData.fmlNetworkVer);
            obj.addProperty("d", encodeOptimized(buf));

            // add dummy properties, so old versions do not crash when deserializing
            obj.add("channels", new JsonArray());
            obj.add("mods", new JsonArray());
            // legacy versions see truncated lists, modern versions ignore this truncated flag (binary data has its own)
            obj.addProperty("truncated", true);
            return obj;
        }

        private static final int VERSION_FLAG_IGNORESERVERONLY = 0b1;

        private static ServerStatusPing deserializeOptimized(JsonObject forgeData)
        {
            int remoteFMLVersion = GsonHelper.getAsInt(forgeData, "fmlNetworkVersion");
            var buf = new FriendlyByteBuf(decodeOptimized(GsonHelper.getAsString(forgeData, "d")));

            boolean truncated;
            Map<ResourceLocation, Pair<String, Boolean>> channels;
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
                        channels.put(new ResourceLocation(modId, channelName), Pair.of(channelVersion, requiredOnClient));
                    }

                    mods.put(modId, modVersion);
                }

                var nonModChannelCount = buf.readVarInt();
                for (var i = 0; i < nonModChannelCount; i++) {
                    var channelName = buf.readResourceLocation();
                    var channelVersion = buf.readUtf();
                    var requiredOnClient = buf.readBoolean();
                    channels.put(channelName, Pair.of(channelVersion, requiredOnClient));
                }
            }
            finally
            {
                buf.release();
            }

            return new ServerStatusPing(channels, mods, remoteFMLVersion, truncated);
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
    }

    public Map<ResourceLocation, Pair<String, Boolean>> getRemoteChannels()
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
}
