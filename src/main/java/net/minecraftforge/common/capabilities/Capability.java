/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.common.util.LazyOptional;

/**
 * This is the core holder object for Capabilities.<br>
 * Each cap will only have one {@link Capability} instance.<br>
 * The instance should only be used at runtime if it returns true to {@link Capability#isRegistered()}.
 *
 * @see {@link CapabilityManager#get(CapabilityToken)}
 */
public class Capability<T>
{
    /**
     * @return The unique name of this capability, typically this is
     * the fully qualified class name for the target interface.
     */
    public String getName() { return name; }

    public @NotNull <R> LazyOptional<R> orEmpty(Capability<R> toCheck, LazyOptional<T> inst)
    {
        return this == toCheck ? inst.cast() : LazyOptional.empty();
    }

    /**
     * This is a marker that the class for this capability exists, and can be used.
     * @return true if something has registered this capability to the Manager.
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
    public synchronized Capability<T> addListener(Consumer<Capability<T>> listener)
    {
        if (this.isRegistered())
            listener.accept(this);
        else
            this.listeners.add(listener);
        return this;
    }

    // INTERNAL
    private final String name;
    private int id;
    List<Consumer<Capability<T>>> listeners = new ArrayList<>();

    Capability(String name)
    {
        this.name = name;
    }

    void onRegister()
    {
        var listeners = this.listeners;
        this.listeners = null;
        listeners.forEach(l -> l.accept(this));
    }
    
    void setId(int id) {
    	this.id = id;
    }
    
    public int getId() {
    	return this.id;
    }
}
