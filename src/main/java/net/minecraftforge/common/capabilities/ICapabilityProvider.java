/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

/**
 * This interface allows an object to provide capability instances.
 * 
 * @see {@link CapabilityProvider} for the default implementation.
 */
public interface ICapabilityProvider
{
    /**
     * Retrieves the {@link CapabilityInstance} for the capability requested on the specific side.<br>
     * The return value may be the same for multiple faces.<br>
     * Accessors should cache the returned instance and not make repeated calls to this method.<br>
     * Contractually, unless invalidated, the returned instance will always be the same for the same arguments.<br>
     *
     * @param cap The {@link Capability} to check.
     * @param direction The {@link Direction} to check from, which may be null.  Null represents "internal".<br>
     * If this provider is not direction-sensitive, all sides are equivalent, and null is preferred.
     * @return The requested an optional holding the requested capability.
     * 
     * @see {@link #isDirectionSensitive()}
     */
    @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap, final @Nullable Direction direction);

    /**
     * Utility for {@link #getCapability(Capability, Direction)} that calls with a null direction.<br>
     * Do not override this method - only override the contextual variant!
     * 
     * @see {@link #getCapability(Capability, Direction)}
     */
    @NotNull default <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap) {
        return getCapability(cap, null);
    }

    /**
     * Checks if this capability provider is direction-sensitive.<br>
     * The return of this method should be static, i.e. a provider should always or never be direction-sensitive.
     * @return If directions have importance in {@link #getCapability(Capability, Direction)}
     */
    default boolean isDirectionSensitive() { 
    	return true;
    }

    /**
     * Invalidates all contained caps, and prevents {@link #getCapability(Capability, Direction)} from returning a value.<br>
     * This is usually called when the object in question is removed/terminated.<br>
     * However there are be cases these 'invalid' caps need to be retrieved/copied.<br>
     * In that case, call {@link #reviveCaps()}, perform the needed operations, and then call {@link #invalidateCaps()} again.<br>
     * Be sure to make your invalidate callbacks recursion safe.
     */
    void invalidateCaps();

    /**
     * This function will allow {@link #getCapability(Capability, Direction)} to return values again.<br>
     * This can be used to copy caps from one removed provider to a new one.<br>
     * It is expected that all calls to this method are closely followed by a call to {@link #invalidateCaps()}
     */
    void reviveCaps();
}