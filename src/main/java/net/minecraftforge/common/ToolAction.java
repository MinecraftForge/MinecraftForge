/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ClassCanBeRecord")
public class ToolAction
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
