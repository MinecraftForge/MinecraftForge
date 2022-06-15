/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines an action which produces a sound.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class SoundAction
{
    private static final Map<String, SoundAction> ACTIONS = new ConcurrentHashMap<>();

    /**
     * Gets or creates a new {@code SoundAction} for the given name.
     *
     * @param name the name of the action
     * @return the existing {@code SoundAction}, or a new one if not present
     */
    public static SoundAction get(String name)
    {
        return ACTIONS.computeIfAbsent(name, SoundAction::new);
    }

    private final String name;

    private SoundAction(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the action.
     *
     * @return the name of the action
     */
    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return "SoundAction[" + this.name + "]";
    }
}
