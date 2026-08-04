/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.ModContainer;

import java.util.function.Supplier;

/**
 * This is the second of four commonly called events during mod lifecycle startup.
 *
 * Called before {@link InterModEnqueueEvent}
 * Called after {@link FMLCommonSetupEvent}
 *
 * Called on {@link net.minecraftforge.api.distmarker.Dist#DEDICATED_SERVER} - the dedicated game server.
 *
 * Alternative to {@link FMLClientSetupEvent}.
 *
 * Do dedicated server specific activities with this event.
 *
 * This is a parallel dispatch event.
 */
public class FMLDedicatedServerSetupEvent extends ModLifecycleEvent
{
    private final Supplier<DedicatedServer> serverSupplier;

    public FMLDedicatedServerSetupEvent(Supplier<DedicatedServer> server, ModContainer container)
    {
        super(container);
        this.serverSupplier = server;
    }

    public Supplier<DedicatedServer> getServerSupplier()
    {
        return serverSupplier;
    }
}
