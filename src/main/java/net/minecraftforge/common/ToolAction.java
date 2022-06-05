/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ClassCanBeRecord")
public final class ToolAction
{
    private static final Map<String, ToolAction> actions = new ConcurrentHashMap<>();

    /**
     * Returns all registered actions.
     * This collection can be kept around, and will update itself in response to changes to the map.
     * See {@link ConcurrentHashMap#values()} for details.
     */
    public static Collection<ToolAction> getActions()
    {
        return Collections.unmodifiableCollection(actions.values());
    }

    /**
     * Gets or creates a new ToolAction for the given name.
     */
    public static ToolAction get(String name)
    {
        return actions.computeIfAbsent(name, ToolAction::new);
    }

    /**
     * Returns the name of this tool action
     */
    public String name()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "ToolAction[" + name + "]";
    }

    private final String name;

    /**
     * Use {@link #get(String)} to get or create a ToolAction
     */
    private ToolAction(String name)
    {
        this.name = name;
    }
}
