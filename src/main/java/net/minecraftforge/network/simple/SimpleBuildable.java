/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.simple;

import net.minecraftsingularity.network.ChannelBuildable;
import net.minecraftsingularity.network.SimpleChannel;

public interface SimpleBuildable extends ChannelBuildable<Object> {
    /*
     * This will build the entire channel, locking any future modifications from happening.
     * This should be the LAST call in your builder chain and can only be called once.
     */
    @Override
    SimpleChannel build();
}
