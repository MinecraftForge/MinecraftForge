/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Proxy object for a value that is calculated on first access
 * @param <T> The type of the value
 */
public sealed interface LazyFunction<T, R> extends Function<T, R> permits LazyFunction.Fast, LazyFunction.Concurrent {
    /**
     * Constructs a lazy-initialized object
     * @param function The function for the value, to be called the first time the value is needed.
     */
    static <T, R> LazyFunction<T, R> of(@NotNull Function<T, R> function) {
        return new LazyFunction.Fast<>(function);
    }

    /**
     * Constructs a thread-safe lazy-initialized object
     * @param function The function for the value, to be called the first time the value is needed.
     */
    static <T, R> LazyFunction<T, R> concurrentOf(@NotNull Function<T, R> function) {
        return new LazyFunction.Concurrent<>(function);
    }

    R get() throws IllegalStateException;

    /** Non-thread-safe implementation. */
    final class Fast<T, R> implements LazyFunction<T, R> {
        private Function<T, R> function;
        private R instance;

        private Fast(Function<T, R> function) {
            this.function = function;
        }

        @Override
        public @Nullable R apply(T t) {
            if (function != null) {
                instance = function.apply(t);
                function = null;
            }

            return instance;
        }

        @Override
        public R get() throws IllegalStateException {
            if (function != null)
                throw new IllegalStateException("Cannot call get() before apply()");

            return instance;
        }
    }

    /** Thread-safe implementation. */
    final class Concurrent<T, R> implements LazyFunction<T, R> {
        private volatile Object lock = new Object();
        private volatile Function<T, R> function;
        private volatile R instance;

        private Concurrent(Function<T, R> function)
        {
            this.function = function;
        }

        @Override
        public @Nullable R apply(T t) {
            // Copy the lock to a local variable to prevent NPEs if the lock field is set to null between the
            // null-check and the synchronization
            Object localLock = this.lock;
            if (function != null)
            {
                // localLock is not null here because supplier was non-null after we copied the lock and both of them
                // are volatile
                synchronized (localLock)
                {
                    if (function != null)
                    {
                        instance = function.apply(t);
                        function = null;
                        this.lock = null;
                    }
                }
            }
            return instance;
        }

        @Override
        public R get() throws IllegalStateException {
            if (function != null)
                throw new IllegalStateException("Cannot call get() before apply()");

            return instance;
        }
    }
}
