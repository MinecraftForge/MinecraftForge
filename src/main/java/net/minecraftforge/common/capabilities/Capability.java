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

package net.minecraftforge.common.capabilities;

import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

/**
 * This is the core holder object Capabilities.
 * Each capability will have ONE instance of this class,
 * and it will the the one passed into the ICapabilityProvider functions.
 *
 * The CapabilityManager is in charge of creating this class.
 */
public class Capability<T>
{
    /**
     * @return The unique name of this capability, typically this is
     * the fully qualified class name for the target interface.
     */
    public String getName() { return name; }

    public @Nonnull <R> LazyOptional<R> orEmpty(Capability<R> toCheck, LazyOptional<T> inst)
    {
        return this == toCheck ? inst.cast() : LazyOptional.empty();
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
}
