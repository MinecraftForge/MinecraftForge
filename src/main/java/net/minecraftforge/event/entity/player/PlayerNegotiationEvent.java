/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * This event is fired when a connection has started the Forge Handshake.<br>
 * Forge will wait for all enqueued work to be completed before proceeding further with the login process.
 */
public class PlayerNegotiationEvent extends Event
{

    private final Connection connection;
    private final GameProfile profile;
    private final List<Future<Void>> futures;

    public PlayerNegotiationEvent(Connection connection, GameProfile profile, List<Future<Void>> futures)
    {
        this.connection = connection;
        this.profile = profile;
        this.futures = futures;
    }

    /**
     * Enqueue work to be completed asynchronously before the login proceeds.
     */
    public void enqueueWork(Runnable runnable)
    {
        enqueueWork(CompletableFuture.runAsync(runnable));
    }

    /**
     * Enqueue work to be completed asynchronously before the login proceeds.
     */
    public void enqueueWork(Future<Void> future)
    {
        futures.add(future);
    }

    public Connection getConnection()
    {
        return connection;
    }

    public GameProfile getProfile()
    {
        return profile;
    }
}
