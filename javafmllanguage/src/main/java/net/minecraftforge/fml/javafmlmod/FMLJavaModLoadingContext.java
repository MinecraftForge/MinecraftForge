/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Use {@link net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent}
 * instead in your mod's constructor to get access to its EventBus.
 */
@Deprecated(forRemoval = true, since = "1.21.1")
public class FMLJavaModLoadingContext
{
    private final FMLModContainer container;

    FMLJavaModLoadingContext(FMLModContainer container) {
        this.container = container;
    }

    /**
     * @return The mod's event bus, to allow subscription to Mod specific events
     */
    public IEventBus getModEventBus()
    {
        return container.getModEventBus();
    }


    /**
     * Helper to get the right instance from the {@link ModLoadingContext} correctly.
     * @return The FMLJavaMod language specific extension from the ModLoadingContext
     */
    public static FMLJavaModLoadingContext get() {
        return ModLoadingContext.get().extension();
    }
}
