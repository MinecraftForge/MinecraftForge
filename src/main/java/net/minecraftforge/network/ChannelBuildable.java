/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

public interface ChannelBuildable<T> {
    /*
     * This will build the entire channel, locking any future modifications from happening.
     * This should be the LAST call in your builder chain and can only be called once.
     */
    Channel<T> build();
}
