/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * This event is fired on the server when a connection has started the Forge handshake,
 * Forge will wait for all enqueued work to be completed before proceeding further with the login process.
 * <br>
 * This event can be used to delay the player login until any necessary work such as preloading user data has completed.
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
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
