/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
