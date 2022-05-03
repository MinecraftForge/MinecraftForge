/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;

/**
 * This is the third of four commonly called events during mod lifecycle startup.
 *
 * Called before {@link InterModProcessEvent}
 * Called after {@link FMLClientSetupEvent} or {@link FMLDedicatedServerSetupEvent}
 *
 *
 * Enqueue {@link net.minecraftforge.fml.InterModComms} messages to other mods with this event.
 *
 * This is a parallel dispatch event.
 */
public class InterModEnqueueEvent extends ParallelDispatchEvent
{

    public InterModEnqueueEvent(final ModContainer container)
    {
        super(container);
    }
}
