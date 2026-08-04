/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Proxy object for a value that is calculated on first access
 * @param <T> The type of the value
 */
public interface Lazy<T> extends Supplier<T>
{
    /**
     * Constructs a lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> Lazy<T> of(@Nonnull Supplier<T> supplier)
    {
        return new Lazy.Fast<>(supplier);
    }

    /**
     * Constructs a thread-safe lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> Lazy<T> concurrentOf(@Nonnull Supplier<T> supplier)
    {
        return new Lazy.Concurrent<>(supplier);
    }

    /**
     * Non-thread-safe implementation.
     */
    final class Fast<T> implements Lazy<T>
    {
        private Supplier<T> supplier;
        private T instance;

        private Fast(Supplier<T> supplier)
        {
            this.supplier = supplier;
        }

        @Nullable
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
    final class Concurrent<T> implements Lazy<T>
    {
        private static final Object lock = new Object();
        private volatile Supplier<T> supplier;
        private volatile T instance;

        private Concurrent(Supplier<T> supplier)
        {
            this.supplier = supplier;
        }

        @Nullable
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
