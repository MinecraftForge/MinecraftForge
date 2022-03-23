/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.task;

import java.util.function.Consumer;

/**
 * A configurator for configuring synchronous tasks.
 * @param <T> The type of the object that the synchronous task consumes.
 */
public interface ITypedSyncTaskConfigurator<T> extends ISyncTaskConfigurator {

    /**
     * Configure the task to run a synchronous which consumes an object of type {@code T}.
     * @param consumer The executing consumer which consumes the object and runs on the main thread.
     */
    void sync(Consumer<T> consumer);
}
