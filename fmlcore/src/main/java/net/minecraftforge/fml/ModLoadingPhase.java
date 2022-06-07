/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

/**
 * Phases of mod loading, for grouping mod loading states.
 */
public enum ModLoadingPhase {
    /**
     * Special phase for exceptional situations and error handling, where mod loading cannot continue normally.
     *
     * <p>There is conventionally only one state with this phase.</p>
     */
    ERROR, // Special state for error handling
    /**
     * Phase for discovering and gathering mods for loading.
     */
    GATHER,
    /**
     * Phase for the loading of mods found from mod discovery.
     *
     * @see #GATHER
     */
    LOAD,
    /**
     * Phase after mod loading has completed, for post-loading tasks.
     *
     * @see #LOAD
     */
    COMPLETE,
    /**
     * Marker phase for the last state in the full mod loading process.
     */
    DONE
}
