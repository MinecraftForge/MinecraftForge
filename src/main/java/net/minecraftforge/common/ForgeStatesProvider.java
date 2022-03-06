/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.datafix.ForgeDataFixerCustomFixersModStateTransition;
import net.minecraftforge.datafix.ForgeDataFixerSchemaConfigurationModStateTransition;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.List;
import java.util.stream.Stream;

public class ForgeStatesProvider implements IModStateProvider {
    final ModLoadingState CONFIGURE_DFU_SCHEMAS = ModLoadingState.withTransition("CONFIGURE_DFU_SCHEMAS", "CONSTRUCT", ModLoadingPhase.GATHER, ForgeDataFixerSchemaConfigurationModStateTransition.get());
    final ModLoadingState ADD_CUSTOM_DFU_FIXERS = ModLoadingState.withTransition("ADD_CUSTOM_DFU_FIXERS", "CONFIGURE_DFU_SCHEMAS", ModLoadingPhase.GATHER, ForgeDataFixerCustomFixersModStateTransition.get());
    final ModLoadingState CREATE_REGISTRIES     = ModLoadingState.withTransition("CREATE_REGISTRIES", "ADD_CUSTOM_DFU_FIXERS", ModLoadingPhase.GATHER, ForgeSerialModStateTransition.of(()-> Stream.of(IModStateTransition.EventGenerator.fromFunction(RegistryEvent.NewRegistry::new))));
    final ModLoadingState CREATE_REGISTRIES = ModLoadingState.withTransition("CREATE_REGISTRIES", "CONSTRUCT", ModLoadingPhase.GATHER, new SerialTransition<>(() -> Stream.of(IModStateTransition.EventGenerator.fromFunction(RegistryManager.newRegistryEventGenerator())), RegistryManager::preNewRegistryEvent, RegistryManager::postNewRegistryEvent, (e, prev) -> prev.thenApplyAsync(Function.identity(), e)));
    final ModLoadingState OBJECT_HOLDERS = ModLoadingState.withInline("OBJECT_HOLDERS", "CREATE_REGISTRIES", ModLoadingPhase.GATHER, ml-> ObjectHolderRegistry.findObjectHolders());
    final ModLoadingState INJECT_CAPABILITIES = ModLoadingState.withInline("INJECT_CAPABILITIES", "OBJECT_HOLDERS", ModLoadingPhase.GATHER, ml-> CapabilityManager.INSTANCE.injectCapabilities(ml.getAllScanData()));
    final ModLoadingState UNFREEZE = ModLoadingState.withInline("UNFREEZE_DATA", "INJECT_CAPABILITIES", ModLoadingPhase.GATHER, ml->GameData.unfreezeData());
    final ModLoadingState LOAD_REGISTRIES = ModLoadingState.withTransition("LOAD_REGISTRIES", "UNFREEZE_DATA", ModLoadingPhase.GATHER, new ForgeSerialModStateTransition<>(GameData::generateRegistryEvents, GameData::preRegistryEventDispatch, GameData::postRegistryEventDispatch, GameData::checkForRevertToVanilla));
    final ModLoadingState FREEZE = ModLoadingState.withInline("FREEZE_DATA", "COMPLETE", ModLoadingPhase.COMPLETE, ml->GameData.freezeData());
    final ModLoadingState NETLOCK = ModLoadingState.withInline("NETWORK_LOCK", "FREEZE_DATA", ModLoadingPhase.COMPLETE, ml-> NetworkRegistry.lock());
    @Override
    public List<IModLoadingState> getAllStates() {
        return List.of(CONFIGURE_DFU_SCHEMAS, ADD_CUSTOM_DFU_FIXERS, CREATE_REGISTRIES, OBJECT_HOLDERS, INJECT_CAPABILITIES, UNFREEZE, LOAD_REGISTRIES, FREEZE, NETLOCK);
    }
}
