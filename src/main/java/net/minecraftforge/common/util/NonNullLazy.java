/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

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
        return new NonNullLazy.Fast<>(supplier);
    }

    /**
     * Constructs a thread-safe lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> NonNullLazy<T> concurrentOf(@Nonnull NonNullSupplier<T> supplier)
    {
        return new NonNullLazy.Concurrent<>(supplier);
    }

    /**
     * Non-thread-safe implementation.
     */
    final class Fast<T> implements NonNullLazy<T>
    {
        private NonNullSupplier<T> supplier;
        private T instance;

        private Fast(NonNullSupplier<T> supplier)
        {
            this.supplier = supplier;
        }

        @Nonnull
        @Override
        public final T get()
        {
            if (supplier != null)
            {
                instance = supplier.get();
                supplier = null;
            }
            return instance;
        }
    }

    /**
     * Thread-safe implementation.
     */
    final class Concurrent<T> implements NonNullLazy<T>
    {
        private static final Object lock = new Object();
        private volatile NonNullSupplier<T> supplier;
        private volatile T instance;

        private Concurrent(NonNullSupplier<T> supplier)
        {
            this.supplier = supplier;
        }

        @Nonnull
        @Override
        public final T get()
        {
            if (supplier != null)
            {
                synchronized (lock)
                {
                    if (supplier != null)
                    {
                        instance = supplier.get();
                        supplier = null;
                    }
                }
            }
            return instance;
        }
    }
}
