/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

/**
 * Proxy object for a value that is calculated on first access
 * @param <T> The type of the value
 */
public final class Lazy<T> implements Supplier<T>
{
    /**
     * Constructs a lazy-initialized object.
     * @param supplier The supplier for the value, to be called the first time the value is needed,
     * or whenever the cache has been invalidated
     */
    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    /**
     * Invalidates the cache, causing the supplier to be called again on the next access.
     */
    public synchronized void invalidate() {
        this.cachedValue = null;
    }

    /**
     * Non-thread-safe implementation.
     */
    private final Supplier<T> delegate;
    @Nullable
    private volatile T cachedValue;

    private Lazy(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    /**
     * Thread-safe implementation.
     */
    @Override
    public T get() {
        T ret = cachedValue;
        if (ret == null) {
            synchronized (this) {
                ret = cachedValue;
                if (ret == null) {
                    cachedValue = ret = delegate.get();
                    if (ret == null) {
                        throw new IllegalStateException("Lazy value cannot be null, but supplier returned null: " + delegate);
                    }
                }
            }
        }
        return ret;
    }
}
