/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmllegacy;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public enum LogicalSidedProvider
{
    WORKQUEUE((c)->c.get(), (s)->s.get()),
    INSTANCE((c)->c.get(), (s)->s.get()),
    CLIENTWORLD((c)-> Optional.<Level>of(c.get().level), (s)->Optional.<Level>empty())
    ;
    private static Supplier<Minecraft> client;
    private static Supplier<MinecraftServer> server;

    LogicalSidedProvider(Function<Supplier<Minecraft>, ?> clientSide, Function<Supplier<MinecraftServer>, ?> serverSide)
    {
        this.clientSide = clientSide;
        this.serverSide = serverSide;
    }

    public static void setClient(Supplier<Minecraft> client)
    {
        LogicalSidedProvider.client = client;
    }
    public static void setServer(Supplier<MinecraftServer> server)
    {
        LogicalSidedProvider.server = server;
    }


    private final Function<Supplier<Minecraft>, ?> clientSide;
    private final Function<Supplier<MinecraftServer>, ?> serverSide;

    @SuppressWarnings("unchecked")
    public <T> T get(final LogicalSide side) {
        return (T)(side==LogicalSide.CLIENT ? clientSide.apply(client) : serverSide.apply(server));
    }
}
