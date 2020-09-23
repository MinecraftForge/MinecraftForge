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

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModContainer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ParallelDispatchEvent extends ModLifecycleEvent {
    public ParallelDispatchEvent(final ModContainer container) {
        super(container);
    }

    private Optional<DeferredWorkQueue> getQueue() {
        return DeferredWorkQueue.lookup(Optional.of(getClass()));
    }

    public CompletableFuture<Void> enqueueWork(Runnable work) {
        return getQueue().map(q->q.enqueueWork(getContainer().getModInfo(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
    }

    public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
        return getQueue().map(q->q.enqueueWork(getContainer().getModInfo(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
    }
}