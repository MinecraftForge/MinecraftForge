/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.ApiStatus;

public record ToolAction(String name) {
    public static final Codec<ToolAction> CODEC = Codec.STRING.xmap(ToolAction::get, ToolAction::name).stable();
    private static final Map<String, ToolAction> ACTIONS = new ConcurrentHashMap<>();

    /**
     * Returns all registered actions.
     * This collection can be kept around, and will update itself in response to changes to the map.
     * See {@link ConcurrentHashMap#values()} for details.
     */
    public static Collection<ToolAction> getActions() {
        return Collections.unmodifiableCollection(ACTIONS.values());
    }

    /**
     * Gets or creates a new ToolAction for the given name.
     */
    public static ToolAction get(String name) {
        return ACTIONS.computeIfAbsent(name, ToolAction::new);
    }

    /**
     * Returns the name of this tool action
     */
    public String name() {
        return name;
    }

    /** Use {@link #get(String)} to get or create a ToolAction */
    @ApiStatus.Internal
    public ToolAction {}
}
