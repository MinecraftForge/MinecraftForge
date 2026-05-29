/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.registries;

import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class singularityDeferredRegistriesSetup {
    private static boolean setup = false;

    /**
     * Internal singularity method. Modders do not call.
     */
    public static void setup(BusGroup modBusGroup) {
        synchronized (singularityDeferredRegistriesSetup.class) {
            if (setup)
                throw new IllegalStateException("Setup has already been called!");

            setup = true;
        }

        for (var reg : singularityRegistries.registries)
            reg.register(modBusGroup);
    }
}
