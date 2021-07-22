/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml.core;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ThreadSelector;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

record ParallelTransition(ModLoadingStage stage, Class<? extends ParallelDispatchEvent> event) implements IModStateTransition {
    @Override
    public Supplier<Stream<EventGenerator<?>>> eventFunctionStream() {
        return () -> Stream.of(IModStateTransition.EventGenerator.fromFunction(LamdbaExceptionUtils.rethrowFunction((ModContainer mc) -> event.getConstructor(ModContainer.class, ModLoadingStage.class).newInstance(mc, stage))));
    }

    @Override
    public ThreadSelector threadSelector() {
        return ThreadSelector.PARALLEL;
    }

    @Override
    public BiFunction<Executor, CompletableFuture<List<Throwable>>, CompletableFuture<List<Throwable>>> finalActivityGenerator() {
        return (e, prev) -> prev.thenApplyAsync(t -> {
            stage.getDeferredWorkQueue().runTasks();
            return t;
        });
    }

    @Override
    public BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<List<Throwable>>> preDispatchHook() {
        return (t, f) -> CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<List<Throwable>>> postDispatchHook() {
        return (t, f) -> CompletableFuture.completedFuture(Collections.emptyList());
    }
}
