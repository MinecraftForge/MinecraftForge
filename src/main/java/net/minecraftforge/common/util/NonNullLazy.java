/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Proxy object for a value that is calculated on first access.
 * Same as {@link Lazy}, but with a nonnull contract.
 * @param <T> The type of the value
 */
public interface NonNullLazy<T> extends NonNullSupplier<T>
{
    /**
     * Constructs a lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> NonNullLazy<T> of(@Nonnull NonNullSupplier<T> supplier)
    {
        Lazy<T> lazy = Lazy.of(supplier::get);
        return () -> Objects.requireNonNull(lazy.get());
    }

    /**
     * Constructs a thread-safe lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> NonNullLazy<T> concurrentOf(@Nonnull NonNullSupplier<T> supplier)
    {
        Lazy<T> lazy = Lazy.concurrentOf(supplier::get);
        return () -> Objects.requireNonNull(lazy.get());
    }
}
