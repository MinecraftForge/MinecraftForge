/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import com.google.common.base.Predicates;

import java.util.function.Predicate;

/**
 * A property to be used in {@link ModelData}.
 * <p>
 * May optionally validate incoming values.
 *
 * @see ModelData
 */
public class ModelProperty<T> implements Predicate<T>
{
    private final Predicate<T> predicate;

    public ModelProperty()
    {
        this(Predicates.alwaysTrue());
    }

    public ModelProperty(Predicate<T> predicate)
    {
        this.predicate = predicate;
    }

    @Override
    public boolean test(T value)
    {
        return predicate.test(value);
    }
}
