/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ClearableLazy<T> extends Lazy<T> {
    void invalidate();

    /**
     * Constructs a lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> ClearableLazy<T> of(@NotNull Supplier<T> supplier) {
        return new ClearableLazy.Fast<T>(supplier);
    }

    /**
     * Constructs a thread-safe lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> ClearableLazy<T> concurrentOf(@NotNull Supplier<T> supplier) {
        return new ClearableLazy.Concurrent<>(supplier);
    }

    /**
     * Non-thread-safe implementation.
     */
    final class Fast<T> implements ClearableLazy<T> {
        private final Supplier<T> supplier;
        private T instance;

        private Fast(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Nullable
        @Override
        public final T get() {
            if (instance == null) {
                instance = supplier.get();
            }
            return instance;
        }

        @Override
        public void invalidate() {
            this.instance = null;
        }
    }

    /**
     * Thread-safe implementation.
     */
    final class Concurrent<T> implements ClearableLazy<T> {
        private final Object lock = new Object();
        private final Supplier<T> supplier;
        private volatile T instance;

        private Concurrent(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Nullable
        @Override
        public final T get()
        {
            var ret = instance;
            if (ret == null) {
                synchronized (lock) {
                    if (instance == null) {
                        return instance = supplier.get();
                    }
                }
            }
            return ret;
        }

        @Override
        public void invalidate() {
            synchronized (lock) {
                this.instance = null;
            }
        }
    }
}
