/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmllegacy;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;

import java.util.List;
import java.util.stream.Stream;

/*
        dispatchAndHandleError(ModLoadingStage.CONSTRUCT, syncExecutor, parallelExecutor, periodicTask);
        statusConsumer.ifPresent(c->c.accept("Creating registries"));
        dispatchAndHandleError(ModLoadingStage.CREATE_REGISTRIES, syncExecutor, parallelExecutor, periodicTask);
        ObjectHolderRegistry.findObjectHolders();
        CapabilityManager.INSTANCE.injectCapabilities(modList.getAllScanData());
        statusConsumer.ifPresent(c->c.accept("Adding custom tag types"));
        GameData.setCustomTagTypesFromRegistries();
        statusConsumer.ifPresent(c->c.accept("Populating registries"));
        dispatchAndHandleError(ModLoadingStage.LOAD_REGISTRIES, syncExecutor, parallelExecutor, periodicTask);
        statusConsumer.ifPresent(c->c.accept("Early mod loading complete"));

         statusConsumer.ifPresent(c->c.accept("Freezing data"));
        GameData.freezeData();
        NetworkRegistry.lock();
*/
public class ForgeStatesProvider implements IModStateProvider {
    final ModLoadingState CREATE_REGISTRIES = ModLoadingState.withTransition("CREATE_REGISTRIES", "CONSTRUCT", ModLoadingPhase.GATHER, SerialTransition.of(()-> Stream.of(IModStateTransition.EventGenerator.fromFunction(RegistryEvent.NewRegistry::new))));
    final ModLoadingState OBJECT_HOLDERS = ModLoadingState.withInline("OBJECT_HOLDERS", "CREATE_REGISTRIES", ModLoadingPhase.GATHER, ml-> ObjectHolderRegistry.findObjectHolders());
    final ModLoadingState INJECT_CAPABILITIES = ModLoadingState.withInline("INJECT_CAPABILITIES", "OBJECT_HOLDERS", ModLoadingPhase.GATHER, ml-> CapabilityManager.INSTANCE.injectCapabilities(ml.getAllScanData()));
    final ModLoadingState CUSTOM_TAGS = ModLoadingState.withInline("CUSTOM_TAGS", "INJECT_CAPABILITIES", ModLoadingPhase.GATHER, ml-> GameData.setCustomTagTypesFromRegistries());
    final ModLoadingState LOAD_REGISTRIES = ModLoadingState.withTransition("LOAD_REGISTRIES", "CUSTOM_TAGS", ModLoadingPhase.GATHER, new SerialTransition<RegistryEvent.Register<?>>(GameData::generateRegistryEvents, GameData::preRegistryEventDispatch, GameData::postRegistryEventDispatch, GameData::checkForRevertToVanilla));
    final ModLoadingState FREEZE = ModLoadingState.withInline("FREEZE_DATA", "COMPLETE", ModLoadingPhase.COMPLETE, ml->GameData.freezeData());
    final ModLoadingState NETLOCK = ModLoadingState.withInline("NETWORK_LOCK", "FREEZE_DATA", ModLoadingPhase.COMPLETE, ml-> NetworkRegistry.lock());
    @Override
    public List<IModLoadingState> getAllStates() {
        return List.of(CREATE_REGISTRIES, OBJECT_HOLDERS, INJECT_CAPABILITIES, CUSTOM_TAGS, LOAD_REGISTRIES, FREEZE, NETLOCK);
    }
}
