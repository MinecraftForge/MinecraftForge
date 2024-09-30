/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Use this to register {@link ICapabilityProvider}'s to {@link CapabilityProviderHolder}
 */
public class CapabilityFactoryRegisterEvent extends Event {
    final Map<Class<?>, Map<ResourceLocation, ICapabilityFactory<?, ?>>> factory = new HashMap<>();
    final Map<CapabilityFactoryHolder<?>, Map<ResourceLocation, ICapabilityFactory<?, ?>>> factory_holder = new HashMap<>();

    public CapabilityFactoryRegisterEvent() {}

    /**
     * Used to register {@link ICapabilityProvider}'s for all objects that extend gClass
     */
    public <G, H extends ICapabilityProvider> void register(Class<G> gClass, ResourceLocation resourceLocation, ICapabilityFactory<G, H> factory) {
        if (this.factory.computeIfAbsent(gClass, k -> new HashMap<>()).put(resourceLocation, factory) != null) {
            throw new IllegalStateException("Duplicate CapabilityFactory registered for class %s with id of %s".formatted(gClass, resourceLocation));
        }
    }

    /**
     * Used to register {@link ICapabilityProvider}'s for only the provided {@link CapabilityFactoryHolder}
     *
     * When registering it to EntityType.PIG, it will mean that the PIG will have your CapabilityProvider.
     *
     * Modded Entities that extend {@link net.minecraft.world.entity.animal.Pig} will not have your CcpabilityProvider.
     */
    public <G, H extends ICapabilityProvider> void register(CapabilityFactoryHolder<G> holder, ResourceLocation resourceLocation, ICapabilityFactory<G, H> factory) {
        if (this.factory_holder.computeIfAbsent(holder, k -> new HashMap<>()).put(resourceLocation, factory) != null) {
            throw new IllegalStateException("Duplicate CapabilityFactory registered for holder %s with id of %s".formatted(holder, resourceLocation));
        }
    }

}
