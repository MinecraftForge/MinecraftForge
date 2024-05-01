/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.settings;

import net.minecraft.client.KeyMapping;

/**
 * Defines the context that a {@link KeyMapping} is used.
 * Key conflicts occur when a {@link KeyMapping} has the same {@link IKeyConflictContext} and has conflicting modifiers and keyCodes.
 */
public interface IKeyConflictContext {
    /**
     * @return true if conditions are met to activate {@link KeyMapping}s with this context
     */
    boolean isActive();

    /**
     * @return true if the other context can have {@link KeyMapping} conflicts with this one.
     * This will be called on both contexts to check for conflicts.
     */
    boolean conflicts(IKeyConflictContext other);
}
