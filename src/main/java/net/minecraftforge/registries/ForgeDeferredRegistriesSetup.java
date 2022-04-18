/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraftforge.eventbus.api.IEventBus;

public class ForgeDeferredRegistriesSetup
{
    private static boolean setup = false;

    /**
     * Internal forge method. Modders do not call.
     */
    public static void setup(IEventBus modEventBus)
    {
        if (setup)
            throw new IllegalStateException("Setup has already been called!");

        ForgeRegistries.DEFERRED_DATA_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_WORLD_TYPES.register(modEventBus);

        setup = true;
    }
}
