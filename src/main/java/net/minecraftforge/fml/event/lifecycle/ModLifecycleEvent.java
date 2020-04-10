/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModContainer;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Parent type to all ModLifecycle events. This is based on Forge EventBus. They fire through the
 * ModContainer's eventbus instance.
 */
public class ModLifecycleEvent extends Event
{
    private final ModContainer container;

    public ModLifecycleEvent(ModContainer container)
    {
        this.container = container;
    }

    public final String description()
    {
       String cn = getClass().getName();
       return cn.substring(cn.lastIndexOf('.')+1);
    }

    public Stream<InterModComms.IMCMessage> getIMCStream() {
        return InterModComms.getMessages(this.container.getModId());
    }

    public Stream<InterModComms.IMCMessage> getIMCStream(Predicate<String> methodFilter) {
        return InterModComms.getMessages(this.container.getModId(), methodFilter);
    }

    /**
     * Run a task on the loading thread at the next available opportunity, i.e.
     * after the current lifecycle event has completed.
     * <p>
     * If the task must throw a checked exception, use
     * {@link #enqueueSynchronousWork(DeferredWorkQueue.CheckedRunnable)}.
     * <p>
     * If the task has a result, use {@link #enqueueSynchronousWork(Supplier)} or
     * {@link #enqueueSynchronousWork(Callable)}.
     *
     * @param workToEnqueue A {@link Runnable} to execute later, on the loading
     *                      thread
     * @return A {@link CompletableFuture} that completes at said time
     */
    @SuppressWarnings("deprecation")
    public CompletableFuture<Void> enqueueSynchronousWork(Runnable workToEnqueue) {
        return DeferredWorkQueue.runLater(workToEnqueue);
    }

    /**
     * Run a task on the loading thread at the next available opportunity, i.e.
     * after the current lifecycle event has completed. This variant allows the task
     * to throw a checked exception.
     * <p>
     * If the task does not throw a checked exception, use
     * {@link #enqueueSynchronousWork(Runnable)}.
     * <p>
     * If the task has a result, use {@link #enqueueSynchronousWork(Supplier)} or
     * {@link #enqueueSynchronousWork(Callable)}.
     *
     * @param workToEnqueue A {@link DeferredWorkQueue.CheckedRunnable} to execute later, on the
     *                      loading thread
     * @return A {@link CompletableFuture} that completes at said time
     */
    @SuppressWarnings("deprecation")
    public CompletableFuture<Void> enqueueSynchronousWork(DeferredWorkQueue.CheckedRunnable workToEnqueue) { //TODO 1.16: Move CheckRunnable out of deprecated DeferredWorkQueue
        return DeferredWorkQueue.runLaterChecked(workToEnqueue);
    }

    /**
     * Run a task computing a result on the loading thread at the next available
     * opportunity, i.e. after the current lifecycle event has completed.
     * <p>
     * If the task throws a checked exception, use
     * {@link #enqueueSynchronousWork(Callable)}.
     * <p>
     * If the task does not have a result, use {@link #enqueueSynchronousWork(Runnable)} or
     * {@link #enqueueSynchronousWork(DeferredWorkQueue.CheckedRunnable)}.
     *
     * @param               <T> The result type of the task
     * @param workToEnqueue A {@link Supplier} to execute later, on the loading
     *                      thread
     * @return A {@link CompletableFuture} that completes at said time
     */
    @SuppressWarnings("deprecation")
    public <T> CompletableFuture<T> enqueueSynchronousWork(Supplier<T> workToEnqueue) {
        return DeferredWorkQueue.getLater(workToEnqueue);
    }

    /**
     * Run a task computing a result on the loading thread at the next available
     * opportunity, i.e. after the current lifecycle event has completed. This
     * variant allows the task to throw a checked exception.
     * <p>
     * If the task does not throw a checked exception, use
     * {@link #enqueueSynchronousWork(Supplier)}.
     * <p>
     * If the task does not have a result, use {@link #enqueueSynchronousWork(Runnable)} or
     * {@link #enqueueSynchronousWork(DeferredWorkQueue.CheckedRunnable)}.
     *
     * @param               <T> The result type of the task
     * @param workToEnqueue A {@link Callable} to execute later, on the loading
     *                      thread
     * @return A {@link CompletableFuture} that completes at said time
     */
    @SuppressWarnings("deprecation")
    public <T> CompletableFuture<T> enqueueSynchronousWork(Callable<T> workToEnqueue) {
        return DeferredWorkQueue.getLaterChecked(workToEnqueue);
    }

    @Override
    public String toString() {
        return description();
    }
}
