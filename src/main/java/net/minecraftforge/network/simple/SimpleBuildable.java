/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import net.minecraftforge.network.SimpleChannel;

public interface SimpleBuildable<T> {
    /**
     * Prevents any further packets from being registered to this builder
     */
    T build();


    /**
     * Calls the build function all the way down the stack to the root
     */
    SimpleChannel buildAll();
}
