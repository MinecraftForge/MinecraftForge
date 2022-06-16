/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import org.jetbrains.annotations.Nullable;

public interface IComparableCapabilityProvider<B extends IComparableCapabilityProvider<B>> extends ICapabilityProvider
{
	
    default boolean areCapsCompatible(IComparableCapabilityProvider<B> other)
    {
        return areCapsCompatible(other.getCapDispatcher());
    }

    default boolean areCapsCompatible(@Nullable CapabilityDispatcher<B> other)
    {
        var disp = getCapDispatcher();
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
    
    @Nullable CapabilityDispatcher<B> getCapDispatcher(); // Should be lazy
}