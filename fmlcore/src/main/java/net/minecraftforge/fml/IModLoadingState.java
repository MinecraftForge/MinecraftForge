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

package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IModLoadingState {
    String name();
    String previous();
    ModLoadingPhase phase();
    Function<ModList, String> message();
    Optional<Consumer<ModList>> inlineRunnable();

    default <T extends Event & IModBusEvent> Optional<CompletableFuture<List<Throwable>>> buildTransition(final Executor syncExecutor, final Executor parallelExecutor) {
        return buildTransition(syncExecutor, parallelExecutor, e->CompletableFuture.runAsync(()->{}, e), e->CompletableFuture.runAsync(()->{}, e));
    }

    <T extends Event & IModBusEvent> Optional<CompletableFuture<List<Throwable>>> buildTransition(Executor syncExecutor, Executor parallelExecutor, Function<Executor, CompletableFuture<Void>> preSyncTask, Function<Executor, CompletableFuture<Void>> postSyncTask);
}
