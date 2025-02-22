/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Use the context provided by your language loader in your mod's constructor
 */
public class FMLJavaModLoadingContext extends ModLoadingContext {
    private final FMLModContainer container;

    FMLJavaModLoadingContext(FMLModContainer container) {
        this.container = container;
    }

    public BusGroup getModBusGroup() {
        return container.getBusGroup();
    }

    /**
     * @return {@link FMLModContainer} by default.
     */
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
    @Deprecated(forRemoval = true, since="1.21.1")
    public static FMLJavaModLoadingContext get() {
        return ModLoadingContext.get().extension();
    }
}
