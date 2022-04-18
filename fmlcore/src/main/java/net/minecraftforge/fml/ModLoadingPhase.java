/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

public enum ModLoadingPhase {
    ERROR, // Special state for error handling
    GATHER,
    LOAD,
    COMPLETE,
    DONE;
}
