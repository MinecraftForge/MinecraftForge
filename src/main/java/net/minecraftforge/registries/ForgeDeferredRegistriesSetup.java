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

        ForgeRegistries.DEFERRED_ENTITY_DATA_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_FLUID_TYPES.register(modEventBus);
        ForgeRegistries.DEFERRED_BIOME_MODIFIERS.register(modEventBus);
        ForgeRegistries.DEFERRED_STRUCTURE_MODIFIER_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_STRUCTURE_MODIFIERS.register(modEventBus);
        ForgeRegistries.DEFERRED_HOLDER_SET_TYPES.register(modEventBus);

        setup = true;
    }
}
