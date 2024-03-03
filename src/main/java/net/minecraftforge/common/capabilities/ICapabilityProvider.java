/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

public interface ICapabilityProvider
{
    /**
     * Retrieves the Optional handler for the capability requested on the specific side.
     * The return value <strong>CAN</strong> be the same for multiple faces.
     * Modders are encouraged to cache this value, using the listener capabilities of the Optional to
     * be notified if the requested capability get lost.
     *
     * @param cap The capability to check
     * @param side The Side to check from,
     *   <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
     * @return The requested an optional holding the requested capability.
     */
    @Nonnull <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side);

    /*
     * Purely added as a bouncer to sided version, to make modders stop complaining about calling with a null value.
     * This should never be OVERRIDDEN, modders should only ever implement the sided version.
     */
    @Nonnull default <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap) {
        return getCapability(cap, null);
    }
}
