/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ForgeDeferredRegistriesSetup {
    private static boolean setup = false;

    /**
     * Internal forge method. Modders do not call.
     */
    public static void setup(BusGroup modBusGroup) {
        synchronized (ForgeDeferredRegistriesSetup.class) {
            if (setup)
                throw new IllegalStateException("Setup has already been called!");

            setup = true;
        }

        for (var reg : ForgeRegistries.registries)
            reg.register(modBusGroup);
    }
}
