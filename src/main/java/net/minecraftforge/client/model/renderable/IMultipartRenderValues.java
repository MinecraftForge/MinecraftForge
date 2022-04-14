/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.renderable;

import javax.annotation.Nullable;

/**
 * A standard interface for renderable context values that support providing different values for parts of the model.
 * @param <T> the type of value to be used for each part
 */
public interface IMultipartRenderValues<T>
{
    /**
     * Returns the value for the given part.
     * @param part the name of the part
     * @return the context value for the part, or {@code null}
     */
    @Nullable
    T getPartValues(String part);
}
