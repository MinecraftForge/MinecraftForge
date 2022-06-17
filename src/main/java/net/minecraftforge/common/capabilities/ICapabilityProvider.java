/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICapabilityProvider
{
    /**
     * Retrieves the {@link Capability} for the capability requested on the specific side.<br>
     * The return value may be the same for multiple faces.<br>
     * Callers should cache the returned instance and not make repeated calls to this method when possible.<br>
     * Contractually, unless invalidated, the returned instance will always be the same for the same arguments.<br>
     *
     * @param type The {@link CapabilityType} to check for.
     * @param direction The {@link Direction} to check from, which may be null.  Null represents "internal".<br>
     * @return The requested an optional holding the requested capability.
     * 
     */
    @NotNull <T> Capability<T> getCapability(@NotNull final CapabilityType<T> type, final @Nullable Direction direction);

    /**
     * Utility for {@link #getCapability(Capability, Direction)} that calls with a null direction.<br>
     * Do not override this method - only override the contextual variant!
     * 
     * @see {@link #getCapability(Capability, Direction)}
     */
    @NotNull default <T> Capability<T> getCapability(@NotNull final CapabilityType<T> type) {
        return getCapability(type, null);
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