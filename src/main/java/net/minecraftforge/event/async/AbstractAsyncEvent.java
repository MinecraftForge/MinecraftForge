/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.async;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.task.IAsyncTaskConfigurator;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractAsyncEvent extends Event {

    private final Supplier<IAsyncTaskConfigurator> taskConfiguratorSupplier;

    protected AbstractAsyncEvent(Supplier<IAsyncTaskConfigurator> taskConfiguratorSupplier) {
        this.taskConfiguratorSupplier = taskConfiguratorSupplier;
    }

    public void withAsyncTask(Consumer<IAsyncTaskConfigurator> configuratorConsumer) {
        configuratorConsumer.accept(this.taskConfiguratorSupplier.get());
    }
}
