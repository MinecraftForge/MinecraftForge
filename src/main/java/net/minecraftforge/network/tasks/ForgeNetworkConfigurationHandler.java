/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.tasks;

import org.jetbrains.annotations.ApiStatus;

import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.NetworkContext;

@ApiStatus.Internal
public class ForgeNetworkConfigurationHandler {
    @SubscribeEvent
    public void gatherInit(GatherLoginConfigurationTasksEvent event) {
        var ctx = NetworkContext.get(event.getConnection());
        if (ctx.getType() != ConnectionType.MODDED)
            return;

        // Send minecraft:register packet for all known channels. This allows the other side to know what we can talk about.
        event.addTask(new RegisterChannelsTask());
        // Send the mod version and display name to the client so that it can check for compatibility, Currently doesn't really do anything
        // We expect a response from the client
        event.addTask(new ModVersionsTask());
        // Send the channel list to the client, which is checked on the client to make sure it knows how to talk to the server.
        // We expect a Response from the client
        event.addTask(new ChannelVersionsTask());
        // Sync all of our registry mappings
        event.addTask(new SyncRegistriesTask());
        // Lastly sync raw mod config files. We send raw file bytes, which is very inefficient, but we can change it later. //TODO: Compress/Optimize config file data
        event.addTask(new SyncConfigTask());
    }
}
