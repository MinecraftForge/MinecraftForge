/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.util.thread;

import net.minecraftforge.fml.LogicalSide;

public final class SidedThreadGroups
{
    public static final SidedThreadGroup CLIENT = new SidedThreadGroup(LogicalSide.CLIENT);
    public static final SidedThreadGroup SERVER = new SidedThreadGroup(LogicalSide.SERVER);

    private SidedThreadGroups() {
    }
}
