/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
