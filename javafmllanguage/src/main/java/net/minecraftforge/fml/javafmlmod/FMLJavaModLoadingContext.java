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

    /**
     * @return The mod's event bus group, to allow subscription to Mod specific events
     */
    public BusGroup getModBusGroup() {
        return container.getModBusGroup();
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
     * @deprecated use {@link FMLJavaModLoadingContext} in your mod constructor. For example: {@snippet :
     * @Mod("examplemod")
     * public final class ExampleMod {
     *     public ExampleMod(FMLJavaModLoadingContext context) {
     *          // Use context from the param instead of FMLJavaModLoadingContext.get() or ModLoadingContext.get()
     *     }
     * }}
     */
    @Deprecated(forRemoval = true, since = "1.21.1")
    public static FMLJavaModLoadingContext get() {
        return ModLoadingContext.get().extension();
    }
}
