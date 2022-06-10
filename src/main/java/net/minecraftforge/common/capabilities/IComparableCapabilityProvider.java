/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import org.jetbrains.annotations.Nullable;

/**
 * Interface defining the non-public specification for a capability provider.
 *
 * @param <B>
 */
public interface IComparableCapabilityProvider<B extends IComparableCapabilityProvider<B>> extends ICapabilityProvider
{
	
    default boolean areCapsCompatible(IComparableCapabilityProvider<B> other)
    {
        return areCapsCompatible(other.getCapDispatcher());
    }

    default boolean areCapsCompatible(@Nullable CapabilityDispatcher other)
    {
        final CapabilityDispatcher disp = getCapDispatcher();
        if (disp == null)
        {
            if (other == null)
            {
                return true;
            }
            else
            {
                return other.areCompatible(null);
            }
        }
        else
        {
            return disp.areCompatible(other);
        }
    }
    
    CapabilityDispatcher getCapDispatcher();
}
