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

public record ModLoadingState(String name, String previous, Function<ModList, String> message, ModLoadingPhase phase, Optional<Consumer<ModList>> inlineRunnable, Optional<IModStateTransition> transition) implements IModLoadingState {
    @Override
    public <T extends Event & IModBusEvent> Optional<CompletableFuture<List<Throwable>>> buildTransition(final Executor syncExecutor, final Executor parallelExecutor, Function<Executor, CompletableFuture<Void>> preSyncTask, Function<Executor, CompletableFuture<Void>> postSyncTask) {
        return transition.map(t->t.build(syncExecutor, parallelExecutor, preSyncTask, postSyncTask));
    }

    public static ModLoadingState empty(final String name, final String previous, final ModLoadingPhase phase) {
        return new ModLoadingState(name, previous, ml->"", phase, Optional.empty(), Optional.empty());
    }

    public static ModLoadingState withTransition(final String name, final String previous, final ModLoadingPhase phase, IModStateTransition transition) {
        return new ModLoadingState(name, previous, ml->"Processing transition "+name, phase, Optional.empty(), Optional.of(transition));
    }

    public static ModLoadingState withTransition(final String name, final String previous, final Function<ModList, String> message, final ModLoadingPhase phase, IModStateTransition transition) {
        return new ModLoadingState(name, previous, message, phase, Optional.empty(), Optional.of(transition));
    }
    public static ModLoadingState withInline(final String name, final String previous, final ModLoadingPhase phase, Consumer<ModList> inline) {
        return new ModLoadingState(name, previous, ml->"Processing work "+name, phase, Optional.of(inline), Optional.empty());
    }
}