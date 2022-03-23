/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.task;

/**
 * Defines a task configurator which runs a task on the main thread.
 */
public interface ISyncTaskConfigurator {

    /**
     * Configure the code that should be synchronously run on the main thread.
     * @param runnable The code to run on the main thread.
     */
    void sync(Runnable runnable);
}
