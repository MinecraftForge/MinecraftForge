/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.util.thread;

import net.minecraftsingularity.fml.LogicalSide;

public final class SidedThreadGroups {
    public static final SidedThreadGroup CLIENT = new SidedThreadGroup(LogicalSide.CLIENT);
    public static final SidedThreadGroup SERVER = new SidedThreadGroup(LogicalSide.SERVER);

    public static SidedThreadGroup get(boolean client) {
        return client ? CLIENT : SERVER;
    }

    private SidedThreadGroups() { }
}
