/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the {@link ICapabilityFactory}'s
 *
 * For Modders:
 *  If you have something you wish to use this for, it's okay for that.
 */
@ApiStatus.Internal
public abstract class CapabilityFactoryHolder<T> {
    public record Result(Map<ResourceLocation, ICapabilityProvider> map, List<Runnable> listeners) {};

    private final Map<ResourceLocation, ICapabilityFactory<T, ?>> factories = new HashMap<>();
    private boolean built = false;

    protected <G> void build(G obj) {
        if (built) return;
        built = true;

        factories.putAll(CapabilityFactoryManager.getInstance().build((Class<T>) obj.getClass(), this));
    }

    protected Result getCapabilities(T obj) {
        Map<ResourceLocation, ICapabilityProvider> providerMap = new HashMap<>();
        List<Runnable> runnables = new ArrayList<>();
        factories.forEach((rl, f) -> {
            CapabilityProviderHolder cph = f.create(obj);
            runnables.add(() -> cph.listener().accept(cph.provider()));
            providerMap.put(rl, cph.provider());

        });
        return new Result(providerMap, runnables);
    }

    public static class AsField<T> extends CapabilityFactoryHolder<T> {}
}
