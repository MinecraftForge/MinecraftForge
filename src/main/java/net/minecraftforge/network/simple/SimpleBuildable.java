/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import net.minecraftforge.network.ChannelBuildable;
import net.minecraftforge.network.SimpleChannel;

public interface SimpleBuildable extends ChannelBuildable<Object> {
    /*
     * This will build the entire channel, locking any future modifications from happening.
     * This should be the LAST call in your builder chain and can only be called once.
     */
    @Override
    SimpleChannel build();
}
