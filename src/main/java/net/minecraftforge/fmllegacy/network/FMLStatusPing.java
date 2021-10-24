/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fmllegacy.network;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * {
 *      "fmlNetworkVersion" : FMLNETVERSION,
 *      "channels": [
 *          {
 *              "res": "fml:handshake",
 *              "version": "1.2.3.4",
 *              "required": true
 *          }
 *     ],
 *     "mods": [
 *          {
 *              "modid": "modid",
 *              "modmarker": "<somestring>"
 *          }
 *     ]
 * }
 *
 */
public class FMLStatusPing {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CHANNEL_TRUNCATE_LIMIT = 150;
    private static final int MOD_TRUNCATE_LIMIT = 150;
    private static volatile boolean warnedAboutTruncation = false;

    private transient Map<ResourceLocation, Pair<String, Boolean>> channels;
    private transient Map<String, String> mods;
    private transient int fmlNetworkVer;
    private transient boolean truncated;

    public FMLStatusPing() {
        this.channels = NetworkRegistry.buildChannelVersionsForListPing();
        this.mods = new HashMap<>();
        ModList.get().forEachModContainer((modid, mc) ->
                    mods.put(modid, mc.getCustomExtension(IExtensionPoint.DisplayTest.class)
                            .map(IExtensionPoint.DisplayTest::suppliedVersion)
                            .map(Supplier::get)
                            .orElse(FMLNetworkConstants.IGNORESERVERONLY)));
        this.fmlNetworkVer = FMLNetworkConstants.FMLNETVERSION;
        this.truncated = false;
    }

    private FMLStatusPing(Map<ResourceLocation, Pair<String, Boolean>> deserialized, Map<String,String> modMarkers, int fmlNetVer, boolean truncated) {
        this.channels = ImmutableMap.copyOf(deserialized);
        this.mods = modMarkers;
        this.fmlNetworkVer = fmlNetVer;
        this.truncated = truncated;
    }

    public static class Serializer {
        public static FMLStatusPing deserialize(JsonObject forgeData, JsonDeserializationContext ctx) {
            try {
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
                return new FMLStatusPing(channels, mods, remoteFMLVersion, truncated);
            } catch (JsonSyntaxException e) {
                LOGGER.debug(FMLNetworkConstants.NETWORK, "Encountered an error parsing status ping data", e);
                return null;
            }
        }

        public static JsonObject serialize(FMLStatusPing forgeData, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            JsonArray channels = new JsonArray();
            boolean truncated = forgeData.channels.size() > CHANNEL_TRUNCATE_LIMIT || forgeData.mods.size() > MOD_TRUNCATE_LIMIT;
            if (truncated && !warnedAboutTruncation)
            {
                warnedAboutTruncation = true;
                LOGGER.warn("Heuristically truncating mod and/or network channel list in server status ping packet. Compatibility report " +
                        "in the multiplayer screen may be inaccurate.");
            }

            forgeData.channels.entrySet().stream().limit(CHANNEL_TRUNCATE_LIMIT).forEach(entry -> {
                ResourceLocation namespace = entry.getKey();
                Pair<String, Boolean> version = entry.getValue();
                JsonObject mi = new JsonObject();
                mi.addProperty("res", namespace.toString());
                mi.addProperty("version", version.getLeft());
                mi.addProperty("required", version.getRight());
                channels.add(mi);
            });

            obj.add("channels", channels);

            JsonArray modTestValues = new JsonArray();
            forgeData.mods.entrySet().stream().limit(MOD_TRUNCATE_LIMIT).forEach(entry -> {
                String modId = entry.getKey();
                String value = entry.getValue();
                JsonObject mi = new JsonObject();
                mi.addProperty("modId", modId);
                mi.addProperty("modmarker", value);
                modTestValues.add(mi);
            });
            obj.add("mods", modTestValues);
            obj.addProperty("fmlNetworkVersion", forgeData.fmlNetworkVer);
            obj.addProperty("truncated", truncated);
            return obj;
        }

        public static JsonObject serializeOptimized(FMLStatusPing forgeData, JsonSerializationContext ctx)
        {
            var buf = new FriendlyByteBuf(Unpooled.buffer());


            // what counts here is UTF-16 code-point count, because of how writeUtf counts characters
            var sb = new StringBuilder();
            sb.append(((char) forgeData.mods.size())); // this is good for up to 0xd7ff == 55295 mods and only takes 1 codepoint



            var obj = new JsonObject();
            obj.addProperty("fmlNetworkVersion", forgeData.fmlNetworkVer);
            obj.addProperty("d", sb.toString());
            return obj;
        }
    }

    public Map<ResourceLocation, Pair<String, Boolean>> getRemoteChannels() {
        return this.channels;
    }

    public Map<String,String> getRemoteModData() {
        return mods;
    }

    public int getFMLNetworkVersion() {
        return fmlNetworkVer;
    }

    public boolean isTruncated()
    {
        return truncated;
    }

    /**
     * Encode given ByteBuf to a String. This is optimized for UTF-16 Code-Point count.
     * Supports at most 2^15 bytes in length
     */
    private static String encodeOptimized(ByteBuf buf)
    {
        var byteLength = buf.readableBytes();
        var sb = new StringBuilder();
        sb.append((char) byteLength);

        var buffer = 0; // we will need at most 8 + 14 = 22 bits of buffer
        int bitsInBuf = 0;
        while (buf.isReadable()) {
            if (bitsInBuf >= 15)
            {
                char c = (char) (buffer & 0x7FFF);
                sb.append(c);
                buffer >>>= 15;
                bitsInBuf -= 15;
            }
            var b = buf.readByte();
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
        var size = ((int) s.charAt(0));
        var buf = Unpooled.buffer(size);

        int stringIndex = 1;
        var buffer = 0; // we will need at most 8 + 14 = 22 bits of buffer
        int bitsInBuf = 0;
        while (stringIndex < s.length()) {
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

        while (buf.readableBytes() < size) {
            buf.writeByte(buffer);
            buffer >>>= 8;
            bitsInBuf -= 8;

        }

        return buf;
    }

    public static void main(String[] args) {
        var bytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            bytes[i] = (byte) i;
        }
        var encoded = encodeOptimized(Unpooled.wrappedBuffer(bytes));
        var decoded = decodeOptimized(encoded);

        System.out.println("bytes: " + bytes.length);
        System.out.println("encod: " + encoded.length());
        System.out.println("decod: " + decoded.readableBytes());
        System.out.println("encod: " + encoded.codePoints().mapToObj(Integer::toHexString).collect(Collectors.joining(", ")));
        System.out.println("encod: " + encoded);

        System.out.println(ByteBufUtil.hexDump(Unpooled.wrappedBuffer(bytes)));
        System.out.println(ByteBufUtil.hexDump(decoded));
    }

}
