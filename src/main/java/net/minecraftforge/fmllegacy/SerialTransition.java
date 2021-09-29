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

package net.minecraftforge.fmllegacy;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ThreadSelector;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

record SerialTransition<T extends Event & IModBusEvent>(Supplier<Stream<IModStateTransition.EventGenerator<?>>> eventStream, BiFunction<Executor, ? extends EventGenerator<T>, CompletableFuture<List<Throwable>>> preDispatchHook, BiFunction<Executor, ? extends EventGenerator<T>, CompletableFuture<List<Throwable>>> postDispatchHook, BiFunction<Executor, CompletableFuture<List<Throwable>>, CompletableFuture<List<Throwable>>> finalActivityGenerator) implements IModStateTransition {
    public static <T extends Event & IModBusEvent> SerialTransition<T> of(Supplier<Stream<IModStateTransition.EventGenerator<?>>> eventStream) {
        return new SerialTransition<T>(eventStream, (t,f)->CompletableFuture.completedFuture(Collections.emptyList()), (t, f)->CompletableFuture.completedFuture(Collections.emptyList()), (e, prev) ->prev.thenApplyAsync(Function.identity(), e));
    }
    @Override
    public Supplier<Stream<EventGenerator<?>>> eventFunctionStream() {
        return eventStream;
    }

    @Override
    public ThreadSelector threadSelector() {
        return ThreadSelector.SYNC;
    }

    // These never progress the ModLoadingStage..
    @Override
    public BiFunction<ModLoadingStage, Throwable, ModLoadingStage> nextModLoadingStage() {
        return ModLoadingStage::currentState;
    }
}
