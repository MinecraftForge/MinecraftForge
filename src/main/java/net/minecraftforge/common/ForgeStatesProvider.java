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

package net.minecraftforge.common;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.datafix.ForgeDataFixerEventHandler;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.datafix.ConfigureDatafixSchemaEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ForgeStatesProvider implements IModStateProvider {
    final ModLoadingState SETUP_DFU = ModLoadingState.withTransition("SETUP_DFU", "CONSTRUCT", ModLoadingPhase.GATHER, SerialTransition.forDFU());
    final ModLoadingState CREATE_REGISTRIES = ModLoadingState.withTransition("CREATE_REGISTRIES", "SETUP_DFU", ModLoadingPhase.GATHER, SerialTransition.of(()-> Stream.of(IModStateTransition.EventGenerator.fromFunction(RegistryEvent.NewRegistry::new))));
    final ModLoadingState OBJECT_HOLDERS = ModLoadingState.withInline("OBJECT_HOLDERS", "CREATE_REGISTRIES", ModLoadingPhase.GATHER, ml-> ObjectHolderRegistry.findObjectHolders());
    final ModLoadingState INJECT_CAPABILITIES = ModLoadingState.withInline("INJECT_CAPABILITIES", "OBJECT_HOLDERS", ModLoadingPhase.GATHER, ml-> CapabilityManager.INSTANCE.injectCapabilities(ml.getAllScanData()));
    final ModLoadingState CUSTOM_TAGS = ModLoadingState.withInline("CUSTOM_TAGS", "INJECT_CAPABILITIES", ModLoadingPhase.GATHER, ml-> GameData.setCustomTagTypesFromRegistries());
    final ModLoadingState LOAD_REGISTRIES = ModLoadingState.withTransition("LOAD_REGISTRIES", "CUSTOM_TAGS", ModLoadingPhase.GATHER, new SerialTransition<RegistryEvent.Register<?>>(GameData::generateRegistryEvents, GameData::preRegistryEventDispatch, GameData::postRegistryEventDispatch, GameData::checkForRevertToVanilla));
    final ModLoadingState FREEZE = ModLoadingState.withInline("FREEZE_DATA", "COMPLETE", ModLoadingPhase.COMPLETE, ml->GameData.freezeData());
    final ModLoadingState NETLOCK = ModLoadingState.withInline("NETWORK_LOCK", "FREEZE_DATA", ModLoadingPhase.COMPLETE, ml-> NetworkRegistry.lock());
    @Override
    public List<IModLoadingState> getAllStates() {
        return List.of(SETUP_DFU, CREATE_REGISTRIES, OBJECT_HOLDERS, INJECT_CAPABILITIES, CUSTOM_TAGS, LOAD_REGISTRIES, FREEZE, NETLOCK);
    }

    static record SerialTransition<T extends Event & IModBusEvent>(Supplier<Stream<EventGenerator<?>>> eventStream, BiFunction<Executor, ? extends EventGenerator<T>, CompletableFuture<List<Throwable>>> preDispatchHook, BiFunction<Executor, ? extends EventGenerator<T>, CompletableFuture<List<Throwable>>> postDispatchHook, BiFunction<Executor, CompletableFuture<List<Throwable>>, CompletableFuture<List<Throwable>>> finalActivityGenerator) implements IModStateTransition {
        public static <T extends Event & IModBusEvent> SerialTransition<T> of(Supplier<Stream<EventGenerator<?>>> eventStream) {
            return new SerialTransition<T>(eventStream, (t, f)->CompletableFuture.completedFuture(Collections.emptyList()), (t, f)->CompletableFuture.completedFuture(Collections.emptyList()), (e, prev) ->prev.thenApplyAsync(Function.identity(), e));
        }

        private static SerialTransition<ConfigureDatafixSchemaEvent> forDFU() {
            return new SerialTransition<>(
              ForgeDataFixerEventHandler::generateConfigureEvents,
              ForgeDataFixerEventHandler::preDispatch,
              ForgeDataFixerEventHandler::postDispatch,
              ForgeDataFixerEventHandler::rebuildFixer
            );
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
