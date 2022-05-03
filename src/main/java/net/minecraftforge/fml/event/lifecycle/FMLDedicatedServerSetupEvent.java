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
 * <em>This event is fired before construction of the dedicated server. Use {@link net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent}
 * or {@link net.minecraftforge.fml.event.server.FMLServerStartingEvent} to do stuff with the server, in both dedicated
 * and integrated server contexts</em>
 *
 * This is a parallel dispatch event.
 */
public class FMLDedicatedServerSetupEvent extends ParallelDispatchEvent
{
    public FMLDedicatedServerSetupEvent(ModContainer container)
    {
        super(container);
    }
}
