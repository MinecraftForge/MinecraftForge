package net.minecraftforge.common.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/*
 * The purpose of this class is to support invalidation of cached values.
 * By having this class implement Lazy, we can use methods from Lazy
 * without having to change Lazy to a class in order to support the
 * same functionality
 */

public final class Lazier<T> implements Lazy<T> {
    private final Supplier<T> delegate;
    @Nullable
    private volatile T cachedValue;

    private Lazier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    public static <T> Lazier<T> of(Supplier<T> supplier) {
        return new Lazier<>(supplier);
    }

    public synchronized void invalidate() {
        this.cachedValue = null;
    }

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
