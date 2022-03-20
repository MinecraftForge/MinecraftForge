/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ForgeStatesProvider implements IModStateProvider {
    final ModLoadingState CREATE_REGISTRIES = ModLoadingState.withTransition("CREATE_REGISTRIES", "CONSTRUCT", ModLoadingPhase.GATHER, new SerialTransition<>(() -> Stream.of(IModStateTransition.EventGenerator.fromFunction(RegistryManager.newRegistryEventGenerator())), RegistryManager::preNewRegistryEvent, RegistryManager::postNewRegistryEvent, (e, prev) -> prev.thenApplyAsync(Function.identity(), e)));
    final ModLoadingState OBJECT_HOLDERS = ModLoadingState.withInline("OBJECT_HOLDERS", "CREATE_REGISTRIES", ModLoadingPhase.GATHER, ml-> ObjectHolderRegistry.findObjectHolders());
    final ModLoadingState INJECT_CAPABILITIES = ModLoadingState.withInline("INJECT_CAPABILITIES", "OBJECT_HOLDERS", ModLoadingPhase.GATHER, ml-> CapabilityManager.INSTANCE.injectCapabilities(ml.getAllScanData()));
    final ModLoadingState UNFREEZE = ModLoadingState.withInline("UNFREEZE_DATA", "INJECT_CAPABILITIES", ModLoadingPhase.GATHER, ml->GameData.unfreezeData());
    final ModLoadingState LOAD_REGISTRIES = ModLoadingState.withTransition("LOAD_REGISTRIES", "UNFREEZE_DATA", ModLoadingPhase.GATHER, new SerialTransition<RegistryEvent.Register<?>>(GameData::generateRegistryEvents, GameData::preRegistryEventDispatch, GameData::postRegistryEventDispatch, GameData::checkForRevertToVanilla));
    final ModLoadingState FREEZE = ModLoadingState.withInline("FREEZE_DATA", "COMPLETE", ModLoadingPhase.COMPLETE, ml->GameData.freezeData());
    final ModLoadingState NETLOCK = ModLoadingState.withInline("NETWORK_LOCK", "FREEZE_DATA", ModLoadingPhase.COMPLETE, ml-> NetworkRegistry.lock());
    @Override
    public List<IModLoadingState> getAllStates() {
        return List.of(CREATE_REGISTRIES, OBJECT_HOLDERS, INJECT_CAPABILITIES, UNFREEZE, LOAD_REGISTRIES, FREEZE, NETLOCK);
    }

    // TODO 1.19: Pass the event instances to the pre and post hooks rather than the event generators
    static record SerialTransition<T extends Event & IModBusEvent>(Supplier<Stream<EventGenerator<?>>> eventStream, BiFunction<Executor, ? extends EventGenerator<T>, CompletableFuture<List<Throwable>>> preDispatchHook, BiFunction<Executor, ? extends EventGenerator<T>, CompletableFuture<List<Throwable>>> postDispatchHook, BiFunction<Executor, CompletableFuture<List<Throwable>>, CompletableFuture<List<Throwable>>> finalActivityGenerator) implements IModStateTransition {
        public static <T extends Event & IModBusEvent> SerialTransition<T> of(Supplier<Stream<EventGenerator<?>>> eventStream) {
            return new SerialTransition<T>(eventStream, (t, f)->CompletableFuture.completedFuture(Collections.emptyList()), (t, f)->CompletableFuture.completedFuture(Collections.emptyList()), (e, prev) ->prev.thenApplyAsync(Function.identity(), e));
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
}
