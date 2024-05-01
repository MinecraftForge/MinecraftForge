/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;

/**
 * This is the second of four commonly called events during mod core startup.
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
 * <em>This event is fired before construction of the dedicated server. Use {@code FMLServerAboutToStartEvent}
 * or {@code FMLServerStartingEvent} to do stuff with the server, in both dedicated
 * and integrated server contexts</em>
 *
 * This is a parallel dispatch event.
 */
public class FMLDedicatedServerSetupEvent extends ParallelDispatchEvent
{
    public FMLDedicatedServerSetupEvent(ModContainer container, ModLoadingStage stage)
    {
        super(container, stage);
    }
}
