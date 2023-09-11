/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;

import io.netty.util.AttributeKey;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraftforge.network.packets.ModVersions;

public class NetworkContext {
    private static final String MARKER = "FORGE";
    public static final int NET_VERSION = 0;
    private static final AttributeKey<NetworkContext> CHANNEL_KEY = AttributeKey.valueOf("forge:netcontext");

    /**
     * Gets the associated NetworkContext from the connection, creating a new instance if necessary.
     */
    public static NetworkContext get(Connection connection) {
        var attr = connection.channel().attr(CHANNEL_KEY);
        var ret = attr.get();
        if (ret == null) {
            synchronized(connection.channel()) {
                ret = attr.get();
                if (ret == null) {
                    ret = new NetworkContext(connection);
                    attr.set(ret);
                }
            }
        }
        return ret;
    }

    private final Connection connection;
    private ConnectionType type = ConnectionType.VANILLA;
    private int netVersion = 0;

    private NetworkContext(Connection connection) {
        this.connection = connection;
    }

    public ConnectionType getType() {
        return this.type;
    }

    public int getNetVersion() {
        return this.netVersion;
    }

    public Set<ResourceLocation> getRemoteChannels() {
        return this.remoteChannelsView;
    }

    /**
     * A list of mods and their verisons reported by the other side of the connection.
     * The remote side can and does lie about this, so not rely on it for any anti-cheat system.
     */
    public Map<String, ModVersions.Info> getModList() {
        return this.modListView;
    }

    /* ===================================================================================
     *                           INTERNAL BELOW THIS
     * ===================================================================================
     */

    /**
     * This is a hint that we are a modded client, This is sent in the first packet the client sends to the server.
     * Unfortunately we do not have a modder facing value in this packet, so we have a long standing tradition of
     * encoding it on the hostName field, which is a UTF string.
     * Forge treats this string a null terminated list. With our value being anywhere in the list.
     * Our identifier is currently FORGE, Optionally followed by a number indicating the network version.
     * If the version is missing, we assume 0.
     *
     * TODO: Move out of the Intention packet and into a immediately sent Login custom payload? Which should arrive before we get to config stage?
     */
    @ApiStatus.Internal
    public static String enhanceHostName(String hostName) {
        return String.join("\0", hostName, MARKER);
    }

    @ApiStatus.Internal
    public void processIntention(String hostName) {
        this.type = ConnectionType.VANILLA;
        this.netVersion = 0;

        int idx = hostName.indexOf('\0');
        if (idx != -1) {
            for (var pt : hostName.split("\0")) {
                if (pt.startsWith(MARKER)) {
                    this.type = ConnectionType.MODDED;
                    if (pt.length() > MARKER.length())
                        this.netVersion = Integer.valueOf(pt.substring(MARKER.length()));
                }
            }
        }
    }

    @ApiStatus.Internal
    Set<ResourceLocation> remoteChannels = new HashSet<>();
    private Set<ResourceLocation> remoteChannelsView = Collections.unmodifiableSet(this.remoteChannels);

    @ApiStatus.Internal
    Set<ResourceLocation> sentChannels = new HashSet<>();

    @ApiStatus.Internal
    Map<String, ModVersions.Info> modList = new HashMap<>();
    private Map<String, ModVersions.Info> modListView = Collections.unmodifiableMap(modList);

    @ApiStatus.Internal
    Map<ResourceLocation, Integer> channelList = new Object2IntOpenHashMap<>();

    // TODO Make this official API? This is to allow us to finish a task in the packet handlers and not the task function itself.
    @ApiStatus.Internal
    public void finishTask(ConfigurationTask.Type task) {
        if (this.connection.getPacketListener() instanceof ServerConfigurationPacketListenerImpl cfg)
            cfg.finishCurrentTask(task);
        else
            throw new IllegalStateException("Attempted to finish task " + task + " when packet listener was " + this.connection.getPacketListener().getClass().getName());
    }

    @ApiStatus.Internal
    NetworkMismatchData mismatchData = null;

    @ApiStatus.Internal
    public NetworkMismatchData getMismatchs() {
        return mismatchData;
    }

    @ApiStatus.Internal
    void setConnectionType(ConnectionType type) {
        this.type = type;
    }

    @ApiStatus.Internal
    public record NetworkMismatchData(
        Map<ResourceLocation, Version> mismatched,
        Set<ResourceLocation> missing,
        boolean fromServer,
        Map<String, ModVersions.Info> mods
    ) {
        public record Version(String received, String had) {}
    }
}
