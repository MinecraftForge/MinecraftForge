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

package net.minecraftforge.server.permission.nodes;

import java.util.function.Function;

/**
 * Represents a key that can be used to build a {@link PermissionDynamicContext}.
 *
 * <p>Keys, along with their associated values, can be used to provide additional context for a permission handler
 * in determining whether to grant permission for an actor and a specific node.</p>
 *
 * <p>As an example usage, a dimension context key could be used inside a building permission
 * check to ensure that the actor can build given those constraints.</p>
 */
public record PermissionDynamicContextKey<T>(Class<T> typeToken, String name, Function<T, String> serializer)
{
    public PermissionDynamicContext<T> createContext(T value)
    {
        return new PermissionDynamicContext<>(this, value);
    }
}
