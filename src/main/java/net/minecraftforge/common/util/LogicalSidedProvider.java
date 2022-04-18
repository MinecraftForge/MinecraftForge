/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class LogicalSidedProvider<T>
{
    public static final LogicalSidedProvider<BlockableEventLoop<? super TickTask>> WORKQUEUE = new LogicalSidedProvider<>(Supplier::get, Supplier::get);
    public static final LogicalSidedProvider<Optional<Level>> CLIENTWORLD = new LogicalSidedProvider<>((c)-> Optional.of(c.get().level), (s)->Optional.empty());

    private static Supplier<Minecraft> client;
    private static Supplier<MinecraftServer> server;

    // INTERNAL, DO  NOT CALL
    public static void setClient(Supplier<Minecraft> client)
    {
        LogicalSidedProvider.client = client;
    }
    public static void setServer(Supplier<MinecraftServer> server)
    {
        LogicalSidedProvider.server = server;
    }

    private LogicalSidedProvider(Function<Supplier<Minecraft>, T> clientSide, Function<Supplier<MinecraftServer>, T> serverSide)
    {
        this.clientSide = clientSide;
        this.serverSide = serverSide;
    }

    private final Function<Supplier<Minecraft>, T> clientSide;
    private final Function<Supplier<MinecraftServer>, T> serverSide;

    public T get(final LogicalSide side) {
        return side==LogicalSide.CLIENT ? clientSide.apply(client) : serverSide.apply(server);
    }
}
