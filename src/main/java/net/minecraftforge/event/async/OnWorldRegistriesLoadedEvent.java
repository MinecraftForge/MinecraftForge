/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.async;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.task.IAsyncTaskConfigurator;

import java.util.function.Supplier;

public class OnWorldRegistriesLoadedEvent extends AbstractAsyncEvent implements IModBusEvent {

    private final RegistryAccess registryAccess;
    private final DynamicOps<JsonElement> registryOps;
    private final ResourceManager resourceManager;

    OnWorldRegistriesLoadedEvent(Supplier<IAsyncTaskConfigurator> taskConfiguratorSupplier, RegistryAccess registryAccess, DynamicOps<JsonElement> registryOps, ResourceManager resourceManager) {
        super(taskConfiguratorSupplier);
        this.registryAccess = registryAccess;
        this.registryOps = registryOps;
        this.resourceManager = resourceManager;
    }

    public RegistryAccess getRegistryAccess() {
        return registryAccess;
    }

    public DynamicOps<JsonElement> getRegistryOps() {
        return registryOps;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
