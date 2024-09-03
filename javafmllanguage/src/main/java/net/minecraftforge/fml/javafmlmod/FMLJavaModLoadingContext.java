/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;

public class FMLJavaModLoadingContext extends ModLoadingContext
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
        return container.getEventBus();
    }

    @Override
    public FMLModContainer getContainer() {
        return container;
    }

    /**
     * Helper to get the right instance from the {@link ModLoadingContext} correctly.
     * @return The FMLJavaMod language specific extension from the ModLoadingContext
     *
     * @deprecated use {@link FMLJavaModLoadingContext} in your mod constructor
     */
    @Deprecated(forRemoval = true, since = "1.21.1")
    public static FMLJavaModLoadingContext get() {
        return ModLoadingContext.get().extension();
    }
}
