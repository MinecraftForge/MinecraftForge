/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.hunger;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an action that could add exhaustion to the Player by performing it.
 * This is used in the {@code ExhaustionAdded} event to classify which action was performed when the event was fired.
 * For the vanilla actions, see {@link net.minecraftforge.event.hunger.ExhaustionEvent.ExhaustingActions}.
 * To create your own action, call {@link #get(String)} and supply your action name.
 */
@SuppressWarnings("ClassCanBeRecord")
public class ExhaustingAction
{
    private static final Map<String, ExhaustingAction> actions = new ConcurrentHashMap<>();

    /**
     * Returns all registered actions.
     * This collection can be kept around, and will update itself in response to changes to the map.
     * See {@link ConcurrentHashMap#values()} for details.
     */
    public static Collection<ExhaustingAction> getActions()
    {
        return Collections.unmodifiableCollection(actions.values());
    }

    /**
     * Gets or creates a new ExhaustingAction for the given name.
     */
    public static ExhaustingAction get(String name)
    {
        return actions.computeIfAbsent(name, ExhaustingAction::new);
    }

    /**
     * Returns the name of this exhausting action
     */
    public String name()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "ExhaustingAction[" + name + "]";
    }

    private final String name;

    /**
     * Use {@link #get(String)} to get or create a ExhaustingAction
     */
    private ExhaustingAction(String name)
    {
        this.name = name;
    }
}
