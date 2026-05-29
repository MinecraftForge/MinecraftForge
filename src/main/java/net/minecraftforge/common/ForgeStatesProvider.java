/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common;

import java.util.List;
import net.minecraftsingularity.common.capabilities.CapabilityManager;
import net.minecraftsingularity.fml.IModLoadingState;
import net.minecraftsingularity.fml.IModStateProvider;
import net.minecraftsingularity.fml.ModLoadingPhase;
import net.minecraftsingularity.fml.ModLoadingState;
import net.minecraftsingularity.fml.core.ModStateProvider;
import net.minecraftsingularity.network.NetworkRegistry;
import net.minecraftsingularity.registries.GameData;
import net.minecraftsingularity.registries.RegistryManager;

public class singularityStatesProvider implements IModStateProvider {
    public static final ModLoadingState CREATE_REGISTRIES = gather("CREATE_REGISTRIES", ModStateProvider.CONSTRUCT, RegistryManager::postNewRegistryEvent);
    public static final ModLoadingState INJECT_CAPABILITIES = gather("INJECT_CAPABILITIES", CREATE_REGISTRIES).withInline(CapabilityManager::injectCapabilities);
    public static final ModLoadingState UNFREEZE_DATA = gather("UNFREEZE_DATA", INJECT_CAPABILITIES, GameData::unfreezeData);
    public static final ModLoadingState LOAD_REGISTRIES = gather("LOAD_REGISTRIES", UNFREEZE_DATA, GameData::postRegisterEvents);
    public static final ModLoadingState FREEZE_DATA = complete("FREEZE_DATA", ModStateProvider.COMPLETE, GameData::freezeData);
    public static final ModLoadingState NETWORK_LOCK = complete("NETWORK_LOCK", FREEZE_DATA, NetworkRegistry::lock);

    private static ModLoadingState.Builder gather(String name, ModLoadingState after) {
        return ModLoadingState.of(name, ModLoadingPhase.GATHER).after(after);
    }

    private static ModLoadingState gather(String name, ModLoadingState after, Runnable inline) {
        return gather(name, after).withInline(inline);
    }

    private static ModLoadingState.Builder complete(String name, ModLoadingState after) {
        return ModLoadingState.of(name, ModLoadingPhase.COMPLETE).after(after);
    }

    private static ModLoadingState complete(String name, ModLoadingState after, Runnable inline) {
        return complete(name, after).withInline(inline);
    }

    @Override
    public List<IModLoadingState> getAllStates() {
        return List.of(CREATE_REGISTRIES, INJECT_CAPABILITIES, UNFREEZE_DATA, LOAD_REGISTRIES, FREEZE_DATA, NETWORK_LOCK);
    }
}
