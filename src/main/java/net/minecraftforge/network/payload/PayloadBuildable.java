/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.payload;

import net.minecraftforge.network.PayloadChannel;

public interface PayloadBuildable {
    /*
     * This will build the entire channel, locking any future modifications from happening.
     * This should be the LAST call in your builder chain and can only be called once.
     */
    PayloadChannel build();
}
