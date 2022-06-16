/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This is the core holder object Capabilities.
 * Each capability will have ONE instance of this class,
 * and it will the the one passed into the ICapabilityProvider functions.
 *
 * The CapabilityManager is in charge of creating this class.
 */
public class CapabilityType<T>
{
    /**
     * @return The unique name of this capability, typically this is
     * the fully qualified class name for the target interface.
     */
    public ResourceLocation getId() { return this.id; }

    public @NotNull <R> Capability<R> orEmpty(CapabilityType<R> toCheck, Capability<T> inst)
    {
        return this == toCheck ? inst.cast() : Capability.empty();
    }

    /**
     * @return true if something has registered this capability to the Manager.
     *   This is a marker that the class for this capability exists, and can be used.
     */
    public boolean isRegistered()
    {
        return this.listeners == null;
    }

    /**
     * Adds a listener to be called when someone registers this capability.
     * May be called instantly if this is already registered.
     *
     * @param listener Function to fire when capability is registered.
     * @return self, in case people want to use builder pattern.
     */
    public synchronized CapabilityType<T> addListener(Consumer<CapabilityType<T>> listener)
    {
        if (this.isRegistered())
            listener.accept(this);
        else
            this.listeners.add(listener);
        return this;
    }

    // INTERNAL
    private final ResourceLocation id;
    List<Consumer<CapabilityType<T>>> listeners = new ArrayList<>();

    CapabilityType(ResourceLocation id)
    {
        this.id = id;
    }

    void onRegister()
    {
        var listeners = this.listeners;
        this.listeners = null;
        listeners.forEach(l -> l.accept(this));
    }
}
