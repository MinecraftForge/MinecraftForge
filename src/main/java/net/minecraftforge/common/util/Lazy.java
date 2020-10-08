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
        private volatile Object lock = new Object();
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
            // Copy the lock to a local variable to prevent NPEs if the lock field is set to null between the
            // null-check and the synchronization
            Object localLock = this.lock;
            if (supplier != null)
            {
                // localLock is not null here because supplier was non-null after we copied the lock and both of them
                // are volatile
                synchronized (localLock)
                {
                    if (supplier != null)
                    {
                        instance = supplier.get();
                        supplier = null;
                        this.lock = null;
                    }
                }
            }
            return instance;
        }
    }
}
