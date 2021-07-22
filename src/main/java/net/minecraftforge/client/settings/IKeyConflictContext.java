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

package net.minecraftforge.client.settings;

/**
 * Defines the context that a {@link KeyBinding} is used.
 * Key conflicts occur when a {@link KeyBinding} has the same {@link IKeyConflictContext} and has conflicting modifiers and keyCodes.
 */
public interface IKeyConflictContext {
    /**
     * @return true if conditions are met to activate {@link KeyBinding}s with this context
     */
    boolean isActive();

    /**
     * @return true if the other context can have {@link KeyBinding} conflicts with this one.
     * This will be called on both contexts to check for conflicts.
     */
    boolean conflicts(IKeyConflictContext other);
}
