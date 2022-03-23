/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.task;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * A task configurator which can be used to configure task code which needs to run asynchronously first.
 */
public interface IAsyncTaskConfigurator extends ISyncTaskConfigurator {

    /**
     * Configures the task system to run code first asynchronously and then potentially append a synchronous section.
     *
     * @param code The code to run asynchronously.
     * @return The task configurator for configuring synchronous tasks.
     */
    ISyncTaskConfigurator async(Runnable code);

    /**
     * Configures the task system to run code first asynchronously and have it supply the input for the potentially running synchronous section.
     * @param code The code to run asynchronously.
     * @param <T> The type of the object returned by the asynchronous code which is then potentially passed to the synchronous code.
     * @return
     */
    <T> ITypedSyncTaskConfigurator<T> async(Supplier<T> code);
}
