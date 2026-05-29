/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.util.thread;

import net.minecraftsingularity.fml.LogicalSide;

/**
 * Attempts to determine the effect side of the current thread, based on know threads in networking.
 * This is not guaranteed to be correct, and modders should use other methods to determine the logical side when possible.
 */
public class EffectiveSide {
    public static LogicalSide get() {
        final ThreadGroup group = Thread.currentThread().getThreadGroup();
        return group instanceof SidedThreadGroup ? ((SidedThreadGroup) group).getSide() : LogicalSide.CLIENT;
    }
}
