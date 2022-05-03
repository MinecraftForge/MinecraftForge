/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ModContainer;

import java.util.function.Supplier;

/**
 * This is the second of four commonly called events during mod lifecycle startup.
 *
 * Called before {@link InterModEnqueueEvent}
 * Called after {@link FMLCommonSetupEvent}
 *
 * Called on {@link net.minecraftforge.api.distmarker.Dist#CLIENT} - the game client.
 *
 * Alternative to {@link FMLDedicatedServerSetupEvent}.
 *
 * Do client only setup with this event, such as KeyBindings.
 *
 * This is a parallel dispatch event.
 */
public class FMLClientSetupEvent extends ParallelDispatchEvent
{
    private final Supplier<Minecraft> minecraftSupplier;

    public FMLClientSetupEvent(ModContainer container)
    {
        super(container);
        minecraftSupplier = Minecraft::getInstance;
    }

    public Supplier<Minecraft> getMinecraftSupplier()
    {
        return minecraftSupplier;
    }
}
