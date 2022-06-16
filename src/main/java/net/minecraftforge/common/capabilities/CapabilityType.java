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
 * A Capability Type defines a type of Capability.<br>
 * To retrieve one, use {@link CapabilityManager#get(ResourceLocation)}.
 *
 * @param <T> The supertype of all instances of this capability type.
 */
public class CapabilityType<T>
{
    private final ResourceLocation id;
    private List<Consumer<CapabilityType<T>>> listeners = new ArrayList<>();

    CapabilityType(ResourceLocation id)
    {
        this.id = id;
    }

    /**
     * @return The unique identifier for this capability type.
     */
    public ResourceLocation getId()
    {
        return this.id;
    }

    /**
     * Tests if the target capability type is this one before returning the instance, otherwise returns empty.
     * @param toCheck {@link CapabilityType} being checked against.
     * @param inst The {@link Capability} to return if the type matches.
     * @return The passed {@link Capability} if the type matches, or {@link Capability#empty()}.
     */
    public @NotNull <R> Capability<R> orEmpty(CapabilityType<R> toCheck, Capability<T> inst)
    {
        return this == toCheck ? inst.cast() : Capability.empty();
    }

    /**
     * @return True if this {@link CapabilityType} has been registered via {@link RegisterCapabilitiesEvent}
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
     * @return {@link this}
     */
    public synchronized CapabilityType<T> addListener(Consumer<CapabilityType<T>> listener)
    {
        if (this.isRegistered())
            listener.accept(this);
        else
            this.listeners.add(listener);
        return this;
    }

    /**
     * Internal method called when this {@link CapabilityType} is registered.
     */
    void onRegister()
    {
        var listeners = this.listeners;
        this.listeners = null;
        listeners.forEach(l -> l.accept(this));
    }
}